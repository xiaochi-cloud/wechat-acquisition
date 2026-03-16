package com.wechat.acquisition.infrastructure.scheduler.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wechat.acquisition.domain.conversation.Conversation;
import com.wechat.acquisition.domain.conversation.ConversationStatus;
import com.wechat.acquisition.domain.scoring.IntentLevel;
import com.wechat.acquisition.domain.scoring.IntentScore;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 会话跟进定时任务
 * 
 * 执行策略：
 * - 每 5 分钟执行一次
 * - 检查超时未回复会话
 * - 自动发送跟进消息
 * - 更新意向打分
 */
@Component
public class ConversationFollowUpJob {
    private static final Logger log = LoggerFactory.getLogger(ConversationFollowUpJob.class);
    
    // TODO: 注入依赖
    // private final ConversationRepository conversationRepository;
    // private final IntentScoringService scoringService;
    // private final WeChatApiClient weChatClient;
    
    /**
     * 超时会话跟进任务
     */
    @XxlJob("conversationFollowUpJob")
    public ReturnT<String> followUpTimeoutConversations(String param) {
        log.info("========== 会话跟进任务开始 ==========");
        
        try {
            // 1. 获取超时未回复的会话 (超过 30 分钟)
            List<Conversation> timeoutConversations = getTimeoutConversations(30);
            if (timeoutConversations.isEmpty()) {
                XxlJobLogger.log("没有超时会话");
                return ReturnT.SUCCESS;
            }
            
            int followedUpCount = 0;
            
            for (Conversation conversation : timeoutConversations) {
                try {
                    // 2. 根据意向等级决定跟进策略
                    IntentScore score = conversation.getIntentScore();
                    if (score == null) {
                        // 先打分
                        // score = scoringService.calculateScore(conversation.getId());
                        continue;
                    }
                    
                    // 3. 根据等级采取不同策略
                    if (score.getLevel() == IntentLevel.A || score.getLevel() == IntentLevel.B) {
                        // 高意向：发送个性化跟进消息
                        sendFollowUpMessage(conversation, getHighIntentFollowUpTemplate());
                        followedUpCount++;
                        
                    } else if (score.getLevel() == IntentLevel.C) {
                        // 中意向：发送通用跟进消息
                        sendFollowUpMessage(conversation, getMediumIntentFollowUpTemplate());
                        followedUpCount++;
                    }
                    // D 级不跟进
                    
                    XxlJobLogger.log("跟进会话：{}", conversation.getId());
                    
                } catch (Exception e) {
                    log.error("跟进会话失败：{}", conversation.getId(), e);
                }
            }
            
            XxlJobLogger.log("跟进完成：{} 个会话", followedUpCount);
            return ReturnT.SUCCESS;
            
        } catch (Exception e) {
            log.error("会话跟进任务异常", e);
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }
    
    /**
     * 意向打分更新任务
     */
    @XxlJob("intentScoreUpdateJob")
    public ReturnT<String> updateIntentScores(String param) {
        log.info("========== 意向打分更新任务开始 ==========");
        
        try {
            // 1. 获取最近有消息的会话
            List<Conversation> activeConversations = getActiveConversations();
            
            int updatedCount = 0;
            
            for (Conversation conversation : activeConversations) {
                try {
                    // 2. 重新计算意向分数
                    // IntentScore newScore = scoringService.calculateScore(conversation.getId());
                    // conversation.updateIntentScore(newScore);
                    
                    // 3. 如果等级提升，触发通知
                    // if (newScore.getLevel() == IntentLevel.A) {
                    //     notifySalesTeam(conversation);
                    // }
                    
                    updatedCount++;
                    
                } catch (Exception e) {
                    log.error("更新打分失败：{}", conversation.getId(), e);
                }
            }
            
            XxlJobLogger.log("打分更新完成：{} 个会话", updatedCount);
            return ReturnT.SUCCESS;
            
        } catch (Exception e) {
            log.error("打分更新任务异常", e);
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }
    
    /**
     *  abandoned 会话清理任务
     */
    @XxlJob("conversationCleanupJob")
    public ReturnT<String> cleanupAbandonedConversations(String param) {
        log.info("========== 废弃会话清理任务开始 ==========");
        
        try {
            // 1. 获取超过 7 天无活动的会话
            List<Conversation> abandonedConversations = getAbandonedConversations(7);
            
            int closedCount = 0;
            
            for (Conversation conversation : abandonedConversations) {
                // 2. 关闭会话
                // conversation.close(ConversationStatus.ABANDONED);
                closedCount++;
            }
            
            XxlJobLogger.log("清理完成：关闭 {} 个废弃会话", closedCount);
            return ReturnT.SUCCESS;
            
        } catch (Exception e) {
            log.error("会话清理任务异常", e);
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }
    
    /**
     * 获取超时会话
     */
    private List<Conversation> getTimeoutConversations(int minutes) {
        // TODO: 查询数据库
        return List.of();
    }
    
    /**
     * 获取活跃会话
     */
    private List<Conversation> getActiveConversations() {
        // TODO: 查询数据库
        return List.of();
    }
    
    /**
     * 获取废弃会话
     */
    private List<Conversation> getAbandonedConversations(int days) {
        // TODO: 查询数据库
        return List.of();
    }
    
    /**
     * 发送跟进消息
     */
    private void sendFollowUpMessage(Conversation conversation, String template) {
        // TODO: 调用企微 API 发送消息
    }
    
    /**
     * 高意向跟进模板
     */
    private String getHighIntentFollowUpTemplate() {
        return "您好！刚才聊的还顺利吗？有什么疑问我可以帮您解答的~";
    }
    
    /**
     * 中意向跟进模板
     */
    private String getMediumIntentFollowUpTemplate() {
        return "您好，有任何问题随时联系我哦~";
    }
}
