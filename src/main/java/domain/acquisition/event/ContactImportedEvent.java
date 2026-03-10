package com.wechat.acquisition.domain.acquisition.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wechat.acquisition.domain.common.event.DomainEvent;
import lombok.Getter;

/**
 * 联系人导入完成事件
 */
@Getter
public class ContactImportedEvent extends DomainEvent {
    private static final Logger log = LoggerFactory.getLogger(ContactImportedEvent.class);
    
    private final String contactId;
    private final String phoneNumber;
    private final String campaignId;
    private final String dataSourceId;
    
    public ContactImportedEvent(String contactId, String phoneNumber, String campaignId, String dataSourceId) {
        super();
        this.contactId = contactId;
        this.phoneNumber = phoneNumber;
        this.campaignId = campaignId;
        this.dataSourceId = dataSourceId;
    }
    
    @Override
    public String getEventType() {
        return "CONTACT_IMPORTED";
    }
}
