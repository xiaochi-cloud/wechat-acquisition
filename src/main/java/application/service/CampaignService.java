package com.wechat.acquisition.application.service;

import com.wechat.acquisition.domain.acquisition.Campaign;
import com.wechat.acquisition.domain.acquisition.CampaignStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获客活动服务
 * 
 * 添加事务管理和日志记录
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignService {
    
    // 内存存储 (临时，后续替换为数据库)
    private final Map<String, Campaign> campaignStore = new ConcurrentHashMap<>();
    
    /**
     * 创建活动
     */
    @Transactional(rollbackFor = Exception.class)
    public Campaign createCampaign(String name) {
        log.info("创建活动：name={}", name);
        
        try {
            Campaign campaign = Campaign.create(name);
            campaignStore.put(campaign.getId(), campaign);
            
            log.info("活动创建成功：id={}, name={}", campaign.getId(), name);
            return campaign;
            
        } catch (Exception e) {
            log.error("创建活动失败：name={}", name, e);
            throw e;
        }
    }
    
    /**
     * 启动活动
     */
    @Transactional(rollbackFor = Exception.class)
    public void startCampaign(String campaignId) {
        log.info("启动活动：id={}", campaignId);
        
        Campaign campaign = getCampaign(campaignId);
        campaign.start();
        
        log.info("活动启动成功：id={}", campaignId);
    }
    
    /**
     * 暂停活动
     */
    @Transactional(rollbackFor = Exception.class)
    public void pauseCampaign(String campaignId) {
        log.info("暂停活动：id={}", campaignId);
        
        Campaign campaign = getCampaign(campaignId);
        campaign.pause();
        
        log.info("活动暂停成功：id={}", campaignId);
    }
    
    /**
     * 停止活动
     */
    @Transactional(rollbackFor = Exception.class)
    public void stopCampaign(String campaignId) {
        log.info("停止活动：id={}", campaignId);
        
        Campaign campaign = getCampaign(campaignId);
        campaign.stop();
        
        log.info("活动停止成功：id={}", campaignId);
    }
    
    /**
     * 获取活动列表
     */
    @Transactional(readOnly = true)
    public List<Campaign> listCampaigns(CampaignStatus status) {
        log.debug("查询活动列表：status={}", status);
        
        List<Campaign> result = new ArrayList<>();
        for (Campaign campaign : campaignStore.values()) {
            if (status == null || campaign.getStatus() == status) {
                result.add(campaign);
            }
        }
        
        log.info("查询活动列表完成：count={}", result.size());
        return result;
    }
    
    /**
     * 获取活动详情
     */
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
}
