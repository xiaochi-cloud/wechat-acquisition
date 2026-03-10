package com.wechat.acquisition.infrastructure.persistence.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wechat.acquisition.domain.acquisition.Contact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 联系人 Mapper
 */
@Mapper
public interface ContactMapper extends BaseMapper<Contact> {
    
    /**
     * 根据状态查询
     */
    List<Contact> selectByStatus(@Param("status") String status);
    
    /**
     * 查询待添加好友的联系人
     */
    List<Contact> selectPendingAddFriends(@Param("limit") int limit);
    
    /**
     * 根据手机号查询
     */
    Contact selectByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    /**
     * 批量插入
     */
    int batchInsert(@Param("contacts") List<Contact> contacts);
}
