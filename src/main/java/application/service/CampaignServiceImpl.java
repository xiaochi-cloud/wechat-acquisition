package com.wechat.acquisition.application.service;

import com.wechat.acquisition.infrastructure.persistence.entity.CampaignEntity;
import com.wechat.acquisition.infrastructure.persistence.mapper.CampaignMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignServiceImpl {
    
    private final CampaignMapper campaignMapper;
    
    /**
     * 获取活动列表
     */
    @Transactional(readOnly = true)
    public Map<String, Object> listCampaigns(String status, int page, int size) {
        log.info("查询活动列表：status={}, page={}, size={}", status, page, size);
        
        LambdaQueryWrapper<CampaignEntity> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(CampaignEntity::getStatus, status);
        }
        wrapper.orderByDesc(CampaignEntity::getCreatedAt);
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CampaignEntity> pageResult = 
            campaignMapper.selectPage(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size),
                wrapper
            );
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("page", page);
        result.put("size", size);
        result.put("data", pageResult.getRecords());
        
        return result;
    }
    
    /**
     * 获取活动详情
     */
    @Transactional(readOnly = true)
    public CampaignEntity getCampaign(String id) {
        return campaignMapper.selectById(id);
    }
    
    /**
     * 创建活动
     */
    @Transactional
    public CampaignEntity createCampaign(String name) {
        log.info("创建活动：name={}", name);
        
        CampaignEntity campaign = new CampaignEntity();
        campaign.setId("camp_" + System.currentTimeMillis());
        campaign.setName(name);
        campaign.setStatus("DRAFT");
        campaign.setContactCount(0);
        campaign.setAddedCount(0);
        campaign.setConversationCount(0);
        campaign.setCreatedAt(LocalDateTime.now());
        campaign.setUpdatedAt(LocalDateTime.now());
        
        campaignMapper.insert(campaign);
        return campaign;
    }
    
    /**
     * 启动活动
     */
    @Transactional
    public void startCampaign(String id) {
        log.info("启动活动：id={}", id);
        
        CampaignEntity campaign = campaignMapper.selectById(id);
        if (campaign == null) {
            throw new IllegalStateException("活动不存在：" + id);
        }
        
        campaign.setStatus("RUNNING");
        campaign.setUpdatedAt(LocalDateTime.now());
        campaignMapper.updateById(campaign);
    }
    
    /**
     * 暂停活动
     */
    @Transactional
    public void pauseCampaign(String id) {
        log.info("暂停活动：id={}", id);
        
        CampaignEntity campaign = campaignMapper.selectById(id);
        if (campaign == null) {
            throw new IllegalStateException("活动不存在：" + id);
        }
        
        campaign.setStatus("PAUSED");
        campaign.setUpdatedAt(LocalDateTime.now());
        campaignMapper.updateById(campaign);
    }
    
    /**
     * 停止活动
     */
    @Transactional
    public void stopCampaign(String id) {
        log.info("停止活动：id={}", id);
        
        CampaignEntity campaign = campaignMapper.selectById(id);
        if (campaign == null) {
            throw new IllegalStateException("活动不存在：" + id);
        }
        
        campaign.setStatus("STOPPED");
        campaign.setUpdatedAt(LocalDateTime.now());
        campaignMapper.updateById(campaign);
    }
    
    /**
     * 获取活动统计
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCampaignStats(String id) {
        log.info("获取活动统计：id={}", id);
        
        CampaignEntity campaign = campaignMapper.selectById(id);
        if (campaign == null) {
            throw new IllegalStateException("活动不存在：" + id);
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("id", campaign.getId());
        stats.put("name", campaign.getName());
        stats.put("status", campaign.getStatus());
        stats.put("contactCount", campaign.getContactCount());
        stats.put("addedCount", campaign.getAddedCount());
        stats.put("conversationCount", campaign.getConversationCount());
        stats.put("createdAt", campaign.getCreatedAt().toString());
        
        return stats;
    }
}
