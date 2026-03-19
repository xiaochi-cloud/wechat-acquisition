package com.wechat.acquisition.application.service;

import com.wechat.acquisition.infrastructure.wechat.WeChatApiClient;
import com.wechat.acquisition.infrastructure.wechat.WeChatApiClient.AddFriendResult;
import com.wechat.acquisition.infrastructure.wechat.WeChatApiClient.BatchAddFriendResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企微服务层
 * 封装企微 API 调用，添加业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeChatService {
    
    private final WeChatApiClient weChatClient;
    
    /**
     * 添加好友
     */
    public AddFriendResult addFriend(String phoneNumber, String addMessage) {
        log.info("添加好友：phone={}, message={}", phoneNumber, addMessage);
        return weChatClient.addFriend(phoneNumber, addMessage);
    }
    
    /**
     * 批量添加好友
     */
    public BatchAddFriendResult batchAddFriends(List<String> phoneNumbers, String addMessage) {
        log.info("批量添加好友：count={}", phoneNumbers.size());
        return weChatClient.batchAddFriends(phoneNumbers, addMessage);
    }
    
    /**
     * 发送消息
     */
    public String sendMessage(String userId, String content) {
        log.info("发送消息：userId={}, content={}", userId, content);
        return weChatClient.sendMessage(userId, content, "text");
    }
    
    /**
     * 获取账号状态
     */
    public Map<String, Object> getAccountStatus() {
        log.info("获取账号状态");
        
        Map<String, Object> status = new HashMap<>();
        status.put("connected", true);
        status.put("dailyLimit", 50);
        status.put("dailyUsed", 0);
        status.put("hourlyLimit", 10);
        status.put("hourlyUsed", 0);
        
        return status;
    }
}
