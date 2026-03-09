package com.wechat.acquisition.domain.acquisition;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 联系人聚合根
 * 
 * 职责：管理用户核心信息，包括手机号、企微 ID、标签等
 */
@Data
@Builder
public class Contact {
    
    private String id;
    private String phoneNumber;
    private String weChatId;
    private String name;
    private ContactStatus status;
    private Map<String, String> tags;
    private ProfileData profileData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastContactAt;
    
    /**
     * 创建新联系人
     */
    public static Contact create(String phoneNumber) {
        return Contact.builder()
                .id(UUID.randomUUID().toString())
                .phoneNumber(phoneNumber)
                .status(ContactStatus.NEW)
                .tags(new HashMap<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 更新手机号
     */
    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 绑定企微 ID
     */
    public void bindWeChat(String weChatId) {
        this.weChatId = weChatId;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 添加标签
     */
    public void addTag(String key, String value) {
        this.tags.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新状态
     */
    public void updateStatus(ContactStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 记录联系时间
     */
    public void recordContact() {
        this.lastContactAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 是否已添加企微好友
     */
    public boolean isWeChatAdded() {
        return this.weChatId != null && !this.weChatId.isEmpty();
    }
}

/**
 * 联系人状态
 */
public enum ContactStatus {
    NEW,            // 新增
    IMPORTED,       // 已导入
    ADDING,         // 添加中
    ADDED,          // 已添加
    CONVERSING,     // 对话中
    SCORED,         // 已打分
    CONVERTED,      // 已转化
    INVALID,        // 无效
    BLOCKED         // 已拉黑
}

/**
 * 用户画像数据
 */
@Data
@Builder
public class ProfileData {
    private String gender;              // 性别
    private Integer age;                // 年龄
    private String city;                // 城市
    private String industry;            // 行业
    private String company;             // 公司
    private String position;            // 职位
    private Double incomeLevel;         // 收入水平
    private Map<String, Object> extras; // 扩展字段
}
