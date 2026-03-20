package com.wechat.acquisition.interfaces.web.controller;

import com.wechat.acquisition.application.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 数据统计 API
 */
@Slf4j
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    /**
     * 获取仪表盘统计
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(statisticsService.getDashboardStats());
    }
    
    /**
     * 获取转化漏斗
     */
    @GetMapping("/funnel")
    public ResponseEntity<Map<String, Object>> getConversionFunnel() {
        return ResponseEntity.ok(statisticsService.getConversionFunnel());
    }
    
    /**
     * 获取趋势数据
     */
    @GetMapping("/trend")
    public ResponseEntity<Map<String, Object>> getTrendData(
            @RequestParam(defaultValue = "7") int days) {
        Map<String, Object> result = statisticsService.getDashboardStats();
        return ResponseEntity.ok(result);
    }
}
