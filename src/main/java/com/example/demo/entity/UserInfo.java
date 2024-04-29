package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author luoxuri
 * @date 2024-03-25 17:56:49
 */
@TableName("user_info")
@Data
public class UserInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String number;

    private String name;


}
