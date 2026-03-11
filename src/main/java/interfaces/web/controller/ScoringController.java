package com.wechat.acquisition.interfaces.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wechat.acquisition.application.service.IntentScoringService;
import com.wechat.acquisition.domain.scoring.IntentScore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 意向打分 API
 */
@Slf4j
@RestController
@RequestMapping("/scoring")
@RequiredArgsConstructor
@Data
public class ScoringController {
    private static final Logger log = LoggerFactory.getLogger(ScoringController.class);
    
    private final IntentScoringService scoringService;
    
    /**
     * 实时打分
     */
    @PostMapping("/score")
    public ResponseEntity<Map<String, Object>> calculateScore(
            @RequestBody ScoreRequest request) {
        
        log.info("收到打分请求，对话 ID: {}", request.conversationId());
        
        IntentScore score = scoringService.calculateScore(request.conversationId());
        String suggestion = scoringService.getFollowupSuggestion(score);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of(
            "total_score", score.getTotalScore(),
            "level", score.getLevel().name(),
            "level_name", score.getLevel().getName(),
            "action", score.getLevel().getAction(),
            "dimensions", score.getDimensions(),
            "reasoning", score.getReasoning(),
            "suggestion", suggestion
        ));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 批量打分
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchScore(
            @RequestBody BatchScoreRequest request) {
        
        log.info("收到批量打分请求，对话数量：{}", request.conversationIds().size());
        
        Map<String, IntentScore> results = scoringService.batchCalculateScore(request.conversationIds());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of(
            "task_id", "task_" + System.currentTimeMillis(),
            "total_count", request.conversationIds().size(),
            "completed_count", results.size(),
            "results", results
        ));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取打分规则
     */
    @GetMapping("/rules")
    public ResponseEntity<Map<String, Object>> getScoringRules() {
        
        Map<String, Object> rules = new HashMap<>();
        rules.put("dimensions", List.of(
            Map.of("name", "responsiveness", "label", "响应度", "weight", 0.30),
            Map.of("name", "interest", "label", "兴趣度", "weight", 0.35),
            Map.of("name", "urgency", "label", "紧迫度", "weight", 0.20),
            Map.of("name", "match", "label", "匹配度", "weight", 0.15)
        ));
        rules.put("levels", List.of(
            Map.of("level", "A", "name", "高意向", "range", "80-100", "action", "立即人工跟进"),
            Map.of("level", "B", "name", "中意向", "range", "60-79", "action", "持续培育"),
            Map.of("level", "C", "name", "低意向", "range", "40-59", "action", "自动化触达"),
            Map.of("level", "D", "name", "无意向", "range", "0-39", "action", "归档/移除")
        ));
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", rules);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 打分请求
     */
    public record ScoreRequest(String conversationId) {}
    
    /**
     * 批量打分请求
     */
    public record BatchScoreRequest(List<String> conversationIds) {}
}
