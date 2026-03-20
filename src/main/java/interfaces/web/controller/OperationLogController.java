package com.wechat.acquisition.interfaces.web.controller;

import com.wechat.acquisition.application.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 操作日志 API
 */
@Slf4j
@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class OperationLogController {
    
    private final OperationLogService operationLogService;
    
    /**
     * 获取操作日志列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listLogs(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(operationLogService.listLogs(module, status, page, size));
    }
    
    /**
     * 获取登录日志
     */
    @GetMapping("/login")
    public ResponseEntity<Map<String, Object>> listLoginLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(operationLogService.listLogs("auth", "", page, size));
    }
}
