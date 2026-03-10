package com.wechat.acquisition.application.service;

import com.wechat.acquisition.domain.acquisition.Campaign;
import com.wechat.acquisition.domain.acquisition.CampaignStatus;
import com.wechat.acquisition.domain.acquisition.DataSource;
import com.wechat.acquisition.domain.acquisition.TargetAudience;
import com.wechat.acquisition.domain.acquisition.event.CampaignStartedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 获客活动应用服务
 * 
 * 职责：编排获客活动相关的业务逻辑
 */
@Service
@RequiredArgsConstructor
public class CampaignApplicationService {
    
    // TODO: 注入 Repository
    // private final CampaignRepository campaignRepository;
    // private final DomainEventPublisher eventPublisher;
    
    /**
     * 创建获客活动
     */
    @Transactional
    public Campaign createCampaign(String name, DataSource dataSource, TargetAudience targetAudience) {
        Campaign campaign = Campaign.create(name, dataSource, targetAudience);
        // campaignRepository.save(campaign);
        return campaign;
    }
    
    /**
     * 启动活动
     */
    @Transactional
    public void startCampaign(String campaignId) {
        // Campaign campaign = campaignRepository.findById(campaignId);
        // campaign.start();
        // campaignRepository.save(campaign);
        // eventPublisher.publish(new CampaignStartedEvent(campaignId));
    }
    
    /**
     * 暂停活动
     */
    @Transactional
    public void pauseCampaign(String campaignId) {
        // Campaign campaign = campaignRepository.findById(campaignId);
        // campaign.pause();
        // campaignRepository.save(campaign);
    }
    
    /**
     * 停止活动
     */
    @Transactional
    public void stopCampaign(String campaignId) {
        // Campaign campaign = campaignRepository.findById(campaignId);
        // campaign.stop();
        // campaignRepository.save(campaign);
    }
    
    /**
     * 获取活动列表
     */
    @Transactional(readOnly = true)
    public List<Campaign> listCampaigns(CampaignStatus status) {
        // return campaignRepository.findByStatus(status);
        return List.of();
    }
    
    /**
     * 获取活动详情
     */
    @Transactional(readOnly = true)
    public Campaign getCampaign(String campaignId) {
        // return campaignRepository.findById(campaignId);
        return null;
    }
}
