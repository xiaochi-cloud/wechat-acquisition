package com.wechat.acquisition.interfaces.web.controller;

import com.wechat.acquisition.application.service.AccountSecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 账号安全 API
 */
@Slf4j
@RestController
@RequestMapping("/security")
@RequiredArgsConstructor
public class AccountSecurityController {
    
    private final AccountSecurityService accountSecurityService;
    
    /**
     * 检查账号安全状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> checkSecurityStatus() {
        return ResponseEntity.ok(accountSecurityService.checkSecurityStatus());
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestBody Map<String, String> request) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        return ResponseEntity.ok(accountSecurityService.changePassword("admin", oldPassword, newPassword));
    }
    
    /**
     * 切换账号状态
     */
    @PostMapping("/toggle-account")
    public ResponseEntity<Map<String, Object>> toggleAccount(
            @RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        Boolean enabled = (Boolean) request.get("enabled");
        return ResponseEntity.ok(accountSecurityService.toggleAccount(userId, enabled));
    }
}
