package com.wechat.acquisition.domain.acquisition.event;

import com.wechat.acquisition.domain.common.event.DomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 企微好友添加成功事件
 */
@Getter
public class WeChatFriendAddedEvent extends DomainEvent {
    
    private final String contactId;
    private final String weChatAccountId;
    private final String weChatUserId;
    private final LocalDateTime addedAt;
    
    public WeChatFriendAddedEvent(String contactId, String weChatAccountId, String weChatUserId) {
        super();
        this.contactId = contactId;
        this.weChatAccountId = weChatAccountId;
        this.weChatUserId = weChatUserId;
        this.addedAt = LocalDateTime.now();
    }
    
    @Override
    public String getEventType() {
        return "WECHAT_FRIEND_ADDED";
    }
}
