package com.wechat.acquisition.interfaces.web.controller;

import com.wechat.acquisition.application.service.ContactServiceImpl;
import com.wechat.acquisition.infrastructure.persistence.entity.ContactEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {
    
    private final ContactServiceImpl contactService;
    
    /**
     * 获取联系人列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listContacts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(contactService.listContacts(status, keyword, page, size));
    }
    
    /**
     * 获取联系人详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContactEntity> getContact(@PathVariable String id) {
        return ResponseEntity.ok(contactService.getContact(id));
    }
    
    /**
     * 创建联系人
     */
    @PostMapping
    public ResponseEntity<ContactEntity> createContact(
            @RequestBody Map<String, String> request) {
        String phone = request.get("phoneNumber");
        String name = request.getOrDefault("name", "");
        String campaignId = request.get("campaignId");
        return ResponseEntity.ok(contactService.createContact(phone, name, campaignId));
    }
    
    /**
     * 批量创建联系人
     */
    @PostMapping("/batch")
    public ResponseEntity<List<ContactEntity>> batchCreateContacts(
            @RequestBody List<Map<String, String>> contacts,
            @RequestParam(required = false) String campaignId) {
        return ResponseEntity.ok(contactService.batchCreateContacts(contacts, campaignId));
    }
    
    /**
     * Excel 导入联系人
     */
    @PostMapping("/import/excel")
    public ResponseEntity<Map<String, Object>> importContacts(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String campaignId) {
        log.info("Excel 导入：file={}, campaignId={}", file.getOriginalFilename(), campaignId);
        
        // TODO: 解析 Excel 文件
        // 暂时返回模拟数据
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("totalCount", 10);
        result.put("successCount", 10);
        result.put("failCount", 0);
        result.put("message", "导入成功");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新联系人标签
     */
    @PostMapping("/{id}/tags")
    public ResponseEntity<ContactEntity> updateTags(
            @PathVariable String id,
            @RequestBody Map<String, String> tags) {
        return ResponseEntity.ok(contactService.updateTags(id, tags));
    }
    
    /**
     * 更新联系人状态
     */
    @PostMapping("/{id}/status")
    public ResponseEntity<ContactEntity> updateStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return ResponseEntity.ok(contactService.updateStatus(id, status));
    }
    
    /**
     * 删除联系人
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteContact(@PathVariable String id) {
        contactService.deleteContact(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 批量删除联系人
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchDeleteContacts(
            @RequestBody List<String> ids) {
        return ResponseEntity.ok(contactService.batchDelete(ids));
    }
    
    /**
     * 获取联系人统计
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(contactService.getStats());
    }
}
