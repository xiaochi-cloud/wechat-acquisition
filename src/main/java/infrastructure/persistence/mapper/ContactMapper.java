package com.wechat.acquisition.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wechat.acquisition.infrastructure.persistence.entity.ContactEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 联系人 Mapper 接口
 */
@Mapper
public interface ContactMapper extends BaseMapper<ContactEntity> {
    
    /**
     * 根据手机号查询
     */
    ContactEntity selectByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    /**
     * 根据活动 ID 查询联系人列表
     */
    List<ContactEntity> selectByCampaignId(@Param("campaignId") String campaignId, 
                                           @Param("offset") int offset, 
                                           @Param("limit") int limit);
    
    /**
     * 统计活动联系人数量
     */
    int countByCampaignId(@Param("campaignId") String campaignId);
    
    /**
     * 批量插入联系人
     */
    int batchInsert(@Param("contacts") List<ContactEntity> contacts);
    
    /**
     * 更新联系人状态
     */
    int updateStatus(@Param("id") String id, @Param("status") String status);
    
    /**
     * 查询待添加好友的联系人
     */
    List<ContactEntity> selectPendingAddFriends(@Param("limit") int limit);
}
