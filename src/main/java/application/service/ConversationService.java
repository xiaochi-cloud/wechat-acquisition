package com.wechat.acquisition.application.service;

import com.wechat.acquisition.domain.conversation.Conversation;
import com.wechat.acquisition.domain.conversation.DialogueMode;
import com.wechat.acquisition.domain.conversation.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 会话服务
 * 
 * 实现会话管理核心功能
 */
@Slf4j
@Service
public class ConversationService {
    
    // 内存存储 (临时)
    private final Map<String, Conversation> conversationStore = new ConcurrentHashMap<>();
    private final Map<String, List<Message>> messageStore = new ConcurrentHashMap<>();
    
    /**
     * 创建会话
     */
    public Conversation createConversation(String contactId, String campaignId, DialogueMode mode) {
        log.info("创建会话：contactId={}, campaignId={}, mode={}", contactId, campaignId, mode);
        
        Conversation conversation = Conversation.create(contactId, campaignId, mode);
        conversationStore.put(conversation.getId(), conversation);
        messageStore.put(conversation.getId(), new ArrayList<>());
        
        log.info("会话创建成功：id={}", conversation.getId());
        return conversation;
    }
    
    /**
     * 添加消息
     */
    public Message addMessage(String conversationId, Message message) {
        log.debug("添加消息：conversationId={}, direction={}", conversationId, message.getDirection());
        
        Conversation conversation = conversationStore.get(conversationId);
        if (conversation == null) {
            throw new IllegalStateException("会话不存在：" + conversationId);
        }
        
        conversation.addMessage(message);
        messageStore.get(conversationId).add(message);
        
        return message;
    }
    
    /**
     * 获取会话列表
     */
    public Map<String, Object> listConversations(String status, String contactId, int page, int size) {
        log.debug("查询会话列表：status={}, contactId={}", status, contactId);
        
        List<Conversation> all = new ArrayList<>(conversationStore.values());
        
        // 过滤
        if (status != null && !status.isEmpty()) {
            all = all.stream()
                .filter(c -> c.getStatus().name().equals(status))
                .collect(Collectors.toList());
        }
        
        if (contactId != null && !contactId.isEmpty()) {
            all = all.stream()
                .filter(c -> c.getContactId().equals(contactId))
                .collect(Collectors.toList());
        }
        
        // 排序
        all.sort((a, b) -> b.getLastMessageAt().compareTo(a.getLastMessageAt()));
        
        // 分页
        int total = all.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        
        List<Conversation> pageData = fromIndex < total ? 
            all.subList(fromIndex, toIndex) : new ArrayList<>();
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("data", pageData);
        
        return result;
    }
    
    /**
     * 获取会话详情 (含消息记录)
     */
    public Map<String, Object> getConversationDetail(String conversationId) {
        log.debug("查询会话详情：id={}", conversationId);
        
        Conversation conversation = conversationStore.get(conversationId);
        if (conversation == null) {
            throw new IllegalStateException("会话不存在：" + conversationId);
        }
        
        List<Message> messages = messageStore.getOrDefault(conversationId, new ArrayList<>());
        
        Map<String, Object> result = new HashMap<>();
        result.put("conversation", conversation);
        result.put("messages", messages);
        result.put("messageCount", messages.size());
        
        return result;
    }
    
    /**
     * 更新意向分数
     */
    public void updateIntentScore(String conversationId, Object score) {
        log.info("更新意向分数：conversationId={}, score={}", conversationId, score);
        
        Conversation conversation = conversationStore.get(conversationId);
        if (conversation != null) {
            conversation.updateIntentScore((com.wechat.acquisition.domain.scoring.IntentScore) score);
        }
    }
    
    /**
     * 结束会话
     */
    public void closeConversation(String conversationId, String endStatus) {
        log.info("结束会话：id={}, status={}", conversationId, endStatus);
        
        Conversation conversation = conversationStore.get(conversationId);
        if (conversation != null) {
            conversation.close(com.wechat.acquisition.domain.conversation.ConversationStatus.valueOf(endStatus));
        }
    }
    
    /**
     * 删除会话
     */
    public void deleteConversation(String conversationId) {
        log.info("删除会话：id={}", conversationId);
        
        conversationStore.remove(conversationId);
        messageStore.remove(conversationId);
    }
}
