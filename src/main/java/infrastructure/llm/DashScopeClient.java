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

import java.util.List;

/**
 * 通义千问 (DashScope) 客户端
 */
@Component
public class DashScopeClient {
    
    private static final Logger log = LoggerFactory.getLogger(DashScopeClient.class);
    
    @Value("${llm.dashscope.api-key:}")
    private String apiKey;
    
    @Value("${llm.dashscope.model:qwen-max}")
    private String model;
    
    @Value("${llm.dashscope.max-tokens:1000}")
    private Integer maxTokens;
    
    @Value("${llm.dashscope.temperature:0.7}")
    private Double temperature;
    
    private final Generation generation = new Generation();
    
    public ChatResponse chat(List<ChatMessage> messages) {
        log.info("调用通义千问进行对话，消息数：{}", messages.size());
        try {
            List<Message> dashscopeMessages = messages.stream()
                .map(m -> Message.builder().role(m.role()).content(m.content()).build())
                .toList();
            GenerationParam param = GenerationParam.builder()
                .apiKey(apiKey).model(model).messages(dashscopeMessages)
                .maxTokens(maxTokens).temperature(temperature)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE).build();
            GenerationResult result = generation.call(param);
            String reply = result.getOutput().getChoices().get(0).getMessage().getContent();
            int usage = result.getUsage().getTotalTokens();
            log.info("对话生成成功，消耗 token: {}", usage);
            return new ChatResponse(reply, usage);
        } catch (Exception e) {
            log.error("对话生成失败", e);
            throw new LlmException("大模型调用失败：" + e.getMessage(), e);
        }
    }
    
    public IntentAnalysisResult analyzeIntent(List<ChatMessage> conversation) {
        log.info("调用通义千问进行意向分析");
        String prompt = buildIntentAnalysisPrompt(conversation);
        List<Message> messages = List.of(
            Message.builder().role(Role.SYSTEM.getValue()).content("你是一个专业的销售意向分析助手。请分析对话中用户的意向程度，以 JSON 格式返回。").build(),
            Message.builder().role(Role.USER.getValue()).content(prompt).build()
        );
        try {
            GenerationParam param = GenerationParam.builder()
                .apiKey(apiKey).model(model).messages(messages)
                .maxTokens(500).temperature(0.3)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE).build();
            GenerationResult result = generation.call(param);
            String reply = result.getOutput().getChoices().get(0).getMessage().getContent();
            return new IntentAnalysisResult(80.0, "A", "基于大模型分析", reply);
        } catch (Exception e) {
            log.error("意向分析失败", e);
            throw new LlmException("意向分析失败：" + e.getMessage(), e);
        }
    }
    
    private String buildIntentAnalysisPrompt(List<ChatMessage> conversation) {
        StringBuilder sb = new StringBuilder();
        sb.append("请分析以下对话中用户的意向程度，从以下维度打分 (0-100)：\n");
        sb.append("1. 响应度：回复速度、回复长度、主动性\n");
        sb.append("2. 兴趣度：对产品/服务的关注程度、提问深度\n");
        sb.append("3. 紧迫度：时间敏感性、决策周期\n");
        sb.append("4. 匹配度：用户需求与产品的匹配程度\n\n");
        sb.append("对话内容：\n");
        for (ChatMessage msg : conversation) {
            String role = msg.role().equals("user") ? "用户" : "销售";
            sb.append(role).append(": ").append(msg.content()).append("\n");
        }
        sb.append("\n请以 JSON 格式返回打分结果。");
        return sb.toString();
    }
    
    public record ChatMessage(String role, String content) {}
    public record ChatResponse(String content, int usage) {}
    public record IntentAnalysisResult(double totalScore, String level, String reasoning, String rawResponse) {}
    public static class LlmException extends RuntimeException {
        public LlmException(String message, Throwable cause) { super(message, cause); }
    }
}
