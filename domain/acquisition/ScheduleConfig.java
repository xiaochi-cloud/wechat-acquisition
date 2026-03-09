package com.wechat.acquisition.domain.acquisition;

import lombok.Builder;
import lombok.Data;

/**
 * 调度配置
 * 
 * 职责：定义获客任务的执行时间和频率
 */
@Data
@Builder
public class ScheduleConfig {
    
    private ScheduleType type;          // 调度类型
    private String cronExpression;      // Cron 表达式 (定时任务)
    private Integer batchSize;          // 每批次处理数量
    private Integer intervalSeconds;    // 批次间隔 (秒)
    private String startTime;           // 开始时间 (ISO-8601)
    private String endTime;             // 结束时间 (ISO-8601)
    private Integer retryTimes;         // 失败重试次数
    private Boolean enabled;            // 是否启用
    
    /**
     * 创建立即执行的配置
     */
    public static ScheduleConfig createImmediate(Integer batchSize) {
        return ScheduleConfig.builder()
                .type(ScheduleType.IMMEDIATE)
                .batchSize(batchSize)
                .intervalSeconds(60)
                .retryTimes(3)
                .enabled(true)
                .build();
    }
    
    /**
     * 创建定时任务配置
     */
    public static ScheduleConfig createCron(String cronExpression, Integer batchSize) {
        return ScheduleConfig.builder()
                .type(ScheduleType.CRON)
                .cronExpression(cronExpression)
                .batchSize(batchSize)
                .intervalSeconds(60)
                .retryTimes(3)
                .enabled(true)
                .build();
    }
    
    /**
     * 创建一次性任务配置
     */
    public static ScheduleConfig createOnce(String startTime, Integer batchSize) {
        return ScheduleConfig.builder()
                .type(ScheduleType.ONCE)
                .startTime(startTime)
                .batchSize(batchSize)
                .intervalSeconds(60)
                .retryTimes(3)
                .enabled(true)
                .build();
    }
}

/**
 * 调度类型
 */
public enum ScheduleType {
    IMMEDIATE,  // 立即执行
    ONCE,       // 一次性任务
    CRON,       // 定时任务
    MANUAL      // 手动触发
}
