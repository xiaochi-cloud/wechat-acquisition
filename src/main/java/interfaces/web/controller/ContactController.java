package com.wechat.acquisition.interfaces.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    
    private static final Logger log = LoggerFactory.getLogger(ContactController.class);
    
    /**
     * Excel 导入
     */
    @PostMapping("/import/excel")
    public ResponseEntity<Map<String, Object>> importExcel(@RequestParam("file") MultipartFile file) {
        log.info("Excel 导入：{}", file.getOriginalFilename());
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of("import_id", "import_" + System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取联系人列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listContacts() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", new java.util.ArrayList<>());
        response.put("total", 0);
        return ResponseEntity.ok(response);
    }
}
