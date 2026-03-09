package com.wechat.acquisition.domain.conversation.event;

import com.wechat.acquisition.domain.common.event.DomainEvent;
import lombok.Getter;

/**
 * 收到消息事件
 */
@Getter
public class MessageReceivedEvent extends DomainEvent {
    
    private final String conversationId;
    private final String contactId;
    private final String messageContent;
    private final boolean isUserMessage;
    
    public MessageReceivedEvent(String conversationId, String contactId, String messageContent, boolean isUserMessage) {
        super();
        this.conversationId = conversationId;
        this.contactId = contactId;
        this.messageContent = messageContent;
        this.isUserMessage = true;
    }
    
    @Override
    public String getEventType() {
        return "MESSAGE_RECEIVED";
    }
}
