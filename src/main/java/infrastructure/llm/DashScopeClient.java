package com.wechat.acquisition.infrastructure.llm;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DashScopeClient {
    
    private static final Logger log = LoggerFactory.getLogger(DashScopeClient.class);
    
    @Value("${llm.dashscope.api-key:}")
    private String apiKey;
    
    @Value("${llm.dashscope.model:qwen-max}")
    private String model;
    
    @Value("${llm.dashscope.max-tokens:1000}")
    private Integer maxTokens;
    
    private final Generation generation = new Generation();
    
    public ChatResponse chat(List<ChatMessage> messages) {
        log.info("调用通义千问，消息数：{}", messages.size());
        try {
            List<Message> dashscopeMessages = new ArrayList<>();
            for (ChatMessage m : messages) {
                dashscopeMessages.add(Message.builder().role(m.role()).content(m.content()).build());
            }
            GenerationParam param = GenerationParam.builder()
                .apiKey(apiKey).model(model).messages(dashscopeMessages)
                .maxTokens(maxTokens).temperature(0.7f)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE).build();
            GenerationResult result = generation.call(param);
            String reply = result.getOutput().getChoices().get(0).getMessage().getContent();
            int usage = result.getUsage().getTotalTokens();
            return new ChatResponse(reply, usage);
        } catch (Exception e) {
            log.error("对话生成失败", e);
            throw new LlmException("大模型调用失败：" + e.getMessage(), e);
        }
    }
    
    public record ChatMessage(String role, String content) {}
    public record ChatResponse(String content, int usage) {}
    public static class LlmException extends RuntimeException {
        public LlmException(String message, Throwable cause) { super(message, cause); }
    }
}
