package com.wechat.acquisition.domain.acquisition.repository;

import com.wechat.acquisition.domain.acquisition.Campaign;
import com.wechat.acquisition.domain.acquisition.CampaignStatus;

import java.util.List;
import java.util.Optional;

/**
 * 获客活动 Repository 接口
 */
public interface CampaignRepository {
    
    /**
     * 根据 ID 查询
     */
    Optional<Campaign> findById(String id);
    
    /**
     * 根据状态查询
     */
    List<Campaign> findByStatus(CampaignStatus status);
    
    /**
     * 查询所有活动
     */
    List<Campaign> findAll(int page, int size);
    
    /**
     * 保存
     */
    Campaign save(Campaign campaign);
    
    /**
     * 删除
     */
    void deleteById(String id);
    
    /**
     * 统计数量
     */
    long count();
    
    /**
     * 统计某状态的数量
     */
    long countByStatus(CampaignStatus status);
}
