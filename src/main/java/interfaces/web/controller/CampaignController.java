package com.wechat.acquisition.interfaces.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/campaigns")
public class CampaignController {
    
    private static final Logger log = LoggerFactory.getLogger(CampaignController.class);
    
    /**
     * 创建活动
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCampaign(@RequestBody Map<String, Object> request) {
        log.info("创建活动：{}", request.get("name"));
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of("campaign_id", "camp_" + System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取活动列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listCampaigns() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", new java.util.ArrayList<>());
        response.put("total", 0);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 启动活动
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<Map<String, Object>> startCampaign(@PathVariable String id) {
        log.info("启动活动：{}", id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 暂停活动
     */
    @PostMapping("/{id}/pause")
    public ResponseEntity<Map<String, Object>> pauseCampaign(@PathVariable String id) {
        log.info("暂停活动：{}", id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}
