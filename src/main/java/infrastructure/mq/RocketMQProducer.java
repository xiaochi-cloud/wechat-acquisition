package com.wechat.acquisition.infrastructure.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * RocketMQ 消息生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RocketMQProducer {
    
    private final RocketMQTemplate rocketMQTemplate;
    
    /**
     * 发送消息
     */
    public void send(String topic, Object data) {
        log.info("发送消息：topic={}, data={}", topic, data);
        rocketMQTemplate.send(topic, data);
    }
    
    /**
     * 发送延迟消息
     */
    public void sendDelay(String topic, Object data, int delayLevel) {
        log.info("发送延迟消息：topic={}, delayLevel={}", topic, delayLevel);
        Message<?> message = MessageBuilder.withPayload(data).build();
        rocketMQTemplate.syncSend(topic, message, 3000, delayLevel);
    }
    
    /**
     * 发送联系人添加任务
     */
    public void sendContactAddTask(String phoneNumber, String campaignId) {
        ContactAddTask task = new ContactAddTask(phoneNumber, campaignId);
        send("CONTACT_ADD_TOPIC", task);
    }
    
    /**
     * 联系人添加任务
     */
    public record ContactAddTask(String phoneNumber, String campaignId) {}
}
