package com.wechat.acquisition.infrastructure.wechat;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 企业微信 API 客户端
 * 实现加好友、发消息等功能
 */
@Slf4j
@Component
public class WeChatApiClient {
    
    @Value("${wechat.corp-id:}")
    private String corpId;
    
    @Value("${wechat.agent-id:}")
    private String agentId;
    
    @Value("${wechat.secret:}")
    private String secret;
    
    private static final String BASE_URL = "https://qyapi.weixin.qq.com/cgi-bin";
    private final ConcurrentHashMap<String, AccessToken> tokenCache = new ConcurrentHashMap<>();
    
    /**
     * 获取访问令牌
     */
    public String getAccessToken() {
        String cacheKey = corpId + "_" + agentId;
        
        // 检查缓存
        AccessToken cached = tokenCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return cached.getToken();
        }
        
        try {
            String url = String.format("%s/gettoken?corpid=%s&corpsecret=%s", 
                BASE_URL, corpId, secret);
            
            String response = HttpRequest.get(url)
                .timeout(5000)
                .execute()
                .body();
            
            JSONObject json = JSONUtil.parseObj(response);
            int errCode = json.getInt("errcode", 0);
            
            if (errCode != 0) {
                log.error("获取 access_token 失败：{}", json.getStr("errmsg"));
                throw new WeChatApiException("获取 access_token 失败：" + json.getStr("errmsg"));
            }
            
            String accessToken = json.getStr("access_token");
            int expiresIn = json.getInt("expires_in", 7200);
            
            // 缓存 token
            AccessToken tokenObj = new AccessToken(accessToken, System.currentTimeMillis() + expiresIn * 1000L);
            tokenCache.put(cacheKey, tokenObj);
            
            log.info("获取 access_token 成功，过期时间：{}s", expiresIn);
            return accessToken;
            
        } catch (Exception e) {
            log.error("获取 access_token 异常", e);
            throw new WeChatApiException("获取 access_token 异常：" + e.getMessage(), e);
        }
    }
    
    /**
     * 添加企业微信好友
     */
    public AddFriendResult addFriend(String phoneNumber, String addMessage) {
        log.info("添加企微好友：{}, 申请语：{}", phoneNumber, addMessage);
        
        try {
            String accessToken = getAccessToken();
            String url = String.format("%s/externalcontact/add_contact_friend?access_token=%s", 
                BASE_URL, accessToken);
            
            JSONObject body = new JSONObject();
            body.set("name", "获客助手");
            body.set("mobile", phoneNumber);
            body.set("email", "");
            
            // 欢迎语
            if (addMessage != null && !addMessage.isEmpty()) {
                JSONObject welcome = new JSONObject();
                welcome.set("type", "text");
                JSONObject text = new JSONObject();
                text.set("content", addMessage);
                welcome.set("text", text);
                body.set("welcome_msg", welcome);
            }
            
            String response = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .body(body.toString())
                .timeout(10000)
                .execute()
                .body();
            
            JSONObject json = JSONUtil.parseObj(response);
            int errCode = json.getInt("errcode", 0);
            
            if (errCode != 0) {
                log.warn("添加好友失败：{}, 手机号：{}", json.getStr("errmsg"), phoneNumber);
                return AddFriendResult.failed(json.getStr("errmsg"));
            }
            
            String userId = json.getStr("user_id");
            log.info("添加好友成功：{}, userId: {}", phoneNumber, userId);
            
            return AddFriendResult.success(userId);
            
        } catch (Exception e) {
            log.error("添加好友异常：{}", phoneNumber, e);
            return AddFriendResult.failed(e.getMessage());
        }
    }
    
    /**
     * 批量添加好友
     */
    public BatchAddFriendResult batchAddFriends(java.util.List<String> phoneNumbers, String addMessage) {
        log.info("批量添加好友，数量：{}", phoneNumbers.size());
        
        int success = 0;
        int fail = 0;
        java.util.List<String> failedPhones = new java.util.ArrayList<>();
        
        for (String phone : phoneNumbers) {
            AddFriendResult result = addFriend(phone, addMessage);
            if (result.isSuccess()) {
                success++;
            } else {
                fail++;
                failedPhones.add(phone);
            }
            
            // 频率控制 (防封号)
            try {
                Thread.sleep(2000); // 2 秒间隔
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        return new BatchAddFriendResult(success, fail, failedPhones);
    }
    
    /**
     * 发送消息给用户
     */
    public String sendMessage(String userId, String content, String msgType) {
        log.info("发送消息给用户：{}, 类型：{}", userId, msgType);
        
        try {
            String accessToken = getAccessToken();
            String url = String.format("%s/message/send?access_token=%s", BASE_URL, accessToken);
            
            JSONObject body = new JSONObject();
            body.set("touser", userId);
            body.set("msgtype", msgType);
            body.set("agentid", Integer.parseInt(agentId));
            
            if ("text".equals(msgType)) {
                JSONObject text = new JSONObject();
                text.set("content", content);
                body.set("text", text);
            } else if ("markdown".equals(msgType)) {
                JSONObject markdown = new JSONObject();
                markdown.set("content", content);
                body.set("markdown", markdown);
            }
            
            String response = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .body(body.toString())
                .timeout(10000)
                .execute()
                .body();
            
            JSONObject json = JSONUtil.parseObj(response);
            int errCode = json.getInt("errcode", 0);
            
            if (errCode != 0) {
                log.error("发送消息失败：{}", json.getStr("errmsg"));
                throw new WeChatApiException("发送消息失败：" + json.getStr("errmsg"));
            }
            
            log.info("发送消息成功：{}", userId);
            return json.getStr("msgid");
            
        } catch (Exception e) {
            log.error("发送消息异常", e);
            throw new WeChatApiException("发送消息异常：" + e.getMessage(), e);
        }
    }
    
    /**
     * Access Token 缓存对象
     */
    private static class AccessToken {
        private final String token;
        private final long expireTime;
        
        public AccessToken(String token, long expireTime) {
            this.token = token;
            this.expireTime = expireTime;
        }
        
        public String getToken() { return token; }
        
        public boolean isExpired() {
            return System.currentTimeMillis() >= expireTime;
        }
    }
    
    /**
     * 添加好友结果
     */
    @lombok.Data
    @lombok.Builder
    public static class AddFriendResult {
        private boolean success;
        private String userId;
        private String errorMsg;
        
        public static AddFriendResult success(String userId) {
            return AddFriendResult.builder()
                .success(true)
                .userId(userId)
                .build();
        }
        
        public static AddFriendResult failed(String errorMsg) {
            return AddFriendResult.builder()
                .success(false)
                .errorMsg(errorMsg)
                .build();
        }
    }
    
    /**
     * 批量添加好友结果
     */
    public record BatchAddFriendResult(
        int successCount,
        int failCount,
        java.util.List<String> failedPhones
    ) {}
    
    /**
     * API 异常
     */
    public static class WeChatApiException extends RuntimeException {
        public WeChatApiException(String message) { super(message); }
        public WeChatApiException(String message, Throwable cause) { super(message, cause); }
    }
}
