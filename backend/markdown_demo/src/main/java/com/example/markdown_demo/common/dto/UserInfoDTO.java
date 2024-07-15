package com.example.markdown_demo.common.dto;

import lombok.Data;

@Data
public class UserInfoDTO {
    private String username;
    private String email;
    private String bio;       // 添加个人简介字段
    private String avatar;    // 添加头像URL字段
    private String password;
}