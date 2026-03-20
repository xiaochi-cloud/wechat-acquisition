package com.wechat.acquisition.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@TableName("operation_log")
public class OperationLogEntity {
    
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    @TableField("user_id")
    private String userId;
    
    @TableField("username")
    private String username;
    
    @TableField("operation")
    private String operation;
    
    @TableField("module")
    private String module;
    
    @TableField("ip_address")
    private String ipAddress;
    
    @TableField("user_agent")
    private String userAgent;
    
    @TableField("status")
    private String status;
    
    @TableField("message")
    private String message;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
