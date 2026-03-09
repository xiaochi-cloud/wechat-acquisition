package com.wechat.acquisition.infrastructure.wechat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 企业微信 API 客户端
 * 
 * 职责：封装企微 API 调用，包括加好友、发消息等
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WeChatApiClient {
    
    @Value("${wechat.corp-id:}")
    private String corpId;
    
    @Value("${wechat.agent-id:}")
    private String agentId;
    
    @Value("${wechat.secret:}")
    private String secret;
    
    @Value("${wechat.token:}")
    private String token;
    
    // TODO: 实现企微 API 调用
    
    /**
     * 获取访问令牌
     */
    public String getAccessToken() {
        // TODO: 调用企微 API 获取 access_token
        // GET https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={corpId}&corpsecret={secret}
        return "mock_access_token";
    }
    
    /**
     * 添加企业微信好友
     * 
     * @param phoneNumber 用户手机号
     * @param addMessage 好友申请语
     * @return 任务 ID
     */
    public String addFriend(String phoneNumber, String addMessage) {
        log.info("添加企微好友：{}, 申请语：{}", phoneNumber, addMessage);
        
        // TODO: 调用企微 API
        // POST https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_contact_friend
        
        return "mock_task_id";
    }
    
    /**
     * 发送消息给用户
     * 
     * @param userId 企微用户 ID
     * @param content 消息内容
     * @param msgType 消息类型 (text/image/link)
     * @return 消息 ID
     */
    public String sendMessage(String userId, String content, String msgType) {
        log.info("发送消息给用户：{}, 类型：{}", userId, msgType);
        
        // TODO: 调用企微 API
        // POST https://qyapi.weixin.qq.com/cgi-bin/externalcontact/send_welcome_msg
        
        return "mock_message_id";
    }
    
    /**
     * 获取用户详情
     */
    public WeChatUserInfo getUserInfo(String userId) {
        // TODO: 调用企微 API 获取用户详情
        return new WeChatUserInfo(userId, "Mock User", "mock_avatar.png");
    }
    
    /**
     * 检查账号健康状态
     */
    public AccountHealth checkAccountHealth(String accountId) {
        // TODO: 检查企微账号风险状态
        return AccountHealth.builder()
            .accountId(accountId)
            .status("NORMAL")
            .riskLevel("LOW")
            .dailyAddCount(0)
            .dailyMessageCount(0)
            .build();
    }
    
    /**
     * 企微用户信息
     */
    public record WeChatUserInfo(
        String userId,
        String name,
        String avatar
    ) {}
    
    /**
     * 账号健康状态
     */
    @lombok.Builder
    @lombok.Data
    public static class AccountHealth {
        private String accountId;
        private String status;
        private String riskLevel;
        private int dailyAddCount;
        private int dailyMessageCount;
    }
}
