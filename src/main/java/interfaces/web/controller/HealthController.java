package com.wechat.acquisition.interfaces.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查 API
 */
@Slf4j
@RestController
@RequestMapping
public class HealthController {
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("version", "1.0.0-SNAPSHOT");
        return ResponseEntity.ok(response);
    }
    
    /**
     * 就绪检查
     */
    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> ready() {
        // TODO: 检查数据库、Redis、MQ 等依赖是否就绪
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("checks", Map.of(
            "database", "UP",
            "redis", "UP",
            "mongodb", "UP",
            "rocketmq", "UP"
        ));
        return ResponseEntity.ok(response);
    }
    
    /**
     * 系统信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "WeChat Acquisition Platform");
        response.put("description", "企业微信获客平台");
        response.put("version", "1.0.0-SNAPSHOT");
        response.put("java_version", System.getProperty("java.version"));
        response.put("java_vendor", System.getProperty("java.vendor"));
        response.put("os_name", System.getProperty("os.name"));
        response.put("os_version", System.getProperty("os.version"));
        response.put("available_processors", Runtime.getRuntime().availableProcessors());
        response.put("total_memory_mb", Runtime.getRuntime().totalMemory() / 1024 / 1024);
        response.put("max_memory_mb", Runtime.getRuntime().maxMemory() / 1024 / 1024);
        return ResponseEntity.ok(response);
    }
}
