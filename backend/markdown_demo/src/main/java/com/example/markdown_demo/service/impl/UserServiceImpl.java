package com.example.markdown_demo.service.impl;

import com.example.markdown_demo.entity.User;
import com.example.markdown_demo.mapper.UserMapper;
import com.example.markdown_demo.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xmg
 * @since 2024-07-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
