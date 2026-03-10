package com.wechat.acquisition.domain.acquisition.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wechat.acquisition.domain.acquisition.Contact;
import com.wechat.acquisition.domain.acquisition.ContactStatus;

import java.util.List;
import java.util.Optional;

/**
 * 联系人 Repository 接口
 */
public interface ContactRepository {
    
    /**
     * 根据 ID 查询
     */
    Optional<Contact> findById(String id);
    
    /**
     * 根据手机号查询
     */
    Optional<Contact> findByPhoneNumber(String phoneNumber);
    
    /**
     * 根据状态查询
     */
    List<Contact> findByStatus(ContactStatus status);
    
    /**
     * 根据活动 ID 查询
     */
    List<Contact> findByCampaignId(String campaignId, int page, int size);
    
    /**
     * 查询待添加好友的联系人
     */
    List<Contact> findPendingAddFriends(int limit);
    
    /**
     * 保存
     */
    Contact save(Contact contact);
    
    /**
     * 批量保存
     */
    List<Contact> saveAll(List<Contact> contacts);
    
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
    long countByStatus(ContactStatus status);
    
    /**
     * 统计某活动的数量
     */
    long countByCampaignId(String campaignId);
}
