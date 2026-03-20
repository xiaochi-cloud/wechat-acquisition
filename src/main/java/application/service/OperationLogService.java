package com.wechat.acquisition.application.service;

import com.wechat.acquisition.infrastructure.persistence.entity.OperationLogEntity;
import com.wechat.acquisition.infrastructure.persistence.mapper.OperationLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogService {
    
    private final OperationLogMapper operationLogMapper;
    
    /**
     * 记录操作日志
     */
    @Transactional
    public void logOperation(String userId, String username, String operation, String module, String status, String message) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String ipAddress = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");
            
            OperationLogEntity logEntity = new OperationLogEntity();
            logEntity.setId("log_" + System.currentTimeMillis());
            logEntity.setUserId(userId);
            logEntity.setUsername(username);
            logEntity.setOperation(operation);
            logEntity.setModule(module);
            logEntity.setIpAddress(ipAddress);
            logEntity.setUserAgent(userAgent);
            logEntity.setStatus(status);
            logEntity.setMessage(message);
            logEntity.setCreatedAt(LocalDateTime.now());
            
            operationLogMapper.insert(logEntity);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }
    
    /**
     * 获取操作日志列表
     */
    @Transactional(readOnly = true)
    public Map<String, Object> listLogs(String module, String status, int page, int size) {
        log.info("查询操作日志：module={}, status={}, page={}, size={}", module, status, page, size);
        
        LambdaQueryWrapper<OperationLogEntity> wrapper = new LambdaQueryWrapper<>();
        if (module != null && !module.isEmpty()) {
            wrapper.eq(OperationLogEntity::getModule, module);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(OperationLogEntity::getStatus, status);
        }
        wrapper.orderByDesc(OperationLogEntity::getCreatedAt);
        
        Page<OperationLogEntity> pageResult = operationLogMapper.selectPage(new Page<>(page, size), wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("page", page);
        result.put("size", size);
        result.put("data", pageResult.getRecords());
        
        return result;
    }
    
    /**
     * 记录登录日志
     */
    public void logLogin(String userId, String username, String status) {
        logOperation(userId, username, "登录", "auth", status, "用户登录系统");
    }
    
    /**
     * 记录联系人操作日志
     */
    public void logContact(String userId, String username, String operation, String contactId) {
        logOperation(userId, username, operation, "contact", "SUCCESS", "操作联系人：" + contactId);
    }
    
    /**
     * 记录活动操作日志
     */
    public void logCampaign(String userId, String username, String operation, String campaignId) {
        logOperation(userId, username, operation, "campaign", "SUCCESS", "操作活动：" + campaignId);
    }
}
