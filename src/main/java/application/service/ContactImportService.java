package com.wechat.acquisition.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wechat.acquisition.domain.acquisition.Contact;
import com.wechat.acquisition.domain.acquisition.ContactStatus;
import com.wechat.acquisition.domain.acquisition.event.ContactImportedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 联系人导入应用服务
 * 
 * 职责：处理 Excel/API/Webhook 等多渠道联系人导入
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContactImportService {
    private static final Logger log = LoggerFactory.getLogger(ContactImportService.class);
    
    // TODO: 注入 Repository
    // private final ContactRepository contactRepository;
    // private final DomainEventPublisher eventPublisher;
    
    /**
     * 从 Excel 导入联系人
     * 
     * @param file Excel 文件
     * @param campaignId 活动 ID
     * @return 导入结果
     */
    @Transactional
    public ImportResult importFromExcel(MultipartFile file, String campaignId) {
        log.info("开始从 Excel 导入联系人，文件：{}, 活动：{}", file.getOriginalFilename(), campaignId);
        
        List<Contact> contacts = new ArrayList<>();
        int totalCount = 0;
        int validCount = 0;
        int invalidCount = 0;
        
        try {
            // TODO: 使用 EasyExcel 解析文件
            // contacts = EasyExcel.read(file.getInputStream())
            //     .headRowNumber(1)
            //     .sheet()
            //     .doReadSync();
            
            // 模拟导入
            Contact contact = Contact.create("13800138000");
            contact.updateStatus(ContactStatus.IMPORTED);
            contacts.add(contact);
            
            totalCount = 1;
            validCount = 1;
            
            // 保存联系人
            // contacts.forEach(contactRepository::save);
            
            // 发布事件
            // contacts.forEach(c -> eventPublisher.publish(new ContactImportedEvent(
            //     c.getId(), c.getPhoneNumber(), campaignId, "excel")));
            
            log.info("Excel 导入完成，总数：{}, 有效：{}, 无效：{}", totalCount, validCount, invalidCount);
            
        } catch (Exception e) {
            log.error("Excel 导入失败", e);
            throw new ImportException("Excel 导入失败：" + e.getMessage(), e);
        }
        
        return new ImportResult(totalCount, validCount, invalidCount, contacts);
    }
    
    /**
     * 从 API 同步联系人
     */
    @Transactional
    public ImportResult importFromApi(String apiUrl, String apiKey, String campaignId) {
        log.info("开始从 API 同步联系人，URL: {}", apiUrl);
        
        // TODO: 调用外部 API 获取联系人列表
        // 解析响应，创建 Contact 对象
        // 保存并发布事件
        
        return new ImportResult(0, 0, 0, List.of());
    }
    
    /**
     * 批量创建联系人
     */
    @Transactional
    public List<Contact> batchCreate(List<String> phoneNumbers, String campaignId) {
        List<Contact> contacts = new ArrayList<>();
        
        for (String phone : phoneNumbers) {
            if (isValidPhoneNumber(phone)) {
                Contact contact = Contact.create(phone);
                contact.updateStatus(ContactStatus.IMPORTED);
                contacts.add(contact);
                // contactRepository.save(contact);
                // eventPublisher.publish(new ContactImportedEvent(contact.getId(), phone, campaignId, "batch"));
            }
        }
        
        return contacts;
    }
    
    /**
     * 验证手机号
     */
    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }
    
    /**
     * 导入结果
     */
    public record ImportResult(
        int totalCount,
        int validCount,
        int invalidCount,
        List<Contact> contacts
    ) {}
    
    /**
     * 导入异常
     */
    public static class ImportException extends RuntimeException {
        public ImportException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
