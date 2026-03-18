package com.wechat.acquisition.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wechat.acquisition.infrastructure.persistence.entity.CampaignEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CampaignMapper extends BaseMapper<CampaignEntity> {
}
