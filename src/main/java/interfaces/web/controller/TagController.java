package com.wechat.acquisition.interfaces.web.controller;

import com.wechat.acquisition.application.service.TagService;
import com.wechat.acquisition.infrastructure.persistence.entity.TagEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标签管理 API
 */
@Slf4j
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {
    
    private final TagService tagService;
    
    /**
     * 获取标签列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listTags(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tagService.listTags(category, page, size));
    }
    
    /**
     * 获取预设标签
     */
    @GetMapping("/presets")
    public ResponseEntity<List<TagEntity>> getPresets() {
        return ResponseEntity.ok(tagService.getPresets());
    }
    
    /**
     * 创建标签
     */
    @PostMapping
    public ResponseEntity<TagEntity> createTag(
            @RequestBody Map<String, String> request) {
        String name = request.get("name");
        String category = request.getOrDefault("category", "default");
        String color = request.getOrDefault("color", "#409EFF");
        String description = request.get("description");
        return ResponseEntity.ok(tagService.createTag(name, category, color, description));
    }
    
    /**
     * 更新标签
     */
    @PutMapping("/{id}")
    public ResponseEntity<TagEntity> updateTag(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        String name = request.get("name");
        String color = request.get("color");
        String description = request.get("description");
        return ResponseEntity.ok(tagService.updateTag(id, name, color, description));
    }
    
    /**
     * 删除标签
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTag(@PathVariable String id) {
        tagService.deleteTag(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return ResponseEntity.ok(result);
    }
}
