package com.wechat.acquisition.interfaces.web.controller;

import com.wechat.acquisition.application.service.CampaignServiceImpl;
import com.wechat.acquisition.infrastructure.persistence.entity.CampaignEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
public class CampaignController {
    
    private final CampaignServiceImpl campaignService;
    
    /**
     * 获取活动列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listCampaigns(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(campaignService.listCampaigns(status, page, size));
    }
    
    /**
     * 获取活动详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<CampaignEntity> getCampaign(@PathVariable String id) {
        return ResponseEntity.ok(campaignService.getCampaign(id));
    }
    
    /**
     * 创建活动
     */
    @PostMapping
    public ResponseEntity<CampaignEntity> createCampaign(
            @RequestBody Map<String, String> request) {
        String name = request.get("name");
        return ResponseEntity.ok(campaignService.createCampaign(name));
    }
    
    /**
     * 启动活动
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<Map<String, Object>> startCampaign(@PathVariable String id) {
        campaignService.startCampaign(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "活动已启动");
        return ResponseEntity.ok(result);
    }
    
    /**
     * 暂停活动
     */
    @PostMapping("/{id}/pause")
    public ResponseEntity<Map<String, Object>> pauseCampaign(@PathVariable String id) {
        campaignService.pauseCampaign(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "活动已暂停");
        return ResponseEntity.ok(result);
    }
    
    /**
     * 停止活动
     */
    @PostMapping("/{id}/stop")
    public ResponseEntity<Map<String, Object>> stopCampaign(@PathVariable String id) {
        campaignService.stopCampaign(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "活动已停止");
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取活动统计
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getCampaignStats(@PathVariable String id) {
        return ResponseEntity.ok(campaignService.getCampaignStats(id));
    }
}
