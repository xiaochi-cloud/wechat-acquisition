package com.wechat.acquisition.domain.scoring;
public enum IntentLevel {
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
