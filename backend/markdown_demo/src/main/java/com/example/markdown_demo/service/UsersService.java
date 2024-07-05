package com.example.markdown_demo.service;

import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xmg
 * @since 2024-07-04
 */
public interface UsersService extends IService<Users> {
    /**
     * 用户注册
     *
     * @param username 用户名
     * @param password 密码
     * @param email    邮箱
     * @throws BusinessException 如果用户名或邮箱已存在
     */
    void register(String username, String password, String email) throws BusinessException;

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功后的 token
     * @throws BusinessException 如果登录凭证无效
     */
    String login(String username, String password) throws BusinessException;
}
