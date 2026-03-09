package com.wechat.acquisition.domain.acquisition;

import lombok.Builder;
import lombok.Data;

/**
 * 频率限制配置
 * 
 * 职责：控制企微加好友和发消息的频率，防止封号
 */
@Data
@Builder
public class RateLimitConfig {
    
    private Integer dailyAddLimit;        // 单号每日加人上限
    private Integer hourlyAddLimit;       // 单号每小时加人上限
    private Integer minuteAddLimit;       // 单号每分钟加人上限
    private Integer addIntervalMin;       // 加好友最小间隔 (秒)
    private Integer addIntervalMax;       // 加好友最大间隔 (秒)
    private Integer messageDailyLimit;    // 单号每日发消息上限
    private Integer accountPoolSize;      // 账号池大小
    private Boolean enableRotation;       // 是否启用账号轮换
    private Integer rotationThreshold;    // 轮换阈值 (达到上限后轮换)
    
    /**
     * 创建安全默认配置 (防封号)
     */
    public static RateLimitConfig createSafeDefault() {
        return RateLimitConfig.builder()
                .dailyAddLimit(50)           // 每日最多 50 人
                .hourlyAddLimit(10)          // 每小时最多 10 人
                .minuteAddLimit(2)           // 每分钟最多 2 人
                .addIntervalMin(30)          // 最小间隔 30 秒
                .addIntervalMax(120)         // 最大间隔 120 秒 (随机)
                .messageDailyLimit(200)      // 每日最多 200 条消息
                .accountPoolSize(3)          // 默认 3 个账号
                .enableRotation(true)        // 启用轮换
                .rotationThreshold(80)       // 达到 80% 上限后轮换
                .build();
    }
    
    /**
     * 创建激进配置 (风险较高)
     */
    public static RateLimitConfig createAggressive() {
        return RateLimitConfig.builder()
                .dailyAddLimit(100)
                .hourlyAddLimit(15)
                .minuteAddLimit(3)
                .addIntervalMin(20)
                .addIntervalMax(60)
                .messageDailyLimit(500)
                .accountPoolSize(10)
                .enableRotation(true)
                .rotationThreshold(90)
                .build();
    }
    
    /**
     * 创建保守配置 (最安全)
     */
    public static RateLimitConfig createConservative() {
        return RateLimitConfig.builder()
                .dailyAddLimit(30)
                .hourlyAddLimit(5)
                .minuteAddLimit(1)
                .addIntervalMin(60)
                .addIntervalMax(180)
                .messageDailyLimit(100)
                .accountPoolSize(5)
                .enableRotation(true)
                .rotationThreshold(50)
                .build();
    }
    
    /**
     * 检查是否超过每日限制
     */
    public boolean isDailyLimitReached(int currentCount) {
        return currentCount >= dailyAddLimit;
    }
    
    /**
     * 检查是否超过每小时限制
     */
    public boolean isHourlyLimitReached(int currentCount) {
        return currentCount >= hourlyAddLimit;
    }
    
    /**
     * 获取随机间隔时间 (秒)
     */
    public int getRandomInterval() {
        return (int)(addIntervalMin + Math.random() * (addIntervalMax - addIntervalMin));
    }
}
