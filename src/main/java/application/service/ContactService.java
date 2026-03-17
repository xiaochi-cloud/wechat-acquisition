package com.wechat.acquisition.application.service;

import com.wechat.acquisition.domain.acquisition.Contact;
import com.wechat.acquisition.domain.acquisition.ContactStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 联系人服务
 * 
 * 实现联系人管理核心功能
 */
@Slf4j
@Service
public class ContactService {
    
    // 内存存储 (临时，后续替换为数据库)
    private final Map<String, Contact> contactStore = new ConcurrentHashMap<>();
    
    /**
     * 创建联系人
     */
    public Contact createContact(String phoneNumber, String name) {
        log.info("创建联系人：phone={}, name={}", phoneNumber, name);
        
        Contact contact = Contact.create(phoneNumber);
        contact.setName(name);
        contactStore.put(contact.getId(), contact);
        
        log.info("联系人创建成功：id={}", contact.getId());
        return contact;
    }
    
    /**
     * 批量创建联系人
     */
    public List<Contact> batchCreateContacts(List<Map<String, String>> contacts) {
        log.info("批量创建联系人：count={}", contacts.size());
        
        List<Contact> result = new ArrayList<>();
        for (Map<String, String> data : contacts) {
            String phone = data.get("phoneNumber");
            String name = data.get("name");
            if (phone != null && !phone.isEmpty()) {
                Contact contact = createContact(phone, name != null ? name : "");
                result.add(contact);
            }
        }
        
        log.info("批量创建完成：success={}", result.size());
        return result;
    }
    
    /**
     * 导入联系人 (Excel)
     */
    public Map<String, Object> importContacts(List<Map<String, String>> contacts, String campaignId) {
        log.info("导入联系人：count={}, campaignId={}", contacts.size(), campaignId);
        
        int totalCount = contacts.size();
        int successCount = 0;
        int failCount = 0;
        List<String> failedPhones = new ArrayList<>();
        
        for (Map<String, String> data : contacts) {
            String phone = data.get("phoneNumber");
            if (phone == null || phone.isEmpty()) {
                failCount++;
                failedPhones.add("空手机号");
                continue;
            }
            
            // 验证手机号格式
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                failCount++;
                failedPhones.add(phone);
                continue;
            }
            
            // 检查是否重复
            if (contactStore.values().stream()
                    .anyMatch(c -> c.getPhoneNumber().equals(phone))) {
                failCount++;
                failedPhones.add(phone + " (重复)");
                continue;
            }
            
            try {
                createContact(phone, data.getOrDefault("name", ""));
                successCount++;
            } catch (Exception e) {
                failCount++;
                failedPhones.add(phone + " (" + e.getMessage() + ")");
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", totalCount);
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("failedPhones", failedPhones.subList(0, Math.min(10, failedPhones.size())));
        
        log.info("导入完成：total={}, success={}, fail={}", totalCount, successCount, failCount);
        return result;
    }
    
    /**
     * 获取联系人列表
     */
    public Map<String, Object> getContactList(Map<String, String> params) {
        log.debug("查询联系人列表：params={}", params);
        
        int page = Integer.parseInt(params.getOrDefault("page", "1"));
        int size = Integer.parseInt(params.getOrDefault("size", "20"));
        String status = params.get("status");
        String campaignId = params.get("campaignId");
        String keyword = params.get("keyword");
        
        List<Contact> all = new ArrayList<>(contactStore.values());
        
        // 过滤
        if (status != null && !status.isEmpty()) {
            all = all.stream()
                .filter(c -> c.getStatus().name().equals(status))
                .collect(Collectors.toList());
        }
        
        if (campaignId != null && !campaignId.isEmpty()) {
            // TODO: 添加 campaignId 过滤
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            all = all.stream()
                .filter(c -> c.getPhoneNumber().contains(keyword) || 
                           (c.getName() != null && c.getName().contains(keyword)))
                .collect(Collectors.toList());
        }
        
        // 排序 (按创建时间倒序)
        all.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        
        // 分页
        int total = all.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        
        List<Contact> pageData = fromIndex < total ? 
            all.subList(fromIndex, toIndex) : new ArrayList<>();
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("data", pageData);
        
        log.info("查询完成：total={}, page={}", total, page);
        return result;
    }
    
    /**
     * 获取联系人详情
     */
    public Contact getContactDetail(String contactId) {
        log.debug("查询联系人详情：id={}", contactId);
        
        Contact contact = contactStore.get(contactId);
        if (contact == null) {
            log.warn("联系人不存在：id={}", contactId);
            throw new IllegalStateException("联系人不存在：" + contactId);
        }
        
        return contact;
    }
    
    /**
     * 更新联系人标签
     */
    public Contact updateContactTags(String contactId, Map<String, String> tags) {
        log.info("更新联系人标签：id={}, tags={}", contactId, tags);
        
        Contact contact = getContactDetail(contactId);
        
        if (contact.getTags() == null) {
            contact.setTags(new HashMap<>());
        }
        
        contact.getTags().putAll(tags);
        contact.recordContact();
        
        log.info("标签更新成功：id={}", contactId);
        return contact;
    }
    
    /**
     * 更新联系人状态
     */
    public Contact updateContactStatus(String contactId, ContactStatus status) {
        log.info("更新联系人状态：id={}, status={}", contactId, status);
        
        Contact contact = getContactDetail(contactId);
        contact.updateStatus(status);
        
        log.info("状态更新成功：id={}", contactId);
        return contact;
    }
    
    /**
     * 删除联系人
     */
    public void deleteContact(String contactId) {
        log.info("删除联系人：id={}", contactId);
        
        Contact contact = contactStore.remove(contactId);
        if (contact == null) {
            log.warn("联系人不存在：id={}", contactId);
            throw new IllegalStateException("联系人不存在：" + contactId);
        }
        
        log.info("联系人删除成功：id={}", contactId);
    }
    
    /**
     * 批量删除联系人
     */
    public Map<String, Object> batchDeleteContacts(List<String> contactIds) {
        log.info("批量删除联系人：count={}", contactIds.size());
        
        int successCount = 0;
        int failCount = 0;
        
        for (String id : contactIds) {
            try {
                deleteContact(id);
                successCount++;
            } catch (Exception e) {
                failCount++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        
        return result;
    }
    
    /**
     * 获取联系人统计
     */
    public Map<String, Object> getContactStats() {
        log.info("获取联系人统计");
        
        Collection<Contact> all = contactStore.values();
        
        Map<ContactStatus, Long> statusCount = all.stream()
            .collect(Collectors.groupingBy(Contact::getStatus, Collectors.counting()));
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", all.size());
        result.put("byStatus", statusCount);
        result.put("newCount", statusCount.getOrDefault(ContactStatus.NEW, 0L));
        result.put("addedCount", statusCount.getOrDefault(ContactStatus.ADDED, 0L));
        result.put("conversingCount", statusCount.getOrDefault(ContactStatus.CONVERSING, 0L));
        
        return result;
    }
}
