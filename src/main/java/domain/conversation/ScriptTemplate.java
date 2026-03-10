package com.wechat.acquisition.domain.conversation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptTemplate {
    private String id;
    private String name;
    private String scenario;
    private TemplateType type;
    private String content;
    private List<String> variables;
    private Map<String, Object> conditions;
    private Integer priority;
    private TemplateStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public ScriptTemplate() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    public TemplateType getType() { return type; }
    public void setType(TemplateType type) { this.type = type; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public List<String> getVariables() { return variables; }
    public void setVariables(List<String> variables) { this.variables = variables; }
    public Map<String, Object> getConditions() { return conditions; }
    public void setConditions(Map<String, Object> conditions) { this.conditions = conditions; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public TemplateStatus getStatus() { return status; }
    public void setStatus(TemplateStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public static ScriptTemplate create(String name, String scenario, String content) {
        ScriptTemplate t = new ScriptTemplate();
        t.setId(UUID.randomUUID().toString());
        t.setName(name);
        t.setScenario(scenario);
        t.setType(TemplateType.TEXT);
        t.setContent(content);
        t.setVariables(extractVariables(content));
        t.setPriority(0);
        t.setStatus(TemplateStatus.ACTIVE);
        t.setCreatedAt(LocalDateTime.now());
        t.setUpdatedAt(LocalDateTime.now());
        return t;
    }
    
    public static ScriptTemplate createOpening(String content) { return create("开场白", "opening", content); }
    public static ScriptTemplate createProductIntro(String content) { return create("产品介绍", "product_intro", content); }
    public static ScriptTemplate createPriceResponse(String content) { return create("价格回复", "price_inquiry", content); }
    public static ScriptTemplate createFollowup(String content) { return create("跟进话术", "followup", content); }
    public static ScriptTemplate createClosing(String content) { return create("结束话术", "closing", content); }
    
    public String render(Map<String, String> vars) {
        String rendered = this.content;
        if (vars != null) {
            for (Map.Entry<String, String> entry : vars.entrySet()) {
                rendered = rendered.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return rendered;
    }
    
    private static List<String> extractVariables(String content) {
        List<String> variables = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{(\\w+)\\}");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            variables.add(matcher.group(1));
        }
        return variables;
    }
}

enum TemplateType { TEXT, IMAGE_TEXT, LINK, CARD }
enum TemplateStatus { DRAFT, ACTIVE, DISABLED, ARCHIVED }

class DialogueFlow {
    private String id;
    private String name;
    private String scenario;
    private List<FlowNode> nodes;
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    public List<FlowNode> getNodes() { return nodes; }
    public void setNodes(List<FlowNode> nodes) { this.nodes = nodes; }
    
    public static DialogueFlow createStandardSalesFlow() {
        DialogueFlow flow = new DialogueFlow();
        flow.setId(UUID.randomUUID().toString());
        flow.setName("标准销售流程");
        flow.setScenario("sales");
        List<FlowNode> nodes = new ArrayList<>();
        nodes.add(new FlowNode(1, NodeType.OPENING, "opening"));
        nodes.add(new FlowNode(2, NodeType.NEEDS_ANALYSIS, "needs_analysis"));
        nodes.add(new FlowNode(3, NodeType.PRODUCT_INTRO, "product_intro"));
        nodes.add(new FlowNode(4, NodeType.OBJECTION_HANDLING, "objection"));
        nodes.add(new FlowNode(5, NodeType.CLOSING, "closing"));
        flow.setNodes(nodes);
        return flow;
    }
}

class FlowNode {
    private Integer step;
    private NodeType type;
    private String templateKey;
    
    public FlowNode() {}
    public FlowNode(Integer step, NodeType type, String templateKey) {
        this.step = step; this.type = type; this.templateKey = templateKey;
    }
    public Integer getStep() { return step; }
    public void setStep(Integer step) { this.step = step; }
    public NodeType getType() { return type; }
    public void setType(NodeType type) { this.type = type; }
    public String getTemplateKey() { return templateKey; }
    public void setTemplateKey(String templateKey) { this.templateKey = templateKey; }
}

enum NodeType { OPENING, NEEDS_ANALYSIS, PRODUCT_INTRO, OBJECTION_HANDLING, CLOSING, FOLLOWUP }
