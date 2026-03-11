package com.wechat.acquisition.domain.acquisition;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class DataSource {
    private String id;
    private DataSourceType type;
    private String name;
    private DataSourceConfig config;
    private List<Contact> contacts;
    private ImportStatus importStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public DataSource() { this.contacts = new ArrayList<>(); }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public DataSourceType getType() { return type; }
    public void setType(DataSourceType type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public DataSourceConfig getConfig() { return config; }
    public void setConfig(DataSourceConfig config) { this.config = config; }
    public List<Contact> getContacts() { return contacts; }
    public void setContacts(List<Contact> contacts) { this.contacts = contacts; }
    public ImportStatus getImportStatus() { return importStatus; }
    public void setImportStatus(ImportStatus importStatus) { this.importStatus = importStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public void addContact(Contact contact) { this.contacts.add(contact); }
    public void addContacts(List<Contact> contacts) { this.contacts.addAll(contacts); }
    public int getContactCount() { return this.contacts != null ? this.contacts.size() : 0; }
    
    public static DataSource createExcel(String name, String filePath) {
        DataSource ds = new DataSource();
        ds.setId(java.util.UUID.randomUUID().toString());
        ds.setType(DataSourceType.EXCEL);
        ds.setName(name);
        DataSourceConfig config = new DataSourceConfig();
        config.setFilePath(filePath);
        ds.setConfig(config);
        ds.setContacts(new ArrayList<>());
        ds.setImportStatus(ImportStatus.PENDING);
        ds.setCreatedAt(LocalDateTime.now());
        ds.setUpdatedAt(LocalDateTime.now());
        return ds;
    }
    
    public static DataSource createApi(String name, String apiUrl, String apiKey) {
        DataSource ds = new DataSource();
        ds.setId(java.util.UUID.randomUUID().toString());
        ds.setType(DataSourceType.API);
        ds.setName(name);
        DataSourceConfig config = new DataSourceConfig();
        config.setApiUrl(apiUrl);
        config.setApiKey(apiKey);
        ds.setConfig(config);
        ds.setContacts(new ArrayList<>());
        ds.setImportStatus(ImportStatus.PENDING);
        return ds;
    }
}

enum DataSourceType { EXCEL, API, WEBHOOK, MANUAL }

enum ImportStatus { PENDING, IMPORTING, COMPLETED, FAILED }

class DataSourceConfig {
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
}
