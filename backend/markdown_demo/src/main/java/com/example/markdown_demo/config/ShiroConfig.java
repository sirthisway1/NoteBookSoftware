package com.example.markdown_demo.config;

/**
 * @Author: hahaha
 * @Date: 2024/4/11 16:29
 */


import com.example.markdown_demo.common.shiro.JwtFilter;
import com.example.markdown_demo.common.shiro.JwtRealm;
import jakarta.servlet.Filter;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * shiro的三个重要配置
 * 1、Realm
 * 2、DefaultWebSecurityManager
 * 3、ShiroFilterFactoryBean
 */

/**
 1.用户请求，不携带token，就在JwtFilter处进行错误处理返回，让它去登陆
 2.用户请求，携带token，就到JwtFilter中获取jwt，使用JwtRealm进行认证，若token过期则返回401状态码
 3.在JwtRealm中进行认证判断这个token是否有效，
 */

@Configuration
public class ShiroConfig {

    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager(@Qualifier("jwtRealm") JwtRealm jwtRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置自定义Realm
        securityManager.setRealm(jwtRealm);
        // 关闭shiroDao功能，关闭session
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        // 不需要将ShiroSession中的东西存到任何地方包括Http Session中）
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        // securityManager.setSubjectFactory(subjectFactory());
        return securityManager;
    }

    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager securityManager) {

        /**
         *  注册jwt过滤器，除/login,/register外都先经过jwtFilter
         *
         *   先经过过滤器，如果检测到请求头存在 token，则用 token 去 login，接着走 Realm 去验证
         */
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        HashMap<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt", new JwtFilter());
        shiroFilter.setFilters(filterMap);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        map.put("/user/login", "anon");
        map.put("/user/register", "anon");
        map.put("/api/files", "anon");

        // 所有请求通过我们自己的JWT Filter
        map.put("/**", "jwt");
        shiroFilter.setFilterChainDefinitionMap(map);
        return shiroFilter;
    }

    /**
     * 解决@RequiresAuthentication注解不生效的配置
     */
    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * 为Spring-Bean开启对Shiro注解的支持
     */
    @Bean("authorizationAttributeSourceAdvisor")
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
