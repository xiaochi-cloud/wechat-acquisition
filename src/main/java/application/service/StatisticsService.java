package com.wechat.acquisition.application.service;

import com.wechat.acquisition.infrastructure.persistence.mapper.ContactMapper;
import com.wechat.acquisition.infrastructure.persistence.mapper.CampaignMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 数据统计服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {
    
    private final ContactMapper contactMapper;
    private final CampaignMapper campaignMapper;
    
    /**
     * 获取仪表盘统计
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        log.info("获取仪表盘统计");
        
        long totalContacts = contactMapper.selectCount(null);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalContacts", totalContacts);
        
        // 按状态统计
        Map<String, Long> statusStats = new HashMap<>();
        statusStats.put("new", countByStatus("NEW"));
        statusStats.put("added", countByStatus("ADDED"));
        statusStats.put("conversing", countByStatus("CONVERSING"));
        stats.put("statusStats", statusStats);
        
        // 按意向等级统计
        Map<String, Long> intentStats = new HashMap<>();
        intentStats.put("A", countByIntentLevel("A"));
        intentStats.put("B", countByIntentLevel("B"));
        intentStats.put("C", countByIntentLevel("C"));
        intentStats.put("D", countByIntentLevel("D"));
        stats.put("intentStats", intentStats);
        
        // 近期趋势 (近 7 天)
        stats.put("trend", getTrendData(7));
        
        // 活动统计
        stats.put("campaignStats", getCampaignStats());
        
        return stats;
    }
    
    /**
     * 获取转化漏斗
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getConversionFunnel() {
        log.info("获取转化漏斗");
        
        long total = contactMapper.selectCount(null);
        long added = countByStatus("ADDED");
        long conversing = countByStatus("CONVERSING");
        long highIntent = countByIntentLevel("A");
        
        Map<String, Object> funnel = new LinkedHashMap<>();
        funnel.put("total", total);
        funnel.put("added", added);
        funnel.put("conversing", conversing);
        funnel.put("highIntent", highIntent);
        
        // 计算转化率
        Map<String, Object> rates = new HashMap<>();
        rates.put("addRate", total > 0 ? String.format("%.2f", (double) added / total * 100) + "%" : "0%");
        rates.put("converseRate", added > 0 ? String.format("%.2f", (double) conversing / added * 100) + "%" : "0%");
        rates.put("highIntentRate", conversing > 0 ? String.format("%.2f", (double) highIntent / conversing * 100) + "%" : "0%");
        
        Map<String, Object> result = new HashMap<>();
        result.put("funnel", funnel);
        result.put("rates", rates);
        
        return result;
    }
    
    /**
     * 获取趋势数据
     */
    private List<Map<String, Object>> getTrendData(int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = days - 1; i >= 0; i--) {
            String date = LocalDate.now().minusDays(i).format(formatter);
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date);
            dayData.put("contacts", 0);
            dayData.put("added", 0);
            trend.add(dayData);
        }
        
        return trend;
    }
    
    /**
     * 获取活动统计
     */
    private Map<String, Object> getCampaignStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalCampaigns = campaignMapper.selectCount(null);
        stats.put("total", totalCampaigns);
        stats.put("running", 0);
        stats.put("paused", 0);
        return stats;
    }
    
    /**
     * 按状态统计
     */
    private long countByStatus(String status) {
        LambdaQueryWrapper<com.wechat.acquisition.infrastructure.persistence.entity.ContactEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.wechat.acquisition.infrastructure.persistence.entity.ContactEntity::getStatus, status);
        return contactMapper.selectCount(wrapper);
    }
    
    /**
     * 按意向等级统计
     */
    private long countByIntentLevel(String level) {
        LambdaQueryWrapper<com.wechat.acquisition.infrastructure.persistence.entity.ContactEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.wechat.acquisition.infrastructure.persistence.entity.ContactEntity::getIntentLevel, level);
        return contactMapper.selectCount(wrapper);
    }
}
