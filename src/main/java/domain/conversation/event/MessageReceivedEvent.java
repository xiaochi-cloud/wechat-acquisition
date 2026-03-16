package com.wechat.acquisition.domain.conversation.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wechat.acquisition.domain.common.event.DomainEvent;

/**
 * 收到消息事件
 */
public class MessageReceivedEvent extends DomainEvent {
    private static final Logger log = LoggerFactory.getLogger(MessageReceivedEvent.class);
    
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
