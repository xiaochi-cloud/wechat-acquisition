package com.wechat.acquisition.infrastructure.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.client.AdminClient;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.server.XxlJobServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * XXL-Job 配置
 */
@Slf4j
@Configuration
@Data
public class XxlJobConfig {
    private static final Logger log = LoggerFactory.getLogger(XxlJobConfig.class);
    
    @Value("${xxl.job.admin.addresses:}")
    private String adminAddresses;
    
    @Value("${xxl.job.executor.appname:wechat-acquisition}")
    private String appname;
    
    @Value("${xxl.job.executor.port:9999}")
    private int port;
    
    @Value("${xxl.job.access-token:}")
    private String accessToken;
    
    /**
     * 自动注册到 XXL-Job Admin
     */
    @Bean
    public AdminBiz adminBiz() {
        return new AdminClient(adminAddresses, accessToken);
    }
    
    /**
     * 任务执行器
     */
    @Bean
    public XxlJobServer xxlJobServer() {
        XxlJobServer server = new XxlJobServer();
        server.setAdminAddresses(adminAddresses);
        server.setAppname(appname);
        server.setPort(port);
        server.setAccessToken(accessToken);
        return server;
    }
}
