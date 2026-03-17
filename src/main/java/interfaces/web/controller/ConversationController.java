package com.wechat.acquisition.interfaces.web.controller;

import com.wechat.acquisition.application.service.ConversationService;
import com.wechat.acquisition.domain.conversation.DialogueMode;
import com.wechat.acquisition.domain.conversation.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 会话管理 API
 */
@Slf4j
@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {
    
    private final ConversationService conversationService;
    
    /**
     * 获取会话列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listConversations(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String contactId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
            conversationService.listConversations(status, contactId, page, size)
        );
    }
    
    /**
     * 获取会话详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getConversationDetail(@PathVariable String id) {
        return ResponseEntity.ok(conversationService.getConversationDetail(id));
    }
    
    /**
     * 创建会话
     */
    @PostMapping
    public ResponseEntity<Message> createConversation(
            @RequestBody Map<String, String> request) {
        String contactId = request.get("contactId");
        String campaignId = request.get("campaignId");
        String mode = request.getOrDefault("mode", "FREE");
        
        var conversation = conversationService.createConversation(
            contactId, campaignId, DialogueMode.valueOf(mode)
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", conversation);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 发送消息
     */
    @PostMapping("/{id}/messages")
    public ResponseEntity<Message> sendMessage(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        
        String content = request.get("content");
        String direction = request.get("direction");
        
        Message message;
        if ("FROM_USER".equals(direction)) {
            message = Message.createUserMessage(id, content);
        } else {
            message = Message.createAIMessage(id, content, null);
        }
        
        conversationService.addMessage(id, message);
        return ResponseEntity.ok(message);
    }
    
    /**
     * 更新意向分数
     */
    @PostMapping("/{id}/score")
    public ResponseEntity<Map<String, Object>> updateScore(
            @PathVariable String id,
            @RequestBody Map<String, Object> score) {
        
        conversationService.updateIntentScore(id, score);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "分数已更新");
        return ResponseEntity.ok(result);
    }
    
    /**
     * 结束会话
     */
    @PostMapping("/{id}/close")
    public ResponseEntity<Map<String, Object>> closeConversation(
            @PathVariable String id,
            @RequestParam String status) {
        
        conversationService.closeConversation(id, status);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "会话已结束");
        return ResponseEntity.ok(result);
    }
    
    /**
     * 删除会话
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteConversation(@PathVariable String id) {
        conversationService.deleteConversation(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "删除成功");
        return ResponseEntity.ok(result);
    }
}
