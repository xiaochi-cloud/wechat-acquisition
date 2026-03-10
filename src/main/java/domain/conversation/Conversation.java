package com.wechat.acquisition.domain.conversation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 会话聚合根
 * 
 * 职责：管理与用户的完整对话过程，包括消息记录、意向打分、状态流转
 */
public class Conversation {
    private static final Logger log = LoggerFactory.getLogger(Conversation.class);
    
    private String id;
    private String contactId;
    private String campaignId;
    private String weChatAccountId;     // 负责对话的企微账号
    private ConversationStatus status;
    private DialogueMode mode;          // 对话模式：预设/自由
    private String scenario;            // 对话场景
    private List<Message> messages;
    private IntentScore intentScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastMessageAt;
    
    /**
     * 创建新会话
     */
    public static Conversation create(String contactId, String campaignId, DialogueMode mode) {
        return Conversation.builder()
                .id(UUID.randomUUID().toString())
                .contactId(contactId)
                .campaignId(campaignId)
                .status(ConversationStatus.ACTIVE)
                .mode(mode)
                .messages(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 添加消息
     */
    public void addMessage(Message message) {
        this.messages.add(message);
        this.lastMessageAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新意向打分
     */
    public void updateIntentScore(IntentScore score) {
        this.intentScore = score;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 结束会话
     */
    public void close(ConversationStatus endStatus) {
        this.status = endStatus;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 获取对话轮数
     */
    public int getTurnCount() {
        return (int) this.messages.stream()
                .filter(m -> m.getDirection() == MessageDirection.FROM_USER)
                .count();
    }
    
    /**
     * 是否可以进行下一轮对话
     */
    public boolean canContinue() {
        return this.status == ConversationStatus.ACTIVE 
            && this.getTurnCount() < 50; // 最多 50 轮
    }
}

/**
 * 会话状态
 */
enum ConversationStatus {
    ACTIVE,         // 进行中
    PAUSED,         // 已暂停
    COMPLETED,      // 已完成 (正常结束)
    ABANDONED,      // 已放弃 (用户无响应)
    TRANSFERRED,    // 已转人工
    BLOCKED         // 已拉黑
}

/**
 * 对话模式
 */
enum DialogueMode {
    PRESET,     // 预设流程
    FREE,       // 自由对话
    HYBRID      // 混合模式
}
