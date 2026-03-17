package com.wechat.acquisition.interfaces.web.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 联系人数据传输对象
 * 
 * 用于 API 请求和响应的数据验证
 */
@Data
public class ContactDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 联系人 ID (更新时需要)
     */
    private String id;
    
    /**
     * 手机号
     */
    @NotNull(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;
    
    /**
     * 姓名
     */
    @Size(max = 50, message = "姓名长度不能超过 50 个字符")
    private String name;
    
    /**
     * 企微 ID
     */
    private String wechatId;
    
    /**
     * 活动 ID
     */
    private String campaignId;
    
    /**
     * 标签 (JSON 格式)
     */
    private String tags;
    
    /**
     * 意向分数
     */
    @DecimalMin(value = "0", message = "意向分数不能小于 0")
    @DecimalMax(value = "100", message = "意向分数不能大于 100")
    private Double intentScore;
    
    /**
     * 意向等级
     */
    @Pattern(regexp = "^[ABCD]$", message = "意向等级必须是 A/B/C/D")
    private String intentLevel;
}
