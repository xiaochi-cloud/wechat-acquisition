package com.wechat.acquisition.domain.acquisition;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 获客活动聚合根
 * 
 * 职责：管理一次完整的获客任务，包括数据源、目标人群、调度策略
 */
@Data
@Builder
public class Campaign {
    
    private String id;
    private String name;
    private CampaignStatus status;
    private DataSource dataSource;
    private TargetAudience targetAudience;
    private ScheduleConfig scheduleConfig;
    private RateLimitConfig rateLimitConfig;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 创建新活动
     */
    public static Campaign create(String name, DataSource dataSource, TargetAudience targetAudience) {
        return Campaign.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .dataSource(dataSource)
                .targetAudience(targetAudience)
                .status(CampaignStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 启动活动
     */
    public void start() {
        if (this.status != CampaignStatus.DRAFT) {
            throw new IllegalStateException("只有草稿状态的活动可以启动");
        }
        this.status = CampaignStatus.RUNNING;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 暂停活动
     */
    public void pause() {
        if (this.status != CampaignStatus.RUNNING) {
            throw new IllegalStateException("只有运行中的活动可以暂停");
        }
        this.status = CampaignStatus.PAUSED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 停止活动
     */
    public void stop() {
        this.status = CampaignStatus.STOPPED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 添加联系人到活动
     */
    public void addContact(Contact contact) {
        // 领域逻辑：验证联系人是否符合目标人群
        if (!targetAudience.matches(contact)) {
            throw new IllegalArgumentException("联系人不符合目标人群条件");
        }
        // 添加到数据源
        this.dataSource.addContact(contact);
    }
}

/**
 * 活动状态
 */
@Getter
public enum CampaignStatus {
    DRAFT("草稿"),
    RUNNING("运行中"),
    PAUSED("已暂停"),
    STOPPED("已停止"),
    COMPLETED("已完成");
    
    private final String description;
    
    CampaignStatus(String description) {
        this.description = description;
    }
}
