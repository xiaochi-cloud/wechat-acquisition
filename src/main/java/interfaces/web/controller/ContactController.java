package com.wechat.acquisition.interfaces.web.controller;

import com.wechat.acquisition.application.service.ContactService;
import com.wechat.acquisition.domain.acquisition.Contact;
import com.wechat.acquisition.domain.acquisition.ContactStatus;
import com.wechat.acquisition.interfaces.web.dto.ContactDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 联系人管理 API
 */
@Slf4j
@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {
    
    private final ContactService contactService;
    
    /**
     * 获取联系人列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listContacts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String campaignId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Map<String, String> params = new HashMap<>();
        params.put("status", status);
        params.put("campaignId", campaignId);
        params.put("keyword", keyword);
        params.put("page", String.valueOf(page));
        params.put("size", String.valueOf(size));
        
        return ResponseEntity.ok(contactService.getContactList(params));
    }
    
    /**
     * 获取联系人详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactDetail(@PathVariable String id) {
        return ResponseEntity.ok(contactService.getContactDetail(id));
    }
    
    /**
     * 创建联系人
     */
    @PostMapping
    public ResponseEntity<Contact> createContact(@RequestBody ContactDTO dto) {
        Contact contact = contactService.createContact(dto.getPhoneNumber(), dto.getName());
        return ResponseEntity.ok(contact);
    }
    
    /**
     * 批量创建联系人
     */
    @PostMapping("/batch")
    public ResponseEntity<List<Contact>> batchCreateContacts(
            @RequestBody List<Map<String, String>> contacts) {
        return ResponseEntity.ok(contactService.batchCreateContacts(contacts));
    }
    
    /**
     * 导入联系人 (Excel)
     */
    @PostMapping("/import/excel")
    public ResponseEntity<Map<String, Object>> importContacts(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String campaignId) {
        
        log.info("Excel 导入：file={}, campaignId={}", file.getOriginalFilename(), campaignId);
        
        // TODO: 解析 Excel 文件
        // 暂时返回模拟数据
        List<Map<String, String>> mockContacts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, String> contact = new HashMap<>();
            contact.put("phoneNumber", "1380013800" + i);
            contact.put("name", "测试用户" + i);
            mockContacts.add(contact);
        }
        
        Map<String, Object> result = contactService.importContacts(mockContacts, campaignId);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新联系人标签
     */
    @PostMapping("/{id}/tags")
    public ResponseEntity<Contact> updateTags(
            @PathVariable String id,
            @RequestBody Map<String, String> tags) {
        return ResponseEntity.ok(contactService.updateContactTags(id, tags));
    }
    
    /**
     * 更新联系人状态
     */
    @PostMapping("/{id}/status")
    public ResponseEntity<Contact> updateStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return ResponseEntity.ok(contactService.updateContactStatus(id, ContactStatus.valueOf(status)));
    }
    
    /**
     * 删除联系人
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteContact(@PathVariable String id) {
        contactService.deleteContact(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "删除成功");
        return ResponseEntity.ok(result);
    }
    
    /**
     * 批量删除联系人
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchDeleteContacts(
            @RequestBody List<String> ids) {
        return ResponseEntity.ok(contactService.batchDeleteContacts(ids));
    }
    
    /**
     * 获取联系人统计
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(contactService.getContactStats());
    }
}
