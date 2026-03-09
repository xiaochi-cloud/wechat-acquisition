package com.wechat.acquisition.infrastructure.persistence.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexResolver;

/**
 * MongoDB 配置 (用于存储对话记录)
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
    
    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017/wechat_acquisition}")
    private String mongoUri;
    
    /**
     * Mongo Client
     */
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }
    
    /**
     * Mongo Template
     */
    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "wechat_acquisition");
    }
}
