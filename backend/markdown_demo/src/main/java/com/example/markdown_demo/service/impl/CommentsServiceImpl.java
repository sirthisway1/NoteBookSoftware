package com.example.markdown_demo.service.impl;

import com.example.markdown_demo.entity.Comments;
import com.example.markdown_demo.mapper.CommentsMapper;
import com.example.markdown_demo.service.CommentsService;
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
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements CommentsService {

}
