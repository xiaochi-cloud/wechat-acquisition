package com.wechat.acquisition.domain.acquisition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class Campaign {
    private static final Logger log = LoggerFactory.getLogger(Campaign.class);
    private String id;
    private String name;
    private CampaignStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Campaign() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public CampaignStatus getStatus() { return status; }
    public void setStatus(CampaignStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public static Campaign create(String name) {
        Campaign c = new Campaign();
        c.setId(java.util.UUID.randomUUID().toString());
        c.setName(name);
        c.setStatus(CampaignStatus.DRAFT);
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        return c;
    }
    
    public void start() {
        if (this.status != CampaignStatus.DRAFT) throw new IllegalStateException("只有草稿状态的活动可以启动");
        this.status = CampaignStatus.RUNNING;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void pause() {
        if (this.status != CampaignStatus.RUNNING) throw new IllegalStateException("只有运行中的活动可以暂停");
        this.status = CampaignStatus.PAUSED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void stop() {
        this.status = CampaignStatus.STOPPED;
        this.updatedAt = LocalDateTime.now();
    }
}

public enum CampaignStatus {
    DRAFT("草稿"), RUNNING("运行中"), PAUSED("已暂停"), STOPPED("已停止"), COMPLETED("已完成");
    private final String description;
    CampaignStatus(String description) { this.description = description; }
    public String getDescription() { return description; }
}
