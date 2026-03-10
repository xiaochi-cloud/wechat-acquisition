package com.wechat.acquisition.domain.acquisition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 联系人聚合根
 */
public class Contact {
    private static final Logger log = LoggerFactory.getLogger(Contact.class);
    private String id;
    private String phoneNumber;
    private String weChatId;
    private String name;
    private ContactStatus status;
    private Map<String, String> tags;
    private ProfileData profileData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastContactAt;
    
    public Contact() { this.tags = new HashMap<>(); }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getWeChatId() { return weChatId; }
    public void setWeChatId(String weChatId) { this.weChatId = weChatId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ContactStatus getStatus() { return status; }
    public void setStatus(ContactStatus status) { this.status = status; }
    public Map<String, String> getTags() { return tags; }
    public void setTags(Map<String, String> tags) { this.tags = tags; }
    public ProfileData getProfileData() { return profileData; }
    public void setProfileData(ProfileData profileData) { this.profileData = profileData; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getLastContactAt() { return lastContactAt; }
    public void setLastContactAt(LocalDateTime lastContactAt) { this.lastContactAt = lastContactAt; }
    
    public static Contact create(String phoneNumber) {
        Contact c = new Contact();
        c.setId(UUID.randomUUID().toString());
        c.setPhoneNumber(phoneNumber);
        c.setStatus(ContactStatus.NEW);
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        return c;
    }
    public void updatePhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; this.updatedAt = LocalDateTime.now(); }
    public void bindWeChat(String weChatId) { this.weChatId = weChatId; this.updatedAt = LocalDateTime.now(); }
    public void addTag(String key, String value) { this.tags.put(key, value); this.updatedAt = LocalDateTime.now(); }
    public void updateStatus(ContactStatus status) { this.status = status; this.updatedAt = LocalDateTime.now(); }
    public void recordContact() { this.lastContactAt = LocalDateTime.now(); this.updatedAt = LocalDateTime.now(); }
    public boolean isWeChatAdded() { return this.weChatId != null && !this.weChatId.isEmpty(); }
}

public enum ContactStatus {
    NEW, IMPORTED, ADDING, ADDED, CONVERSING, SCORED, CONVERTED, INVALID, BLOCKED
}

public class ProfileData {
    private String gender;
    private Integer age;
    private String city;
    private String industry;
    private String company;
    private String position;
    private Double incomeLevel;
    private Map<String, Object> extras;
    
    public ProfileData() {}
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public Double getIncomeLevel() { return incomeLevel; }
    public void setIncomeLevel(Double incomeLevel) { this.incomeLevel = incomeLevel; }
    public Map<String, Object> getExtras() { return extras; }
    public void setExtras(Map<String, Object> extras) { this.extras = extras; }
}
