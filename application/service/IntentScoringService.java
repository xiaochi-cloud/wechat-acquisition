package com.wechat.acquisition.application.service;

import com.wechat.acquisition.domain.conversation.Conversation;
import com.wechat.acquisition.domain.conversation.Message;
import com.wechat.acquisition.domain.scoring.IntentLevel;
import com.wechat.acquisition.domain.scoring.IntentScore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 意向打分应用服务
 * 
 * 职责：规则引擎 + 大模型混合打分
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IntentScoringService {
    
    // TODO: 注入依赖
    // private final ConversationRepository conversationRepository;
    // private final LlmClient llmClient;
    // private final ScoringRuleEngine ruleEngine;
    
    /**
     * 实时打分
     */
    public IntentScore calculateScore(String conversationId) {
        log.info("开始对话打分：{}", conversationId);
        
        // 获取对话
        // Conversation conversation = conversationRepository.findById(conversationId);
        
        // 提取消息
        List<Message> messages = List.of(); // conversation.getMessages();
        
        // 规则引擎打分
        Map<String, Double> ruleScores = evaluateByRules(messages);
        
        // 大模型分析 (可选，根据配置)
        // IntentScore llmScore = llmClient.analyzeIntent(messages);
        
        // 综合打分
        IntentScore score = IntentScore.calculate(ruleScores, "基于规则引擎评估");
        
        log.info("打分完成：总分={}, 等级={}", score.getTotalScore(), score.getLevel());
        
        return score;
    }
    
    /**
     * 批量打分
     */
    public Map<String, IntentScore> batchCalculateScore(List<String> conversationIds) {
        Map<String, IntentScore> results = new HashMap<>();
        
        for (String conversationId : conversationIds) {
            try {
                IntentScore score = calculateScore(conversationId);
                results.put(conversationId, score);
            } catch (Exception e) {
                log.error("打分失败：{}", conversationId, e);
            }
        }
        
        return results;
    }
    
    /**
     * 规则引擎评估
     */
    private Map<String, Double> evaluateByRules(List<Message> messages) {
        Map<String, Double> dimensions = new HashMap<>();
        
        // 响应度评分
        dimensions.put("responsiveness", evaluateResponsiveness(messages));
        
        // 兴趣度评分
        dimensions.put("interest", evaluateInterest(messages));
        
        // 紧迫度评分
        dimensions.put("urgency", evaluateUrgency(messages));
        
        // 匹配度评分 (需要用户画像数据)
        dimensions.put("match", 50.0); // 默认
        
        return dimensions;
    }
    
    /**
     * 响应度评分
     */
    private double evaluateResponsiveness(List<Message> messages) {
        if (messages.isEmpty()) return 0;
        
        // 计算平均回复长度
        double avgLength = messages.stream()
            .filter(m -> m.getDirection().toString().equals("FROM_USER"))
            .mapToDouble(m -> m.getContent() != null ? m.getContent().length() : 0)
            .average()
            .orElse(0);
        
        // 长度 > 50 字：100 分，> 20 字：80 分，> 10 字：60 分，否则 40 分
        if (avgLength > 50) return 100;
        if (avgLength > 20) return 80;
        if (avgLength > 10) return 60;
        return 40;
    }
    
    /**
     * 兴趣度评分
     */
    private double evaluateInterest(List<Message> messages) {
        if (messages.isEmpty()) return 0;
        
        // 产品关键词
        List<String> keywords = List.of("价格", "费用", "怎么用", "功能", "效果", "案例", "服务", "产品");
        
        long keywordCount = messages.stream()
            .filter(m -> m.getDirection().toString().equals("FROM_USER"))
            .map(Message::getContent)
            .filter(content -> content != null)
            .flatMap(content -> keywords.stream().filter(content::contains))
            .count();
        
        if (keywordCount > 5) return 100;
        if (keywordCount > 2) return 80;
        if (keywordCount > 0) return 60;
        return 30;
    }
    
    /**
     * 紧迫度评分
     */
    private double evaluateUrgency(List<Message> messages) {
        if (messages.isEmpty()) return 0;
        
        // 时间紧迫关键词
        List<String> urgencyKeywords = List.of("尽快", "马上", "本周", "下周", "急", "快", "最近", "现在");
        
        boolean hasUrgency = messages.stream()
            .filter(m -> m.getDirection().toString().equals("FROM_USER"))
            .map(Message::getContent)
            .anyMatch(content -> content != null && 
                urgencyKeywords.stream().anyMatch(content::contains));
        
        return hasUrgency ? 100 : 50;
    }
    
    /**
     * 根据分数获取跟进建议
     */
    public String getFollowupSuggestion(IntentScore score) {
        return switch (score.getLevel()) {
            case A -> "🔥 高意向用户，建议 1 小时内人工跟进！";
            case B -> "📞 中意向用户，建议 24 小时内跟进培育";
            case C -> "📧 低意向用户，纳入自动化培育流程";
            case D -> "🗂️ 无意向用户，归档或移除";
        };
    }
}
