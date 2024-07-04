package com.example.markdown_demo.common.shiro;

import com.example.markdown_demo.common.lang.JwtValidationException;
import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.common.lang.ResultType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Description: JwtFilter
 * shiro的过滤器，用于拦截请求，
 * @Author: hahaha
 * @Date: 2024/4/11 17:26
 */

@Slf4j
@Component
public class JwtFilter extends AccessControlFilter {

    /**
     * isAccessAllowed()判断是否携带了有效的JwtToken
     * onAccessDenied()是没有携带JwtToken的时候进行账号密码登录
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        /**
         * 1. 返回true，shiro就直接允许访问url
         * 2. 返回false，shiro才会根据onAccessDenied的方法的返回值决定是否允许访问url
         *  这里先让它始终返回false来使用onAccessDenied()方法
         *  如果带有 token，则对 token 进行检查，否则直接通过
         *
         */
        try {
            onAccessDenied(servletRequest, servletResponse);
//            return true;
        } catch (Exception e) {
            log.error("isAccessAllowed error:", e);
            responseError(servletResponse, ResultType.UNAUTHORIZED.getCode(), "Authentication failed: " + e.getMessage());
//            return false;
        }
        return true;
    }


    /**
     * @param servletRequest
     * @param servletResponse
     * @throws Exception
     * @return 返回结果为true表明登录通过
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        /**
         *  跟前端约定将jwtToken放在请求的Header的token中，token:token
         */
        log.info("onAccessDenied方法被调用");
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader("token");
        //如果token为空的话，返回true，交给控制层进行判断；也会达到没有权限的作用
        if (token == null) {
            responseError(servletResponse, ResultType.UNAUTHORIZED.getCode(), "No token provided");
            return false;
        }
        JwtToken jwtToken = new JwtToken(token);
        try {
            //进行登录处理，委托realm进行登录认证，调用JwtRealm进行的认证,doGetAuthenticationInfo
            getSubject(servletRequest, servletResponse).login(jwtToken);
            return true;
        } catch (JwtValidationException e) {
            // Handle specific custom exceptions
            responseError(servletResponse, e.getStatusCode(), e.getMessage());
            return false;
        }
//        catch (AuthenticationException e) {
//            responseError(servletResponse, ResultType.UNAUTHORIZED.getCode(), "Authentication failed: " + e.getMessage());
//            return false;
//        }
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        System.out.println("进入拦截器");
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 设置CORS头部
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        resp.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization,token");
        resp.setHeader("Access-Control-Allow-Credentials", "true");

        if ("OPTIONS".equals(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return false; // 阻止后续的过滤器链执行
        }

        return super.preHandle(request, response);
    }

    //失败要执行的方法
    private void responseError(ServletResponse response, String statusCode, String message) throws IOException {

        HttpServletResponse resp = WebUtils.toHttp(response);
        resp.setStatus(HttpStatus.UNAUTHORIZED.value());
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=utf-8");
        ObjectMapper mapper = new ObjectMapper();

        try (ServletOutputStream out = resp.getOutputStream()) {
            Result<String> result = new Result<>();
            result.setStatusCode(statusCode);
            result.setMessage(message);
            String json = mapper.writeValueAsString(result);
            out.write(json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new AuthenticationException("Response writing failed with IOException: " + e.getMessage());
        }
    }
}
