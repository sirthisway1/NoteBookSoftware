package com.example.markdown_demo.service.impl;

import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.entity.Users;
import com.example.markdown_demo.mapper.UsersMapper;
import com.example.markdown_demo.service.UsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.markdown_demo.common.utils.JwtUtil.generateToken;

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

    @Transactional
    public void register(String username, String password, String email) {
        // 使用 MyBatis-Plus 提供的方法进行查询
        if (lambdaQuery().eq(Users::getUsername, username).count() > 0) {
            throw new BusinessException(ResultType.USERNAME_ALREADY_EXISTS);
        }
        if (lambdaQuery().eq(Users::getEmail, email).count() > 0) {
            throw new BusinessException(ResultType.EMAIL_ALREADY_EXISTS);
        }

        // 创建用户实体
        Users user = new Users();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        // 使用 MyBatis-Plus 提供的方法保存用户
        save(user);
    }

    @Override
    public String login(String username, String password) throws BusinessException {
        Users user = lambdaQuery()
                .eq(Users::getUsername, username)
                .one();
        if (user == null || !password.equals(user.getPassword())) {
            throw new BusinessException(ResultType.INVALID_CREDENTIALS);
        }
        LocalDateTime now = LocalDateTime.now();
        user.setUpdatedAt(now);
        updateById(user);
        // 生成并返回 token 或者其他登录成功的标识
        return generateToken(user.getId().toString());
    }

}
