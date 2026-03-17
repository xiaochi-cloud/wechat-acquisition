package com.wechat.acquisition.interfaces.web.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 活动数据传输对象
 */
@Data
public class CampaignDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 活动 ID
     */
    private String id;
    
    /**
     * 活动名称
     */
    @NotNull(message = "活动名称不能为空")
    @Size(min = 1, max = 100, message = "活动名称长度必须在 1-100 之间")
    private String name;
    
    /**
     * 活动状态
     */
    @Pattern(regexp = "^(DRAFT|RUNNING|PAUSED|STOPPED|COMPLETED)$", message = "活动状态不正确")
    private String status;
    
    /**
     * 数据源 ID
     */
    private String dataSourceId;
    
    /**
     * 联系人总数
     */
    @Min(value = 0, message = "联系人数量不能小于 0")
    private Integer contactCount;
    
    /**
     * 已添加好友数
     */
    @Min(value = 0, message = "已添加数量不能小于 0")
    private Integer addedCount;
    
    /**
     * 会话数
     */
    @Min(value = 0, message = "会话数不能小于 0")
    private Integer conversationCount;
}
