package com.example.markdown_demo.service;

import com.example.markdown_demo.common.dto.CommentCreateDTO;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.entity.Comments;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.xml.stream.events.Comment;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xmg
 * @since 2024-07-04
 */
public interface CommentsService extends IService<Comments> {


    /**
     * 发表评论
     *
     * @param noteId 笔记ID
     * @param userId 用户ID
     * @param content 评论内容
     * @throws BusinessException 如果操作失败
     */
    void postComment(Integer noteId, Integer userId, String content) throws BusinessException;

    /**
     * 查看针对某篇笔记的所有评论
     *
     * @param noteId 笔记ID
     * @return 评论详情列表
     * @throws BusinessException 如果操作失败
     */
    List<CommentCreateDTO> viewComments(Integer noteId) throws BusinessException;

    /**
     * 查询特定笔记的评论数量
     *
     * @param noteId 笔记ID
     * @return 评论数量
     */
    int countCommentsByNoteId(Integer noteId);

    /**
     * 删除评论
     *
     * @param noteId 笔记ID
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否删除成功
     * @throws BusinessException 如果操作失败
     */
    boolean deleteComment(Integer noteId, String commentId, Integer userId) throws BusinessException;
    }

