package com.wechat.acquisition.interfaces.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * WeChat Acquisition Platform - 企业微信获客平台
 * 
 * @author 池少
 * @version 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.wechat.acquisition")
@EnableScheduling
@EnableAsync
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("""
            
            ╔══════════════════════════════════════════════════════════╗
            ║   WeChat Acquisition Platform Started!                   ║
            ║   企业微信获客平台 v1.0.0                                ║
            ║                                                          ║
            ║   DDD + Spec Driven Design                               ║
            ║   Phase 1: MVP (10 万用户量级)                            ║
            ╚══════════════════════════════════════════════════════════╝
            
            """);
    }
}
