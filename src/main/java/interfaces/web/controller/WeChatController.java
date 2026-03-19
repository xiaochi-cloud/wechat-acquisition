package com.wechat.acquisition.interfaces.web.controller;

import com.wechat.acquisition.application.service.WeChatService;
import com.wechat.acquisition.infrastructure.wechat.WeChatApiClient.AddFriendResult;
import com.wechat.acquisition.infrastructure.wechat.WeChatApiClient.BatchAddFriendResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企微对接 API
 */
@Slf4j
@RestController
@RequestMapping("/wechat")
@RequiredArgsConstructor
public class WeChatController {
    
    private final WeChatService weChatService;
    
    /**
     * 添加好友
     */
    @PostMapping("/friends/add")
    public ResponseEntity<Map<String, Object>> addFriend(
            @RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String message = request.getOrDefault("message", "您好，我是企业微信助手！");
        
        AddFriendResult result = weChatService.addFriend(phoneNumber, message);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        if (result.isSuccess()) {
            response.put("userId", result.getUserId());
            response.put("message", "添加成功");
        } else {
            response.put("error", result.getErrorMsg());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 批量添加好友
     */
    @PostMapping("/friends/batch-add")
    public ResponseEntity<Map<String, Object>> batchAddFriends(
            @RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<String> phoneNumbers = (List<String>) request.get("phoneNumbers");
        String message = (String) request.getOrDefault("message", "您好，我是企业微信助手！");
        
        BatchAddFriendResult result = weChatService.batchAddFriends(phoneNumbers, message);
        
        Map<String, Object> response = new HashMap<>();
        response.put("successCount", result.successCount());
        response.put("failCount", result.failCount());
        response.put("failedPhones", result.failedPhones());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 发送消息
     */
    @PostMapping("/messages/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String content = request.get("content");
        
        String msgId = weChatService.sendMessage(userId, content);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("msgId", msgId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取账号状态
     */
    @GetMapping("/account/status")
    public ResponseEntity<Map<String, Object>> getAccountStatus() {
        return ResponseEntity.ok(weChatService.getAccountStatus());
    }
}
