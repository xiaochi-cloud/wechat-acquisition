package com.wechat.acquisition.interfaces.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 企业微信回调处理
 * 
 * 处理企微推送的事件通知：
 * - 好友添加成功
 * - 消息接收
 * - 用户标签变更
 */
@RestController
@RequestMapping("/wechat/callback")
public class WeChatCallbackController {

    private static final Logger log = LoggerFactory.getLogger(WeChatCallbackController.class);
    
    @Value("${wechat.token:}")
    private String token;
    
    @Value("${wechat.encoding-aes-key:}")
    private String encodingAesKey;
    
    @Value("${wechat.corp-id:}")
    private String corpId;
    
    /**
     * 验证回调 URL (GET)
     * 
     * 企微会发送 GET 请求验证 URL 有效性
     */
    @GetMapping
    public ResponseEntity<String> verify(
            @RequestParam("msg_signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam("echostr") String echoStr) {
        
        log.info("收到企微 URL 验证请求");
        
        try {
            // 验证签名
            if (!checkSignature(signature, timestamp, nonce)) {
                log.warn("签名验证失败");
                return ResponseEntity.badRequest().body("signature check failed");
            }
            
            // 返回 echostr 完成验证
            return ResponseEntity.ok(echoStr);
            
        } catch (Exception e) {
            log.error("URL 验证失败", e);
            return ResponseEntity.badRequest().body("verify failed");
        }
    }
    
    /**
     * 接收回调事件 (POST)
     * 
     * 处理各类事件通知
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> receiveCallback(
            @RequestParam("msg_signature") String signature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestBody String requestBody) {
        
        log.info("收到企微回调，签名：{}", signature);
        
        try {
            // 验证签名
            if (!checkSignature(signature, timestamp, nonce)) {
                log.warn("签名验证失败");
                return ResponseEntity.badRequest().body(Map.of("success", false));
            }
            
            // 解析消息
            Map<String, Object> message = JSONUtil.toBean(requestBody, Map.class);
            String eventType = (String) message.get("Event");
            String changeType = (String) message.get("ChangeType");
            
            log.info("事件类型：{}, 变更类型：{}", eventType, changeType);
            
            // 路由到不同处理器
            switch (eventType) {
                case "change_external_contact":
                    handleExternalContactChange(changeType, message);
                    break;
                case "change_external_chat":
                    handleExternalChatChange(changeType, message);
                    break;
                default:
                    log.info("未处理的事件类型：{}", eventType);
            }
            
            // 返回成功响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("处理回调失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 处理外部联系人事件
     */
    private void handleExternalContactChange(String changeType, Map<String, Object> message) {
        String userId = (String) message.get("UserID");
        String externalUserId = (String) message.get("ExternalUserId");
        
        log.info("外部联系人事件：{}, 用户：{}, 外部用户：{}", changeType, userId, externalUserId);
        
        switch (changeType) {
            case "add_external_contact":
                // 新增外部联系人
                log.info("新增好友：userId={}, externalUserId={}", userId, externalUserId);
                // TODO: 发布领域事件
                // eventPublisher.publish(new WeChatFriendAddedEvent(...));
                break;
                
            case "edit_external_contact":
                // 编辑外部联系人
                log.info("编辑联系人：externalUserId={}", externalUserId);
                break;
                
            case "del_external_contact":
                // 删除外部联系人
                log.info("删除联系人：externalUserId={}", externalUserId);
                break;
                
            default:
                log.info("未处理的联系人事件：{}", changeType);
        }
    }
    
    /**
     * 处理外部会话事件
     */
    private void handleExternalChatChange(String changeType, Map<String, Object> message) {
        log.info("外部会话事件：{}", changeType);
        // TODO: 处理群聊事件
    }
    
    /**
     * 验证签名
     * 
     * 签名规则：将 token、timestamp、nonce 三个参数进行字典序排序后拼接成字符串，进行 SHA1 加密
     */
    private boolean checkSignature(String signature, String timestamp, String nonce) {
        if (StrUtil.isEmpty(token)) {
            log.warn("未配置企微 token，跳过签名验证");
            return true;
        }
        
        String[] arr = new String[] { token, timestamp, nonce };
        Arrays.sort(arr);
        
        String content = StrUtil.join("", arr);
        String sha1 = DigestUtil.sha1Hex(content);
        
        return sha1.equals(signature);
    }
    
    /**
     * 解密企微消息 (AES)
     * 
     * TODO: 实现 AES 解密逻辑
     */
    private String decryptMessage(String encryptMsg, String msgSignature, String timestamp, String nonce) {
        // TODO: 使用 WXBizMsgCrypt 解密
        return encryptMsg;
    }
    
    /**
     * 加密消息回复
     */
    private String encryptMessage(String message, String msgSignature, String timestamp, String nonce) {
        // TODO: 使用 WXBizMsgCrypt 加密
        return message;
    }
}
