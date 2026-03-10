package com.wechat.acquisition.domain.scoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 意向打分值对象
 */
public class IntentScore {
    private static final Logger log = LoggerFactory.getLogger(IntentScore.class);
    
    private Double totalScore;
    private IntentLevel level;
    private Map<String, Double> dimensions;
    private String reasoning;
    private LocalDateTime scoredAt;
    
    public IntentScore() {}
    
    public Double getTotalScore() { return totalScore; }
    public void setTotalScore(Double totalScore) { this.totalScore = totalScore; }
    public IntentLevel getLevel() { return level; }
    public void setLevel(IntentLevel level) { this.level = level; }
    public Map<String, Double> getDimensions() { return dimensions; }
    public void setDimensions(Map<String, Double> dimensions) { this.dimensions = dimensions; }
    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }
    public LocalDateTime getScoredAt() { return scoredAt; }
    public void setScoredAt(LocalDateTime scoredAt) { this.scoredAt = scoredAt; }
    
    public static IntentScore calculate(Map<String, Double> dimensions, String reasoning) {
        Map<String, Double> weights = Map.of("responsiveness", 0.30, "interest", 0.35, "urgency", 0.20, "match", 0.15);
        double totalScore = 0;
        for (Map.Entry<String, Double> entry : dimensions.entrySet()) {
            Double weight = weights.get(entry.getKey());
            if (weight != null && entry.getValue() != null) {
                totalScore += entry.getValue() * weight;
            }
        }
        totalScore = Math.max(0, Math.min(100, totalScore));
        IntentScore score = new IntentScore();
        score.setTotalScore(totalScore);
        score.setLevel(IntentLevel.fromScore(totalScore));
        score.setDimensions(dimensions);
        score.setReasoning(reasoning);
        score.setScoredAt(LocalDateTime.now());
        return score;
    }
    
    public static IntentScore empty() {
        IntentScore score = new IntentScore();
        score.setTotalScore(0.0);
        score.setLevel(IntentLevel.D);
        score.setDimensions(new HashMap<>());
        score.setReasoning("尚未评估");
        score.setScoredAt(LocalDateTime.now());
        return score;
    }
    
    public boolean needsHumanFollowup() {
        return this.level == IntentLevel.A || this.level == IntentLevel.B;
    }
}

enum IntentLevel {
    A(80, 100, "高意向", "立即人工跟进"),
    B(60, 79, "中意向", "持续培育"),
    C(40, 59, "低意向", "自动化触达"),
    D(0, 39, "无意向", "归档/移除");
    
    private final int minScore, maxScore;
    private final String name, action;
    
    IntentLevel(int min, int max, String name, String action) {
        this.minScore = min; this.maxScore = max;
        this.name = name; this.action = action;
    }
    
    public int getMinScore() { return minScore; }
    public int getMaxScore() { return maxScore; }
    public String getName() { return name; }
    public String getAction() { return action; }
    
    public static IntentLevel fromScore(double score) {
        for (IntentLevel level : values()) {
            if (score >= level.minScore && score <= level.maxScore) return level;
        }
        return D;
    }
}
