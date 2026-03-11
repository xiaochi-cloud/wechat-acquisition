package com.wechat.acquisition.domain.scoring;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class IntentScore {
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
