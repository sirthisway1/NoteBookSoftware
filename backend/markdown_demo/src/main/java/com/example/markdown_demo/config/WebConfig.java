package com.example.markdown_demo.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射URL路径 /files/** 到文件系统路径 uploads/
        registry.addResourceHandler("/WangFiles/**")
                .addResourceLocations("classpath:/staticFile/upload/");
    }
}
