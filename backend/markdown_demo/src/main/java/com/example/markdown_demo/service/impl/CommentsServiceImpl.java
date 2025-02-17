package com.example.markdown_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.markdown_demo.common.dto.CommentCreateDTO;
import com.example.markdown_demo.common.dto.CommentViewDTO;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.common.utils.JwtUtil;
import com.example.markdown_demo.entity.Comments;
import com.example.markdown_demo.entity.Users;
import com.example.markdown_demo.mapper.CommentsMapper;
import com.example.markdown_demo.mapper.NotesMapper;
import com.example.markdown_demo.mapper.UsersMapper;
import com.example.markdown_demo.service.CommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.events.Comment;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private NotesMapper notesMapper;
    @Autowired
    private CommentsMapper commentsMapper; // 正确的命名和类型

    @Autowired
    private UsersMapper usersMapper;

    private boolean noteExists(Integer noteId) {
        return notesMapper.selectById(noteId) != null;
    }

    /**
     * 发表评论
     *
     * @param noteId 笔记ID

     * @param content 评论内容
     * @throws BusinessException 如果操作失败
     */
    @Override
    @Transactional
    public void postComment(Integer noteId, Integer userId, String content) throws BusinessException {
        if (noteId == null || content == null || content.trim().isEmpty()) {
            throw new BusinessException(ResultType.INVALID_REQUEST_BODY, "笔记id，评论不能为空");
        }
        if (!noteExists(noteId)) {
            throw new BusinessException(ResultType.NOT_FOUND, "笔记不存在");
        }
        // 创建并设置评论实体
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // 或者您需要的任何其他格式
        String createdAtStr = LocalDateTime.now().format(formatter);

        Comments comment = new Comments()
                .setNoteId(noteId)
                .setUserId(userId)
                .setContent(content)
                .setCreatedAt(createdAtStr);

        // 尝试保存评论到数据库
        try {
            commentsMapper.insert(comment);
        } catch (Exception e) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR, "创建评论失败： " + e.getMessage());
        }
    }



    /**
     * 查看针对某篇笔记的所有评论
     *
     * @param noteId 笔记ID
     * @return 评论详情列表
     * @throws BusinessException 如果操作失败
     */




    @Override
    @Transactional(readOnly = true)
    public List<CommentViewDTO> viewComments(Integer noteId) throws BusinessException {
        if (noteId == null) {
            throw new BusinessException(ResultType.PATH_NOT_FOUND.getCode(), "笔记id不能为空");
        }
        if (!noteExists(noteId)) {
            throw new BusinessException(ResultType.NOT_FOUND.getCode(), "笔记不存在");
        }

        // 使用 MyBatis Plus 的查询封装
        QueryWrapper<Comments> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note_id", noteId);
        List<Comments> commentsList;
        try {
            commentsList = commentsMapper.selectList(queryWrapper);
        } catch (Exception e) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR.getCode(), "Failed to retrieve comments due to: " + e.getMessage());
        }

        // 将实体列表转换成 DTO 列表，并获取 username
        return commentsList.stream()
                .map(comment -> {
                    // 通过用户 ID 获取用户名
                    Users user = usersMapper.selectById(comment.getUserId());
                    if (user == null) {
                        throw new RuntimeException("找不到这个id " + comment.getUserId());
                    }
                    String username = getUsernameByUserId(comment.getUserId());
                    return new CommentViewDTO(user.getAvatar(),comment.getContent(), username, comment.getCreatedAt(), comment.getId());
                })
                .collect(Collectors.toList());


    }
    public String getUsernameByUserId(Integer userId) {
        Users user = usersMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        return user.getUsername();
    }



    /**
     * 查询特定笔记的评论数量
     *
     * @param noteId 笔记ID
     * @return 评论数量
     * @throws BusinessException 如果操作失败
     */
    @Override
    public int countCommentsByNoteId(Integer noteId) throws BusinessException {
        if (noteId == null) {
            throw new BusinessException(ResultType.PATH_NOT_FOUND.getCode(), "笔记id不能为空");
        }
        if (noteExists(noteId)) {
            throw new BusinessException(ResultType.NOT_FOUND, "笔记不存在");
        }
        QueryWrapper<Comments> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note_id", noteId);

        try {
            return Math.toIntExact(commentsMapper.selectCount(queryWrapper));
        } catch (Exception e) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR.getCode(), "查询评论数失败" + e.getMessage());
        }
    }



    /**
     * 删除评论
     *
     * @param noteId 笔记ID
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否删除成功
     * @throws BusinessException 如果操作失败
     */
    @Override
    public boolean deleteComment(Integer noteId, String commentId, Integer userId) throws BusinessException {
        if (noteId == null || commentId == null ) {
            throw new BusinessException(ResultType.INVALID_REQUEST_BODY.getCode(), "笔记id，评论id不能为空");
        }
        if (!noteExists(noteId)) {
            throw new BusinessException(ResultType.NOT_FOUND, "笔记不存在");
        }
        QueryWrapper<Comments> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", commentId)
                .and(wrapper -> wrapper.eq("note_id", noteId).or().eq("user_id", userId));

        try {
            boolean deleted = commentsMapper.delete(queryWrapper) > 0;
            if (!deleted) {
                throw new BusinessException(ResultType.NOT_FOUND.getCode(), "在提供的 ID 中找不到评论，或者您无权删除它.");
            }
            return deleted;
        } catch (Exception e) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR.getCode(), "由于以下原因，无法删除评论： " + e.getMessage());
        }


    }


}








