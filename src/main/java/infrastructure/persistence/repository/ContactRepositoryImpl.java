package com.wechat.acquisition.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class ContactRepositoryImpl {
    
    private final Map<String, Object> contactCache = new HashMap<>();
    
    public ContactRepositoryImpl() {}
    
    public Optional<Object> findById(String id) {
        return Optional.ofNullable(contactCache.get(id));
    }
    
    public Object save(Object contact) {
        return contact;
    }
    
    public List<Object> saveAll(List<Object> contacts) {
        return contacts;
    }
    
    public long count() {
        return contactCache.size();
    }
}
