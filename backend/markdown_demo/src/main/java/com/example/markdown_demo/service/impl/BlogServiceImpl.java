package com.example.markdown_demo.service.impl;

import com.example.markdown_demo.entity.Blog;
import com.example.markdown_demo.mapper.BlogMapper;
import com.example.markdown_demo.service.BlogService;
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
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

}
