package com.wechat.acquisition.domain.acquisition;
public enum CampaignStatus { DRAFT("草稿"), RUNNING("运行中"), PAUSED("已暂停"), STOPPED("已停止"), COMPLETED("已完成");
    private final String description;
    CampaignStatus(String description) { this.description = description; }
    public String getDescription() { return description; }
}
