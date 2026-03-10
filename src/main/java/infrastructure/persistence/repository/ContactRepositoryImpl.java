package com.wechat.acquisition.infrastructure.persistence.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wechat.acquisition.domain.acquisition.Contact;
import com.wechat.acquisition.domain.acquisition.ContactStatus;
import com.wechat.acquisition.domain.acquisition.repository.ContactRepository;
import com.wechat.acquisition.infrastructure.persistence.mapper.ContactMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 联系人 Repository 实现
 */
@Repository
@RequiredArgsConstructor
public class ContactRepositoryImpl implements ContactRepository {
    private static final Logger log = LoggerFactory.getLogger(ContactRepositoryImpl.class);
    
    private final ContactMapper contactMapper;
    
    @Override
    public Optional<Contact> findById(String id) {
        Contact contact = contactMapper.selectById(id);
        return Optional.ofNullable(contact);
    }
    
    @Override
    public Optional<Contact> findByPhoneNumber(String phoneNumber) {
        Contact contact = contactMapper.selectByPhoneNumber(phoneNumber);
        return Optional.ofNullable(contact);
    }
    
    @Override
    public List<Contact> findByStatus(ContactStatus status) {
        return contactMapper.selectByStatus(status.name());
    }
    
    @Override
    public List<Contact> findByCampaignId(String campaignId, int page, int size) {
        // TODO: 实现分页查询
        return List.of();
    }
    
    @Override
    public List<Contact> findPendingAddFriends(int limit) {
        return contactMapper.selectPendingAddFriends(limit);
    }
    
    @Override
    public Contact save(Contact contact) {
        if (contact.getId() == null || contactMapper.selectById(contact.getId()) == null) {
            contactMapper.insert(contact);
        } else {
            contactMapper.updateById(contact);
        }
        return contact;
    }
    
    @Override
    public List<Contact> saveAll(List<Contact> contacts) {
        if (contacts.isEmpty()) {
            return contacts;
        }
        contactMapper.batchInsert(contacts);
        return contacts;
    }
    
    @Override
    public void deleteById(String id) {
        contactMapper.deleteById(id);
    }
    
    @Override
    public long count() {
        return contactMapper.selectCount(null);
    }
    
    @Override
    public long countByStatus(ContactStatus status) {
        return contactMapper.selectCount(null); // TODO: 添加状态条件
    }
    
    @Override
    public long countByCampaignId(String campaignId) {
        return contactMapper.selectCount(null); // TODO: 添加活动 ID 条件
    }
}
