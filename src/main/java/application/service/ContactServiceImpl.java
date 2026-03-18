package com.wechat.acquisition.application.service;

import com.wechat.acquisition.infrastructure.persistence.entity.ContactEntity;
import com.wechat.acquisition.infrastructure.persistence.mapper.ContactMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactServiceImpl {
    
    private final ContactMapper contactMapper;
    
    /**
     * 分页查询联系人
     */
    @Transactional(readOnly = true)
    public Map<String, Object> listContacts(String status, String keyword, int page, int size) {
        log.info("查询联系人：status={}, keyword={}, page={}, size={}", status, keyword, page, size);
        
        LambdaQueryWrapper<ContactEntity> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ContactEntity::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                .like(ContactEntity::getPhoneNumber, keyword)
                .or()
                .like(ContactEntity::getName, keyword)
            );
        }
        wrapper.orderByDesc(ContactEntity::getCreatedAt);
        
        Page<ContactEntity> pageResult = contactMapper.selectPage(
            new Page<>(page, size),
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
     * 获取联系人详情
     */
    @Transactional(readOnly = true)
    public ContactEntity getContact(String id) {
        return contactMapper.selectById(id);
    }
    
    /**
     * 创建联系人
     */
    @Transactional
    public ContactEntity createContact(String phoneNumber, String name, String campaignId) {
        log.info("创建联系人：phone={}, name={}, campaignId={}", phoneNumber, name, campaignId);
        
        ContactEntity contact = new ContactEntity();
        contact.setId("c_" + System.currentTimeMillis());
        contact.setPhoneNumber(phoneNumber);
        contact.setName(name);
        contact.setCampaignId(campaignId);
        contact.setStatus("NEW");
        contact.setCreatedAt(LocalDateTime.now());
        contact.setUpdatedAt(LocalDateTime.now());
        
        contactMapper.insert(contact);
        return contact;
    }
    
    /**
     * 批量创建联系人
     */
    @Transactional
    public List<ContactEntity> batchCreateContacts(List<Map<String, String>> contacts, String campaignId) {
        log.info("批量创建联系人：count={}, campaignId={}", contacts.size(), campaignId);
        
        List<ContactEntity> result = new ArrayList<>();
        for (Map<String, String> data : contacts) {
            String phone = data.get("phoneNumber");
            if (phone != null && !phone.isEmpty()) {
                ContactEntity contact = createContact(phone, data.getOrDefault("name", ""), campaignId);
                result.add(contact);
            }
        }
        return result;
    }
    
    /**
     * 更新联系人标签
     */
    @Transactional
    public ContactEntity updateTags(String id, Map<String, String> tags) {
        log.info("更新联系人标签：id={}, tags={}", id, tags);
        
        ContactEntity contact = contactMapper.selectById(id);
        if (contact == null) {
            throw new IllegalStateException("联系人不存在：" + id);
        }
        
        // 转换为 JSON 字符串存储
        String tagsJson = convertTagsToJson(tags);
        contact.setTags(tagsJson);
        contact.setUpdatedAt(LocalDateTime.now());
        
        contactMapper.updateById(contact);
        return contact;
    }
    
    /**
     * 更新联系人状态
     */
    @Transactional
    public ContactEntity updateStatus(String id, String status) {
        log.info("更新联系人状态：id={}, status={}", id, status);
        
        ContactEntity contact = contactMapper.selectById(id);
        if (contact == null) {
            throw new IllegalStateException("联系人不存在：" + id);
        }
        
        contact.setStatus(status);
        contact.setUpdatedAt(LocalDateTime.now());
        if ("ADDED".equals(status) || "CONVERSING".equals(status)) {
            contact.setLastContactAt(LocalDateTime.now());
        }
        
        contactMapper.updateById(contact);
        return contact;
    }
    
    /**
     * 删除联系人
     */
    @Transactional
    public void deleteContact(String id) {
        log.info("删除联系人：id={}", id);
        contactMapper.deleteById(id);
    }
    
    /**
     * 批量删除
     */
    @Transactional
    public Map<String, Object> batchDelete(List<String> ids) {
        log.info("批量删除联系人：count={}", ids.size());
        
        int successCount = 0;
        for (String id : ids) {
            try {
                deleteContact(id);
                successCount++;
            } catch (Exception e) {
                log.error("删除失败：id={}", id, e);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", ids.size() - successCount);
        return result;
    }
    
    /**
     * 获取统计信息
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getStats() {
        log.info("获取联系人统计");
        
        long total = contactMapper.selectCount(null);
        
        LambdaQueryWrapper<ContactEntity> addedWrapper = new LambdaQueryWrapper<>();
        addedWrapper.eq(ContactEntity::getStatus, "ADDED");
        long added = contactMapper.selectCount(addedWrapper);
        
        LambdaQueryWrapper<ContactEntity> conversingWrapper = new LambdaQueryWrapper<>();
        conversingWrapper.eq(ContactEntity::getStatus, "CONVERSING");
        long conversing = contactMapper.selectCount(conversingWrapper);
        
        LambdaQueryWrapper<ContactEntity> highIntentWrapper = new LambdaQueryWrapper<>();
        highIntentWrapper.eq(ContactEntity::getIntentLevel, "A");
        long highIntent = contactMapper.selectCount(highIntentWrapper);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("added", added);
        stats.put("conversing", conversing);
        stats.put("highIntent", highIntent);
        
        return stats;
    }
    
    private String convertTagsToJson(Map<String, String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            if (sb.length() > 1) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }
}
