package com.wechat.acquisition.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 联系人实体类
 * 对应数据库表：contact
 */
@TableName("contact")
public class ContactEntity {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    @TableField("phone_number")
    private String phoneNumber;
    
    @TableField("wechat_id")
    private String wechatId;
    
    @TableField("name")
    private String name;
    
    @TableField("status")
    private String status;
    
    @TableField("campaign_id")
    private String campaignId;
    
    @TableField("intent_score")
    private Double intentScore;
    
    @TableField("intent_level")
    private String intentLevel;
    
    @TableField("tags")
    private String tags;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableField("last_contact_at")
    private LocalDateTime lastContactAt;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getWechatId() { return wechatId; }
    public void setWechatId(String wechatId) { this.wechatId = wechatId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }
    public Double getIntentScore() { return intentScore; }
    public void setIntentScore(Double intentScore) { this.intentScore = intentScore; }
    public String getIntentLevel() { return intentLevel; }
    public void setIntentLevel(String intentLevel) { this.intentLevel = intentLevel; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getLastContactAt() { return lastContactAt; }
    public void setLastContactAt(LocalDateTime lastContactAt) { this.lastContactAt = lastContactAt; }
}
