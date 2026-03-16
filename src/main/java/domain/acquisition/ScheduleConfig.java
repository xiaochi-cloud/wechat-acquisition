package com.wechat.acquisition.domain.acquisition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleConfig {
    private static final Logger log = LoggerFactory.getLogger(ScheduleConfig.class);
    private ScheduleType type;
    private String cronExpression;
    private Integer batchSize;
    private Integer intervalSeconds;
    private String startTime;
    private String endTime;
    private Integer retryTimes;
    private Boolean enabled;
    
    public ScheduleConfig() {}
    public ScheduleType getType() { return type; }
    public void setType(ScheduleType type) { this.type = type; }
    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
    public Integer getBatchSize() { return batchSize; }
    public void setBatchSize(Integer batchSize) { this.batchSize = batchSize; }
    public Integer getIntervalSeconds() { return intervalSeconds; }
    public void setIntervalSeconds(Integer intervalSeconds) { this.intervalSeconds = intervalSeconds; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public Integer getRetryTimes() { return retryTimes; }
    public void setRetryTimes(Integer retryTimes) { this.retryTimes = retryTimes; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    
    public static ScheduleConfig createImmediate(Integer batchSize) {
        ScheduleConfig c = new ScheduleConfig();
        c.setType(ScheduleType.IMMEDIATE); c.setBatchSize(batchSize);
        c.setIntervalSeconds(60); c.setRetryTimes(3); c.setEnabled(true);
        return c;
    }
    public static ScheduleConfig createCron(String cronExpression, Integer batchSize) {
        ScheduleConfig c = new ScheduleConfig();
        c.setType(ScheduleType.CRON); c.setCronExpression(cronExpression);
        c.setBatchSize(batchSize); c.setIntervalSeconds(60); c.setRetryTimes(3); c.setEnabled(true);
        return c;
    }
}

enum ScheduleType { IMMEDIATE, ONCE, CRON, MANUAL }
