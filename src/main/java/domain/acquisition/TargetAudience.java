package com.wechat.acquisition.domain.acquisition;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 目标人群定义
 * 
 * 职责：定义获客活动的目标用户条件，用于过滤和分层
 */
@Data
@Builder
public class TargetAudience {
    
    private List<String> targetCities;          // 目标城市
    private List<String> targetIndustries;      // 目标行业
    private AgeRange ageRange;                  // 年龄范围
    private List<String> excludePhones;         // 排除手机号
    private Map<String, Object> customFilters;  // 自定义过滤条件
    
    /**
     * 判断联系人是否符合目标人群
     */
    public boolean matches(Contact contact) {
        if (contact == null || contact.getProfileData() == null) {
            return true; // 无画像数据时默认通过
        }
        
        ProfileData profile = contact.getProfileData();
        
        // 城市过滤
        if (targetCities != null && !targetCities.isEmpty()) {
            if (profile.getCity() == null || !targetCities.contains(profile.getCity())) {
                return false;
            }
        }
        
        // 行业过滤
        if (targetIndustries != null && !targetIndustries.isEmpty()) {
            if (profile.getIndustry() == null || !targetIndustries.contains(profile.getIndustry())) {
                return false;
            }
        }
        
        // 年龄过滤
        if (ageRange != null && profile.getAge() != null) {
            if (!ageRange.contains(profile.getAge())) {
                return false;
            }
        }
        
        // 排除手机号
        if (excludePhones != null && excludePhones.contains(contact.getPhoneNumber())) {
            return false;
        }
        
        return true;
    }
}

/**
 * 年龄范围
 */
@Data
@Builder
static class AgeRange {
    private Integer minAge;
    private Integer maxAge;
    
    public boolean contains(Integer age) {
        if (age == null) return true;
        if (minAge != null && age < minAge) return false;
        if (maxAge != null && age > maxAge) return false;
        return true;
    }
}
