package com.example.demo.repo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author luoxuri
 * @date 2024-03-25 18:09:13
 */
@Mapper
public interface UserInfoRepo extends BaseMapper<UserInfo> {

    /**
     * 批量插入
     *
     * @param list
     */
    void insertBatch(@Param("list") List<UserInfo> list);
}
