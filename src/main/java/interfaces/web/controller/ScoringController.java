package com.wechat.acquisition.interfaces.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/scoring")
public class ScoringController {
    
    private static final Logger log = LoggerFactory.getLogger(ScoringController.class);
    
    /**
     * 实时打分
     */
    @PostMapping("/score")
    public ResponseEntity<Map<String, Object>> calculateScore(@RequestBody Map<String, String> request) {
        log.info("打分请求：{}", request.get("conversationId"));
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of(
            "total_score", 85.5,
            "level", "A",
            "level_name", "高意向",
            "action", "立即人工跟进"
        ));
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取打分规则
     */
    @GetMapping("/rules")
    public ResponseEntity<Map<String, Object>> getScoringRules() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of(
            "dimensions", java.util.List.of(
                Map.of("name", "responsiveness", "label", "响应度", "weight", 0.30),
                Map.of("name", "interest", "label", "兴趣度", "weight", 0.35)
            )
        ));
        return ResponseEntity.ok(response);
    }
}
