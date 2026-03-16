package com.wechat.acquisition.interfaces.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wechat.acquisition.application.service.ContactImportService;
import com.wechat.acquisition.domain.acquisition.Contact;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 联系人管理 API
 */
@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {
    private static final Logger log = LoggerFactory.getLogger(ContactController.class);
    
    private final ContactImportService contactImportService;
    
    /**
     * 从 Excel 导入联系人
     */
    @PostMapping("/import/excel")
    public ResponseEntity<Map<String, Object>> importFromExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "campaignId", required = false) String campaignId) {
        
        log.info("收到 Excel 导入请求，文件：{}", file.getOriginalFilename());
        
        try {
            ContactImportService.ImportResult result = 
                contactImportService.importFromExcel(file, campaignId != null ? campaignId : "default");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "import_id", "import_" + System.currentTimeMillis(),
                "total_count", result.totalCount(),
                "valid_count", result.validCount(),
                "invalid_count", result.invalidCount()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (ContactImportService.ImportException e) {
            log.error("Excel 导入失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "IMPORT_FAILED",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 批量创建联系人
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchCreate(
            @RequestBody BatchCreateRequest request) {
        
        log.info("收到批量创建请求，手机号数量：{}", request.phoneNumbers().size());
        
        List<Contact> contacts = contactImportService.batchCreate(
            request.phoneNumbers(), 
            request.campaignId()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of(
            "count", contacts.size(),
            "contacts", contacts
        ));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取联系人列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listContacts(
            @RequestParam(required = false) String campaignId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // TODO: 实现联系人列表查询
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", List.of());
        response.put("total", 0);
        response.put("page", page);
        response.put("size", size);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取联系人详情
     */
    @GetMapping("/{contactId}")
    public ResponseEntity<Map<String, Object>> getContact(@PathVariable String contactId) {
        // TODO: 实现联系人详情查询
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of("contact_id", contactId));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 更新联系人标签
     */
    @PostMapping("/{contactId}/tags")
    public ResponseEntity<Map<String, Object>> updateTags(
            @PathVariable String contactId,
            @RequestBody Map<String, String> tags) {
        
        // TODO: 实现标签更新
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "标签已更新");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 批量创建请求
     */
    public record BatchCreateRequest(
        List<String> phoneNumbers,
        String campaignId
    ) {}
}
