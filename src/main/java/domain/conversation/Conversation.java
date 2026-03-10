package com.wechat.acquisition.domain.conversation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Conversation {
    private String id;
    private String contactId;
    private String campaignId;
    private String weChatAccountId;
    private ConversationStatus status;
    private DialogueMode mode;
    private String scenario;
    private List<Message> messages;
    private domain.scoring.IntentScore intentScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastMessageAt;
    
    public Conversation() { this.messages = new ArrayList<>(); }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }
    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }
    public String getWeChatAccountId() { return weChatAccountId; }
    public void setWeChatAccountId(String weChatAccountId) { this.weChatAccountId = weChatAccountId; }
    public ConversationStatus getStatus() { return status; }
    public void setStatus(ConversationStatus status) { this.status = status; }
    public DialogueMode getMode() { return mode; }
    public void setMode(DialogueMode mode) { this.mode = mode; }
    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
    public domain.scoring.IntentScore getIntentScore() { return intentScore; }
    public void setIntentScore(domain.scoring.IntentScore intentScore) { this.intentScore = intentScore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }
    
    public void addMessage(Message message) {
        this.messages.add(message);
        this.lastMessageAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateIntentScore(domain.scoring.IntentScore score) {
        this.intentScore = score;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void close(ConversationStatus endStatus) {
        this.status = endStatus;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getTurnCount() {
        return (int) this.messages.stream()
            .filter(m -> m.getDirection() == MessageDirection.FROM_USER).count();
    }
    
    public boolean canContinue() {
        return this.status == ConversationStatus.ACTIVE && this.getTurnCount() < 50;
    }
    
    public static Conversation create(String contactId, String campaignId, DialogueMode mode) {
        Conversation c = new Conversation();
        c.setId(UUID.randomUUID().toString());
        c.setContactId(contactId);
        c.setCampaignId(campaignId);
        c.setStatus(ConversationStatus.ACTIVE);
        c.setMode(mode);
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        return c;
    }
}

enum ConversationStatus { ACTIVE, PAUSED, COMPLETED, ABANDONED, TRANSFERRED, BLOCKED }
enum DialogueMode { PRESET, FREE, HYBRID }
