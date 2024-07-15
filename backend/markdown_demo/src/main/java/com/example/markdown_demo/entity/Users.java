package com.example.markdown_demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.markdown_demo.common.typehandler.LocalDateTimeToStringTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author xmg
 * @since 2024-07-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "m_users", autoResultMap = true)
public class Users implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String email;

    // 添加新的头像和个人简介字段
    private String avatar;
    private String bio;

    @TableField(typeHandler = LocalDateTimeToStringTypeHandler.class)
    private String createdAt;
    @TableField(typeHandler = LocalDateTimeToStringTypeHandler.class)
    private String updatedAt;
}