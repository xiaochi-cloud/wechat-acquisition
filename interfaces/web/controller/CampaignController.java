package com.wechat.acquisition.interfaces.web.controller;

import com.wechat.acquisition.application.service.CampaignApplicationService;
import com.wechat.acquisition.domain.acquisition.Campaign;
import com.wechat.acquisition.domain.acquisition.CampaignStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获客活动管理 API
 */
@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
public class CampaignController {
    
    private final CampaignApplicationService campaignService;
    
    /**
     * 创建活动
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCampaign(@RequestBody CreateCampaignRequest request) {
        // TODO: 实现创建活动逻辑
        Campaign campaign = null; // campaignService.createCampaign(...);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of("campaign_id", "mock_id"));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取活动列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listCampaigns(
            @RequestParam(required = false) CampaignStatus status) {
        
        List<Campaign> campaigns = campaignService.listCampaigns(status);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", campaigns);
        response.put("total", campaigns.size());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取活动详情
     */
    @GetMapping("/{campaignId}")
    public ResponseEntity<Map<String, Object>> getCampaign(@PathVariable String campaignId) {
        Campaign campaign = campaignService.getCampaign(campaignId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", campaign);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 启动活动
     */
    @PostMapping("/{campaignId}/start")
    public ResponseEntity<Map<String, Object>> startCampaign(@PathVariable String campaignId) {
        campaignService.startCampaign(campaignId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "活动已启动");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 暂停活动
     */
    @PostMapping("/{campaignId}/pause")
    public ResponseEntity<Map<String, Object>> pauseCampaign(@PathVariable String campaignId) {
        campaignService.pauseCampaign(campaignId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "活动已暂停");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 停止活动
     */
    @PostMapping("/{campaignId}/stop")
    public ResponseEntity<Map<String, Object>> stopCampaign(@PathVariable String campaignId) {
        campaignService.stopCampaign(campaignId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "活动已停止");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 创建活动请求
     */
    public record CreateCampaignRequest(
        String name,
        String dataSourceId,
        Object targetAudience,
        Object scheduleConfig,
        Object rateLimitConfig
    ) {}
}
