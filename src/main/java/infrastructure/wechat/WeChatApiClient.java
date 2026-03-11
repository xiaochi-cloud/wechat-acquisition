package com.wechat.acquisition.infrastructure.wechat;

import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.json.JSONArray;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Transient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;

/**
 * 企业微信 API 客户端
 */
@Component
@Data
@Builder
@Slf4j
public class WeChatApiClient {
    
    private static final Logger log = LoggerFactory.getLogger(WeChatApiClient.class);
    
    @Value("${wechat.corp-id:}")
    private String corpId;
    
    @Value("${wechat.agent-id:}")
    private String agentId;
    
    @Value("${wechat.secret:}")
    private String secret;
    
    @Value("${wechat.token:}")
    private String token;
    
    @Transient
    private final ConcurrentHashMap<String, AccessToken> tokenCache = new ConcurrentHashMap<>();
    
    private static final String BASE_URL = "https://qyapi.weixin.qq.com/cgi-bin";
    private static final long TOKEN_EXPIRE_SECONDS = 7200;
    
    public String getAccessToken() {
        String cacheKey = corpId + "_" + agentId;
        AccessToken cached = tokenCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return cached.getToken();
        }
        try {
            String url = String.format("%s/gettoken?corpid=%s&corpsecret=%s", BASE_URL, corpId, secret);
            String response = HttpRequest.get(url).timeout(5000).execute().body();
            JSONObject json = JSONUtil.parseObj(response);
            int errCode = json.getInt("errcode", 0);
            if (errCode != 0) {
                log.error("获取 access_token 失败：{}", json.getStr("errmsg"));
                throw new WeChatApiException("获取 access_token 失败：" + json.getStr("errmsg"));
            }
            String accessToken = json.getStr("access_token");
            int expiresIn = json.getInt("expires_in", (int) TOKEN_EXPIRE_SECONDS);
            tokenCache.put(cacheKey, new AccessToken(accessToken, System.currentTimeMillis() + expiresIn * 1000L));
            log.info("获取 access_token 成功，过期时间：{}s", expiresIn);
            return accessToken;
        } catch (Exception e) {
            log.error("获取 access_token 异常", e);
            throw new WeChatApiException("获取 access_token 异常：" + e.getMessage(), e);
        }
    }
    
    public AddFriendResult addFriend(String phoneNumber, String addMessage) {
        log.info("添加企微好友：{}, 申请语：{}", phoneNumber, addMessage);
        try {
            String accessToken = getAccessToken();
            String url = String.format("%s/externalcontact/add_contact_friend?access_token=%s", BASE_URL, accessToken);
            JSONObject body = new JSONObject();
            body.set("name", "获客助手");
            body.set("mobile", phoneNumber);
            body.set("email", "");
            JSONArray externals = new JSONArray();
            JSONObject external = new JSONObject();
            external.set("type", "mobile");
            external.set("attr", phoneNumber);
            externals.add(external);
            body.set("external_contact", external);
            if (addMessage != null && !addMessage.isEmpty()) {
                JSONObject welcome = new JSONObject();
                welcome.set("type", "text");
                welcome.set("text", new JSONObject().set("content", addMessage));
                body.set("welcome_msg", welcome);
            }
            String response = HttpRequest.post(url).header("Content-Type", "application/json").body(body.toString()).timeout(10000).execute().body();
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
    
    public BatchAddFriendResult batchAddFriends(List<String> phoneNumbers, String addMessage) {
        log.info("批量添加好友，数量：{}", phoneNumbers.size());
        int success = 0, fail = 0;
        List<String> failedPhones = new ArrayList<>();
        for (String phone : phoneNumbers) {
            AddFriendResult result = addFriend(phone, addMessage);
            if (result.isSuccess()) { success++; } else { fail++; failedPhones.add(phone); }
            try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        return new BatchAddFriendResult(success, fail, failedPhones);
    }
    
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
            String response = HttpRequest.post(url).header("Content-Type", "application/json").body(body.toString()).timeout(10000).execute().body();
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
    
    public WeChatUserInfo getUserInfo(String userId) {
        try {
            String accessToken = getAccessToken();
            String url = String.format("%s/user/get?access_token=%s&userid=%s", BASE_URL, accessToken, userId);
            String response = HttpRequest.get(url).timeout(5000).execute().body();
            JSONObject json = JSONUtil.parseObj(response);
            int errCode = json.getInt("errcode", 0);
            if (errCode != 0) { return null; }
            return new WeChatUserInfo(userId, json.getStr("name"), json.getStr("avatar"));
        } catch (Exception e) {
            log.error("获取用户信息异常", e);
            return null;
        }
    }
    
    public AccountHealth checkAccountHealth(String accountId) {
        return AccountHealth.builder()
            .accountId(accountId).status("NORMAL").riskLevel("LOW")
            .dailyAddCount(0).dailyMessageCount(0)
            .suggestions(List.of("保持正常频率，避免批量操作"))
            .build();
    }
    
    private static class AccessToken {
        private final String token;
        private final long expireTime;
        public AccessToken(String token, long expireTime) { this.token = token; this.expireTime = expireTime; }
        public String getToken() { return token; }
        public boolean isExpired() { return System.currentTimeMillis() >= expireTime; }
    }
    
    public static class AddFriendResult {
        private boolean success;
        private String userId;
        private String errorMsg;
        public AddFriendResult() {}
        public AddFriendResult(boolean success, String userId, String errorMsg) {
            this.success = success; this.userId = userId; this.errorMsg = errorMsg;
        }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getErrorMsg() { return errorMsg; }
        public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }
        public static AddFriendResult success(String userId) {
            return new AddFriendResult(true, userId, null);
        }
        public static AddFriendResult failed(String errorMsg) {
            return new AddFriendResult(false, null, errorMsg);
        }
    }
    
    public static class BatchAddFriendResult {
        private int successCount, failCount;
        private List<String> failedPhones;
        public BatchAddFriendResult(int successCount, int failCount, List<String> failedPhones) {
            this.successCount = successCount; this.failCount = failCount; this.failedPhones = failedPhones;
        }
        public int getSuccessCount() { return successCount; }
        public int getFailCount() { return failCount; }
        public List<String> getFailedPhones() { return failedPhones; }
    }
    
    public static class WeChatUserInfo {
        private String userId, name, avatar;
        public WeChatUserInfo(String userId, String name, String avatar) {
            this.userId = userId; this.name = name; this.avatar = avatar;
        }
        public String getUserId() { return userId; }
        public String getName() { return name; }
        public String getAvatar() { return avatar; }
    }
    
    public static class AccountHealth {
        private String accountId, status, riskLevel;
        private int dailyAddCount, dailyMessageCount;
        private List<String> suggestions;
        public AccountHealth() {}
        public String getAccountId() { return accountId; }
        public void setAccountId(String accountId) { this.accountId = accountId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
        public int getDailyAddCount() { return dailyAddCount; }
        public void setDailyAddCount(int dailyAddCount) { this.dailyAddCount = dailyAddCount; }
        public int getDailyMessageCount() { return dailyMessageCount; }
        public void setDailyMessageCount(int dailyMessageCount) { this.dailyMessageCount = dailyMessageCount; }
        public List<String> getSuggestions() { return suggestions; }
        public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
        public static AccountHealthBuilder builder() { return new AccountHealthBuilder(); }
        public static class AccountHealthBuilder {
            private String accountId, status, riskLevel;
            private int dailyAddCount, dailyMessageCount;
            private List<String> suggestions;
            public AccountHealthBuilder accountId(String v) { this.accountId = v; return this; }
            public AccountHealthBuilder status(String v) { this.status = v; return this; }
            public AccountHealthBuilder riskLevel(String v) { this.riskLevel = v; return this; }
            public AccountHealthBuilder dailyAddCount(int v) { this.dailyAddCount = v; return this; }
            public AccountHealthBuilder dailyMessageCount(int v) { this.dailyMessageCount = v; return this; }
            public AccountHealthBuilder suggestions(List<String> v) { this.suggestions = v; return this; }
            public AccountHealth build() {
                AccountHealth h = new AccountHealth();
                h.accountId = accountId; h.status = status; h.riskLevel = riskLevel;
                h.dailyAddCount = dailyAddCount; h.dailyMessageCount = dailyMessageCount; h.suggestions = suggestions;
                return h;
            }
        }
    }
    
    public static class WeChatApiException extends RuntimeException {
        public WeChatApiException(String message) { super(message); }
        public WeChatApiException(String message, Throwable cause) { super(message, cause); }
    }
}
