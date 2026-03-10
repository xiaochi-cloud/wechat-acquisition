package com.wechat.acquisition.infrastructure.llm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 通义千问 (DashScope) 客户端
 * 
 * 职责：封装大模型调用，包括对话生成、意向分析等
 */

@Component

    private static final Logger log = LoggerFactory.getLogger(DashScopeClient.class);
@RequiredArgsConstructor
public class DashScopeClient {
    
    @Value("${llm.dashscope.api-key:}")
    private String apiKey;
    
    @Value("${llm.dashscope.model:qwen-max}")
    private String model;
    
    @Value("${llm.dashscope.max-tokens:1000}")
    private Integer maxTokens;
    
    @Value("${llm.dashscope.temperature:0.7}")
    private Double temperature;
    
    private final Generation generation = new Generation();
    
    /**
     * 对话生成
     */
    public ChatResponse chat(List<ChatMessage> messages) {
        log.info("调用通义千问进行对话，消息数：{}", messages.size());
        
        try {
            // 转换为 DashScope 格式
            List<Message> dashscopeMessages = messages.stream()
                .map(m -> new Message(m.role(), m.content()))
                .toList();
            
            GenerationParam param = GenerationParam.builder()
                .apiKey(apiKey)
                .model(model)
                .messages(dashscopeMessages)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
            
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
    
    /**
     * 意向分析
     */
    public IntentAnalysisResult analyzeIntent(List<ChatMessage> conversation) {
        log.info("调用通义千问进行意向分析");
        
        String prompt = buildIntentAnalysisPrompt(conversation);
        
        List<Message> messages = List.of(
            new Message(Role.SYSTEM.getValue(), "你是一个专业的销售意向分析助手。请分析对话中用户的意向程度，以 JSON 格式返回。"),
            new Message(Role.USER.getValue(), prompt)
        );
        
        try {
            GenerationParam param = GenerationParam.builder()
                .apiKey(apiKey)
                .model(model)
                .messages(messages)
                .maxTokens(500)
                .temperature(0.3)  // 分析任务用较低温度
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
            
            GenerationResult result = generation.call(param);
            String reply = result.getOutput().getChoices().get(0).getMessage().getContent();
            
            // TODO: 解析 JSON 响应
            // 期望格式：{"dimensions": {...}, "total_score": 80, "level": "A", "reasoning": "..."}
            
            return new IntentAnalysisResult(80.0, "A", "基于大模型分析", reply);
            
        } catch (Exception e) {
            log.error("意向分析失败", e);
            throw new LlmException("意向分析失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 构建意向分析 Prompt
     */
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
        
        sb.append("\n请以 JSON 格式返回：\n");
        sb.append("{\n");
        sb.append("  \"dimensions\": {\"responsiveness\": 0-100, \"interest\": 0-100, \"urgency\": 0-100, \"match\": 0-100},\n");
        sb.append("  \"total_score\": 0-100,\n");
        sb.append("  \"level\": \"A/B/C/D\",\n");
        sb.append("  \"reasoning\": \"分析理由\"\n");
        sb.append("}");
        
        return sb.toString();
    }
    
    /**
     * 用户分层分析
     */
    public UserSegmentationResult segmentUser(String profileData) {
        log.info("调用通义千问进行用户分层分析");
        
        // TODO: 实现用户分层分析
        
        return new UserSegmentationResult("high_value", 0.85, List.of("高净值", "决策者"));
    }
    
    /**
     * 聊天消息
     */
    public record ChatMessage(String role, String content) {}
    
    /**
     * 对话响应
     */
    public record ChatResponse(String content, int usage) {}
    
    /**
     * 意向分析结果
     */
    public record IntentAnalysisResult(
        double totalScore,
        String level,
        String reasoning,
        String rawResponse
    ) {}
    
    /**
     * 用户分层结果
     */
    public record UserSegmentationResult(
        String segment,
        double confidence,
        List<String> tags
    ) {}
    
    /**
     * LLM 异常
     */
    public static class LlmException extends RuntimeException {
        public LlmException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
