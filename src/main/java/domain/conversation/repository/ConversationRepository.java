package com.wechat.acquisition.domain.conversation.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wechat.acquisition.domain.conversation.Conversation;
import com.wechat.acquisition.domain.conversation.ConversationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 会话 Repository 接口
 */
public interface ConversationRepository {
    
    /**
     * 根据 ID 查询
     */
    Optional<Conversation> findById(String id);
    
    /**
     * 根据联系人 ID 查询
     */
    Optional<Conversation> findByContactId(String contactId);
    
    /**
     * 根据状态查询
     */
    List<Conversation> findByStatus(ConversationStatus status);
    
    /**
     * 查询超时会话
     */
    List<Conversation> findTimeoutConversations(LocalDateTime beforeTime);
    
    /**
     * 查询活跃会话
     */
    List<Conversation> findActiveConversations(LocalDateTime afterTime);
    
    /**
     * 查询废弃会话
     */
    List<Conversation> findAbandonedConversations(LocalDateTime beforeTime);
    
    /**
     * 保存
     */
    Conversation save(Conversation conversation);
    
    /**
     * 删除
     */
    void deleteById(String id);
    
    /**
     * 统计数量
     */
    long count();
    
    /**
     * 统计某状态的数量
     */
    long countByStatus(ConversationStatus status);
}
