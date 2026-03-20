package com.wechat.acquisition.application.service;

import com.wechat.acquisition.infrastructure.persistence.entity.TagEntity;
import com.wechat.acquisition.infrastructure.persistence.mapper.TagMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标签服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {
    
    private final TagMapper tagMapper;
    
    /**
     * 获取标签列表
     */
    @Transactional(readOnly = true)
    public Map<String, Object> listTags(String category, int page, int size) {
        log.info("查询标签列表：category={}, page={}, size={}", category, page, size);
        
        LambdaQueryWrapper<TagEntity> wrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            wrapper.eq(TagEntity::getCategory, category);
        }
        wrapper.orderByDesc(TagEntity::getCreatedAt);
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<TagEntity> pageResult = 
            tagMapper.selectPage(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size),
                wrapper
            );
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("page", page);
        result.put("size", size);
        result.put("data", pageResult.getRecords());
        
        return result;
    }
    
    /**
     * 创建标签
     */
    @Transactional
    public TagEntity createTag(String name, String category, String color, String description) {
        log.info("创建标签：name={}, category={}, color={}", name, category, color);
        
        TagEntity tag = new TagEntity();
        tag.setId("tag_" + System.currentTimeMillis());
        tag.setName(name);
        tag.setCategory(category);
        tag.setColor(color);
        tag.setDescription(description);
        tag.setCreatedAt(LocalDateTime.now());
        tag.setUpdatedAt(LocalDateTime.now());
        
        tagMapper.insert(tag);
        return tag;
    }
    
    /**
     * 更新标签
     */
    @Transactional
    public TagEntity updateTag(String id, String name, String color, String description) {
        log.info("更新标签：id={}, name={}", id, name);
        
        TagEntity tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new IllegalStateException("标签不存在：" + id);
        }
        
        if (name != null) tag.setName(name);
        if (color != null) tag.setColor(color);
        if (description != null) tag.setDescription(description);
        tag.setUpdatedAt(LocalDateTime.now());
        
        tagMapper.updateById(tag);
        return tag;
    }
    
    /**
     * 删除标签
     */
    @Transactional
    public void deleteTag(String id) {
        log.info("删除标签：id={}", id);
        tagMapper.deleteById(id);
    }
    
    /**
     * 获取预设标签模板
     */
    @Transactional(readOnly = true)
    public List<TagEntity> getPresets() {
        log.info("获取预设标签");
        
        LambdaQueryWrapper<TagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TagEntity::getCategory, "preset");
        return tagMapper.selectList(wrapper);
    }
}
