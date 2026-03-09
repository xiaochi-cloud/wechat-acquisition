package com.wechat.acquisition.domain.scoring;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 意向打分值对象
 * 
 * 职责：表示用户的意向评分结果
 */
@Data
@Builder
public class IntentScore {
    
    private Double totalScore;              // 总分 (0-100)
    private IntentLevel level;              // 意向等级
    private Map<String, Double> dimensions; // 各维度得分
    private String reasoning;               // 打分理由
    private LocalDateTime scoredAt;         // 打分时间
    
    /**
     * 计算总分并确定等级
     */
    public static IntentScore calculate(Map<String, Double> dimensions, String reasoning) {
        // 维度权重
        Map<String, Double> weights = Map.of(
            "responsiveness", 0.30,  // 响应度 30%
            "interest", 0.35,        // 兴趣度 35%
            "urgency", 0.20,         // 紧迫度 20%
            "match", 0.15            // 匹配度 15%
        );
        
        double totalScore = 0;
        for (Map.Entry<String, Double> entry : dimensions.entrySet()) {
            Double weight = weights.get(entry.getKey());
            if (weight != null && entry.getValue() != null) {
                totalScore += entry.getValue() * weight;
            }
        }
        
        // 限制在 0-100 范围
        totalScore = Math.max(0, Math.min(100, totalScore));
        
        return IntentScore.builder()
                .totalScore(totalScore)
                .level(IntentLevel.fromScore(totalScore))
                .dimensions(dimensions)
                .reasoning(reasoning)
                .scoredAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 创建空打分 (初始状态)
     */
    public static IntentScore empty() {
        return IntentScore.builder()
                .totalScore(0.0)
                .level(IntentLevel.D)
                .dimensions(new HashMap<>())
                .reasoning("尚未评估")
                .scoredAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 是否需要人工跟进
     */
    public boolean needsHumanFollowup() {
        return this.level == IntentLevel.A || this.level == IntentLevel.B;
    }
}

/**
 * 意向等级
 */
public enum IntentLevel {
    A(80, 100, "高意向", "立即人工跟进"),
    B(60, 79, "中意向", "持续培育"),
    C(40, 59, "低意向", "自动化触达"),
    D(0, 39, "无意向", "归档/移除");
    
    private final int minScore;
    private final int maxScore;
    private final String name;
    private final String action;
    
    IntentLevel(int minScore, int maxScore, String name, String action) {
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.name = name;
        this.action = action;
    }
    
    /**
     * 根据分数确定等级
     */
    public static IntentLevel fromScore(double score) {
        for (IntentLevel level : values()) {
            if (score >= level.minScore && score <= level.maxScore) {
                return level;
            }
        }
        return D;
    }
    
    public String getName() {
        return name;
    }
    
    public String getAction() {
        return action;
    }
}
