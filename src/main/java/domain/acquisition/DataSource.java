package com.wechat.acquisition.domain.acquisition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.List;

/**
 * 数据源聚合根
 * 
 * 职责：管理用户数据来源，支持多渠道导入
 */
public class DataSource {
    private static final Logger log = LoggerFactory.getLogger(DataSource.class);
    
    private String id;
    private DataSourceType type;
    private String name;
    private DataSourceConfig config;
    private List<Contact> contacts;
    private ImportStatus importStatus;
    
    /**
     * 创建 Excel 数据源
     */
    public static DataSource createExcel(String name, String filePath) {
        return DataSource.builder()
                .type(DataSourceType.EXCEL)
                .name(name)
                .config(DataSourceConfig.builder()
                        .filePath(filePath)
                        .build())
                .contacts(new ArrayList<>())
                .importStatus(ImportStatus.PENDING)
                .build();
    }
    
    /**
     * 创建 API 数据源
     */
    public static DataSource createApi(String name, String apiUrl, String apiKey) {
        return DataSource.builder()
                .type(DataSourceType.API)
                .name(name)
                .config(DataSourceConfig.builder()
                        .apiUrl(apiUrl)
                        .apiKey(apiKey)
                        .build())
                .contacts(new ArrayList<>())
                .importStatus(ImportStatus.PENDING)
                .build();
    }
    
    /**
     * 创建 Webhook 数据源
     */
    public static DataSource createWebhook(String name, String webhookPath) {
        return DataSource.builder()
                .type(DataSourceType.WEBHOOK)
                .name(name)
                .config(DataSourceConfig.builder()
                        .webhookPath(webhookPath)
                        .build())
                .contacts(new ArrayList<>())
                .importStatus(ImportStatus.PENDING)
                .build();
    }
    
    /**
     * 添加联系人
     */
    public void addContact(Contact contact) {
        this.contacts.add(contact);
    }
    
    /**
     * 批量添加联系人
     */
    public void addContacts(List<Contact> contacts) {
        this.contacts.addAll(contacts);
    }
    
    /**
     * 获取联系人数量
     */
    public int getContactCount() {
        return this.contacts != null ? this.contacts.size() : 0;
    }
}

/**
 * 数据源类型
 */
public enum DataSourceType {
    EXCEL,      // Excel 导入
    API,        // API 同步
    WEBHOOK,    // Webhook 接收
    MANUAL      // 手动录入
 * 数据源配置
 */
public class DataSourceConfig {
    private static final Logger log = LoggerFactory.getLogger(DataSource.class);
    private String filePath;      // Excel 文件路径
    private String apiUrl;        // API 地址
    private String apiKey;        // API 密钥
    private String webhookPath;   // Webhook 路径
    private Integer batchSize;    // 批量处理大小
}

/**
 * 导入状态
 */
public enum ImportStatus {
    PENDING,    // 待导入
    IMPORTING,  // 导入中
    COMPLETED,  // 已完成
    FAILED      // 失败
}

public class DataSourceConfig {
    private String filePath;
    private String apiUrl;
    private String apiKey;
    private String webhookPath;
    private Integer batchSize;
    
    public DataSourceConfig() {}
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getApiUrl() { return apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getWebhookPath() { return webhookPath; }
    public void setWebhookPath(String webhookPath) { this.webhookPath = webhookPath; }
    public Integer getBatchSize() { return batchSize; }
    public void setBatchSize(Integer batchSize) { this.batchSize = batchSize; }
    
    public static DataSourceConfigBuilder builder() { return new DataSourceConfigBuilder(); }
    public static class DataSourceConfigBuilder {
        private String filePath, apiUrl, apiKey, webhookPath;
        private Integer batchSize;
        public DataSourceConfigBuilder filePath(String v) { this.filePath = v; return this; }
        public DataSourceConfigBuilder apiUrl(String v) { this.apiUrl = v; return this; }
        public DataSourceConfigBuilder apiKey(String v) { this.apiKey = v; return this; }
        public DataSourceConfigBuilder webhookPath(String v) { this.webhookPath = v; return this; }
        public DataSourceConfigBuilder batchSize(Integer v) { this.batchSize = v; return this; }
        public DataSourceConfig build() {
            DataSourceConfig c = new DataSourceConfig();
            c.filePath = filePath; c.apiUrl = apiUrl; c.apiKey = apiKey;
            c.webhookPath = webhookPath; c.batchSize = batchSize;
            return c;
        }
    }
}
