package com.example.markdown_demo.service.impl;

import com.example.markdown_demo.entity.Users;
import com.example.markdown_demo.mapper.UsersMapper;
import com.example.markdown_demo.service.UsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xmg
 * @since 2024-07-04
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

}
