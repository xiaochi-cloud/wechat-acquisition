package com.wechat.acquisition.domain.conversation;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 话术模板聚合根
 * 
 * 职责：管理可配置的对话策略，支持多场景、多版本
 */
@Data
@Builder
public class ScriptTemplate {
    
    private String id;
    private String name;
    private String scenario;            // 适用场景
    private TemplateType type;          // 模板类型
    private String content;             // 模板内容 (支持变量)
    private List<String> variables;     // 变量列表
    private Map<String, Object> conditions; // 使用条件
    private Integer priority;           // 优先级
    private TemplateStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 创建新模板
     */
    public static ScriptTemplate create(String name, String scenario, String content) {
        return ScriptTemplate.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .scenario(scenario)
                .type(TemplateType.TEXT)
                .content(content)
                .variables(extractVariables(content))
                .priority(0)
                .status(TemplateStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 创建开场白模板
     */
    public static ScriptTemplate createOpening(String content) {
        return create("开场白", "opening", content);
    }
    
    /**
     * 创建产品介绍模板
     */
    public static ScriptTemplate createProductIntro(String content) {
        return create("产品介绍", "product_intro", content);
    }
    
    /**
     * 创建价格咨询模板
     */
    public static ScriptTemplate createPriceResponse(String content) {
        return create("价格回复", "price_inquiry", content);
    }
    
    /**
     * 创建跟进模板
     */
    public static ScriptTemplate createFollowup(String content) {
        return create("跟进话术", "followup", content);
    }
    
    /**
     * 创建结束模板
     */
    public static ScriptTemplate createClosing(String content) {
        return create("结束话术", "closing", content);
    }
    
    /**
     * 渲染模板 (替换变量)
     */
    public String render(Map<String, String> variables) {
        String rendered = this.content;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            rendered = rendered.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return rendered;
    }
    
    /**
     * 从内容中提取变量
     */
    private static List<String> extractVariables(String content) {
        List<String> variables = new ArrayList<>();
        // 简单实现：提取 {xxx} 格式的变量
        String regex = "\\{(\\w+)\\}";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            variables.add(matcher.group(1));
        }
        return variables;
    }
}

/**
 * 模板类型
 */
public TemplateType {
    TEXT,           // 文本
    IMAGE_TEXT,     // 图文
    LINK,           // 链接
    CARD            // 卡片
}

/**
 * 模板状态
 */
public TemplateStatus {
    DRAFT,          // 草稿
    ACTIVE,         // 启用
    DISABLED,       // 禁用
    ARCHIVED        // 归档
}

/**
 * 对话流程节点
 */
@Data
@Builder
static class DialogueFlow {
    
    private String id;
    private String name;
    private String scenario;
    private List<FlowNode> nodes;
    
    /**
     * 创建标准销售流程
     */
    public static DialogueFlow createStandardSalesFlow() {
        List<FlowNode> nodes = List.of(
            FlowNode.builder().step(1).type(NodeType.OPENING).templateKey("opening").build(),
            FlowNode.builder().step(2).type(NodeType.NEEDS_ANALYSIS).templateKey("needs_analysis").build(),
            FlowNode.builder().step(3).type(NodeType.PRODUCT_INTRO).templateKey("product_intro").build(),
            FlowNode.builder().step(4).type(NodeType.OBJECTION_HANDLING).templateKey("objection").build(),
            FlowNode.builder().step(5).type(NodeType.CLOSING).templateKey("closing").build()
        );
        
        return DialogueFlow.builder()
                .id(UUID.randomUUID().toString())
                .name("标准销售流程")
                .scenario("sales")
                .nodes(new ArrayList<>(nodes))
                .build();
    }
}

/**
 * 流程节点
 */
@Data
@Builder
static class FlowNode {
    private Integer step;
    private NodeType type;
    private String templateKey;
    private Map<String, Object> conditions;
    private List<String> nextSteps; // 下一步可能的节点
}

/**
 * 节点类型
 */
public NodeType {
    OPENING,            // 开场
    NEEDS_ANALYSIS,     // 需求分析
    PRODUCT_INTRO,      // 产品介绍
    OBJECTION_HANDLING, // 异议处理
    CLOSING,            // 成交
    FOLLOWUP            // 跟进
}
