package com.wechat.acquisition.domain.conversation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 消息实体
 */
public class Message {
    private static final Logger log = LoggerFactory.getLogger(Message.class);
    private String id;
    private String conversationId;
    private MessageDirection direction;
    private MessageType type;
    private String content;
    private Map<String, Object> metadata;
    private String templateId;
    private LocalDateTime createdAt;
    
    public Message() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    public MessageDirection getDirection() { return direction; }
    public void setDirection(MessageDirection direction) { this.direction = direction; }
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public static Message createUserMessage(String conversationId, String content) {
        Message m = new Message();
        m.setId(UUID.randomUUID().toString());
        m.setConversationId(conversationId);
        m.setDirection(MessageDirection.FROM_USER);
        m.setType(MessageType.TEXT);
        m.setContent(content);
        m.setCreatedAt(LocalDateTime.now());
        return m;
    }
    public static Message createAIMessage(String conversationId, String content, String templateId) {
        Message m = new Message();
        m.setId(UUID.randomUUID().toString());
        m.setConversationId(conversationId);
        m.setDirection(MessageDirection.FROM_AI);
        m.setType(MessageType.TEXT);
        m.setContent(content);
        m.setTemplateId(templateId);
        m.setCreatedAt(LocalDateTime.now());
        return m;
    }
    public static Message createSystemMessage(String conversationId, String content) {
        Message m = new Message();
        m.setId(UUID.randomUUID().toString());
        m.setConversationId(conversationId);
        m.setDirection(MessageDirection.SYSTEM);
        m.setType(MessageType.SYSTEM);
        m.setContent(content);
        m.setCreatedAt(LocalDateTime.now());
        return m;
    }
}

