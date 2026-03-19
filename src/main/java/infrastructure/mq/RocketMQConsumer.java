package com.wechat.acquisition.infrastructure.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * RocketMQ 消息消费者
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "CONTACT_ADD_TOPIC",
    consumerGroup = "contact_add_consumer_group"
)
@RequiredArgsConstructor
public class RocketMQConsumer implements RocketMQListener<RocketMQProducer.ContactAddTask> {
    
    @Override
    public void onMessage(RocketMQProducer.ContactAddTask task) {
        log.info("收到联系人添加任务：phone={}, campaignId={}", 
            task.phoneNumber(), task.campaignId());
        
        // TODO: 调用企微 API 添加好友
        // weChatService.addFriend(task.phoneNumber(), "您好，我是企业微信助手！");
        
        log.info("联系人添加任务处理完成：phone={}", task.phoneNumber());
    }
}
