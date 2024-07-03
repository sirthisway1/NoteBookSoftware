package com.example.markdown_demo.shiro;

import com.example.markdown_demo.exception.JwtValidationException;
import com.example.markdown_demo.service.UserService;
import com.example.markdown_demo.utils.JwtUtil;
import com.example.markdown_demo.utils.ResultType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description: JwtRealm
 * shiro的Realm，用于处理JwtToken的认证和授权
 * @Author: hahaha
 * @Date: 2024/4/11 17:26
 */

@Slf4j
@Component
public class JwtRealm extends AuthorizingRealm {

    @Resource
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    /**
     * 多重写一个support
     * 标识这个Realm是专门用来验证JwtToken
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String jwt = (String) token.getCredentials();
        // 获取jwt中关于用户id，解码过程中如果token过期或者被篡改会抛出异常
//        String id = null;
        log.info("处理jwt认证", jwt);
        try {
            // Validate the token
            String id = jwtUtil.validateToken(jwt);
            log.info("jwt认证成功，用户id：", id);
            // 标记用户为活跃状态
            userService.markUserActive(Integer.parseInt(id));
            return new SimpleAuthenticationInfo(jwt, jwt, getName());
        }
        catch (ExpiredJwtException e) {
            throw new JwtValidationException(ResultType.UNAUTHORIZED.getCode(), "Token已过期，请重新登录");
        } catch (JwtException e) {
            throw new JwtValidationException(ResultType.UNAUTHORIZED.getCode(), "无效的Token");
        }
    }

    /**
     * 授权时调用
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return new SimpleAuthorizationInfo();
    }
}
