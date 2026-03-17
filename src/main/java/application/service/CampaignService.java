package com.wechat.acquisition.application.service;

import com.wechat.acquisition.domain.acquisition.Campaign;
import com.wechat.acquisition.domain.acquisition.CampaignStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 获客活动服务
 * 
 * 实现活动管理核心功能
 */
@Slf4j
@Service
public class CampaignService {
    
    // 内存存储 (临时，后续替换为数据库)
    private final Map<String, Campaign> campaignStore = new ConcurrentHashMap<>();
    
    /**
     * 创建活动
     */
    public Campaign createCampaign(String name) {
        log.info("创建活动：name={}", name);
        
        Campaign campaign = Campaign.create(name);
        campaignStore.put(campaign.getId(), campaign);
        
        log.info("活动创建成功：id={}, name={}", campaign.getId(), name);
        return campaign;
    }
    
    /**
     * 启动活动
     */
    public void startCampaign(String campaignId) {
        log.info("启动活动：id={}", campaignId);
        
        Campaign campaign = getCampaign(campaignId);
        campaign.start();
        
        log.info("活动启动成功：id={}", campaignId);
    }
    
    /**
     * 暂停活动
     */
    public void pauseCampaign(String campaignId) {
        log.info("暂停活动：id={}", campaignId);
        
        Campaign campaign = getCampaign(campaignId);
        campaign.pause();
        
        log.info("活动暂停成功：id={}", campaignId);
    }
    
    /**
     * 停止活动
     */
    public void stopCampaign(String campaignId) {
        log.info("停止活动：id={}", campaignId);
        
        Campaign campaign = getCampaign(campaignId);
        campaign.stop();
        
        log.info("活动停止成功：id={}", campaignId);
    }
    
    /**
     * 获取活动列表
     */
    public Map<String, Object> listCampaigns(CampaignStatus status, int page, int size) {
        log.debug("查询活动列表：status={}", status);
        
        List<Campaign> all = new ArrayList<>(campaignStore.values());
        
        // 过滤
        if (status != null) {
            all = all.stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
        }
        
        // 排序
        all.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        
        // 分页
        int total = all.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        
        List<Campaign> pageData = fromIndex < total ? 
            all.subList(fromIndex, toIndex) : new ArrayList<>();
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("data", pageData);
        
        return result;
    }
    
    /**
     * 获取活动详情
     */
    public Campaign getCampaign(String campaignId) {
        log.debug("查询活动详情：id={}", campaignId);
        
        Campaign campaign = campaignStore.get(campaignId);
        if (campaign == null) {
            log.warn("活动不存在：id={}", campaignId);
            throw new IllegalStateException("活动不存在：" + campaignId);
        }
        
        return campaign;
    }
    
    /**
     * 获取活动统计
     */
    public Map<String, Object> getCampaignStats(String campaignId) {
        log.info("获取活动统计：id={}", campaignId);
        
        Campaign campaign = getCampaign(campaignId);
        
        return Map.of(
            "id", campaign.getId(),
            "name", campaign.getName(),
            "status", campaign.getStatus().name(),
            "contactCount", 0,
            "addedCount", 0,
            "conversationCount", 0,
            "createdAt", campaign.getCreatedAt().toString()
        );
    }
    
    /**
     * 删除活动
     */
    public void deleteCampaign(String campaignId) {
        log.info("删除活动：id={}", campaignId);
        
        Campaign campaign = campaignStore.remove(campaignId);
        if (campaign == null) {
            throw new IllegalStateException("活动不存在：" + campaignId);
        }
        
        log.info("活动删除成功：id={}", campaignId);
    }
}
