package com.wechat.acquisition.domain.conversation;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 消息实体
 * 
 * 职责：表示单条对话消息
 */
@Data
@Builder
public class Message {
    
    private String id;
    private String conversationId;
    private MessageDirection direction;
    private MessageType type;
    private String content;
    private Map<String, Object> metadata;
    private String templateId;        // 如果使用模板
    private LocalDateTime createdAt;
    
    /**
     * 创建用户消息
     */
    public static Message createUserMessage(String conversationId, String content) {
        return Message.builder()
                .id(UUID.randomUUID().toString())
                .conversationId(conversationId)
                .direction(MessageDirection.FROM_USER)
                .type(MessageType.TEXT)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 创建 AI 消息
     */
    public static Message createAIMessage(String conversationId, String content, String templateId) {
        return Message.builder()
                .id(UUID.randomUUID().toString())
                .conversationId(conversationId)
                .direction(MessageDirection.FROM_AI)
                .type(MessageType.TEXT)
                .content(content)
                .templateId(templateId)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 创建系统消息
     */
    public static Message createSystemMessage(String conversationId, String content) {
        return Message.builder()
                .id(UUID.randomUUID().toString())
                .conversationId(conversationId)
                .direction(MessageDirection.SYSTEM)
                .type(MessageType.SYSTEM)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }
}

/**
 * 消息方向
 */
public enum MessageDirection {
    FROM_USER,    // 用户发送
    FROM_AI,      // AI 发送
    SYSTEM        // 系统消息
}

/**
 * 消息类型
 */
public enum MessageType {
    TEXT,         // 文本
    IMAGE,        // 图片
    LINK,         // 链接
    FILE,         // 文件
    VOICE,        // 语音
    SYSTEM        // 系统消息
}
