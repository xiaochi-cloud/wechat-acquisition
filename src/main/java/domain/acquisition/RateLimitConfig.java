package com.wechat.acquisition.domain.acquisition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class RateLimitConfig {
    private static final Logger log = LoggerFactory.getLogger(RateLimitConfig.class);
    private Integer dailyAddLimit;
    private Integer hourlyAddLimit;
    private Integer minuteAddLimit;
    private Integer addIntervalMin;
    private Integer addIntervalMax;
    private Integer messageDailyLimit;
    private Integer accountPoolSize;
    private Boolean enableRotation;
    private Integer rotationThreshold;
    
    public RateLimitConfig() {}
    public Integer getDailyAddLimit() { return dailyAddLimit; }
    public void setDailyAddLimit(Integer dailyAddLimit) { this.dailyAddLimit = dailyAddLimit; }
    public Integer getHourlyAddLimit() { return hourlyAddLimit; }
    public void setHourlyAddLimit(Integer hourlyAddLimit) { this.hourlyAddLimit = hourlyAddLimit; }
    public Integer getMinuteAddLimit() { return minuteAddLimit; }
    public void setMinuteAddLimit(Integer minuteAddLimit) { this.minuteAddLimit = minuteAddLimit; }
    public Integer getAddIntervalMin() { return addIntervalMin; }
    public void setAddIntervalMin(Integer addIntervalMin) { this.addIntervalMin = addIntervalMin; }
    public Integer getAddIntervalMax() { return addIntervalMax; }
    public void setAddIntervalMax(Integer addIntervalMax) { this.addIntervalMax = addIntervalMax; }
    public Integer getMessageDailyLimit() { return messageDailyLimit; }
    public void setMessageDailyLimit(Integer messageDailyLimit) { this.messageDailyLimit = messageDailyLimit; }
    public Integer getAccountPoolSize() { return accountPoolSize; }
    public void setAccountPoolSize(Integer accountPoolSize) { this.accountPoolSize = accountPoolSize; }
    public Boolean getEnableRotation() { return enableRotation; }
    public void setEnableRotation(Boolean enableRotation) { this.enableRotation = enableRotation; }
    public Integer getRotationThreshold() { return rotationThreshold; }
    public void setRotationThreshold(Integer rotationThreshold) { this.rotationThreshold = rotationThreshold; }
    
    public static RateLimitConfig createSafeDefault() {
        RateLimitConfig c = new RateLimitConfig();
        c.setDailyAddLimit(50); c.setHourlyAddLimit(10); c.setMinuteAddLimit(2);
        c.setAddIntervalMin(30); c.setAddIntervalMax(120); c.setMessageDailyLimit(200);
        c.setAccountPoolSize(3); c.setEnableRotation(true); c.setRotationThreshold(80);
        return c;
    }
    
    public static RateLimitConfig createAggressive() {
        RateLimitConfig c = new RateLimitConfig();
        c.setDailyAddLimit(100); c.setHourlyAddLimit(15); c.setMinuteAddLimit(3);
        c.setAddIntervalMin(20); c.setAddIntervalMax(60); c.setMessageDailyLimit(500);
        c.setAccountPoolSize(10); c.setEnableRotation(true); c.setRotationThreshold(90);
        return c;
    }
    
    public static RateLimitConfig createConservative() {
        RateLimitConfig c = new RateLimitConfig();
        c.setDailyAddLimit(30); c.setHourlyAddLimit(5); c.setMinuteAddLimit(1);
        c.setAddIntervalMin(60); c.setAddIntervalMax(180); c.setMessageDailyLimit(100);
        c.setAccountPoolSize(5); c.setEnableRotation(true); c.setRotationThreshold(50);
        return c;
    }
    
    public boolean isDailyLimitReached(int currentCount) { return currentCount >= dailyAddLimit; }
    public boolean isHourlyLimitReached(int currentCount) { return currentCount >= hourlyAddLimit; }
    public int getRandomInterval() { return addIntervalMin + (int)(Math.random() * (addIntervalMax - addIntervalMin)); }
}
