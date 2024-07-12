package com.example.markdown_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.markdown_demo.common.dto.LikeDTO;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.entity.NoteLikes;
import com.example.markdown_demo.mapper.NoteLikesMapper;
import com.example.markdown_demo.service.NoteLikesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @since 2024-07-04
 */
@Service
public class NoteLikesServiceImpl extends ServiceImpl<NoteLikesMapper, NoteLikes> implements NoteLikesService {

    @Autowired
    private NoteLikesMapper noteLikesMapper;

    @Override
    public LikeDTO likeOrUnlikeNote(Integer noteId, Integer userId) {
        QueryWrapper<NoteLikes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note_id", noteId).eq("user_id", userId);
        NoteLikes existingLike = noteLikesMapper.selectOne(queryWrapper);
        boolean isLiked;

        if (existingLike == null) {
            // 点赞
            NoteLikes noteLike = new NoteLikes();
            noteLike.setNoteId(noteId);
            noteLike.setUserId(userId);
            noteLikesMapper.insert(noteLike);
            isLiked = true; // 操作为点赞
        } else {
            // 取消点赞
            noteLikesMapper.delete(queryWrapper);
            isLiked = false; // 操作为取消点赞
        }

        // 查询并返回当前的点赞总数
        QueryWrapper<NoteLikes> countQuery = new QueryWrapper<>();
        countQuery.eq("note_id", noteId);
        int likeCount = Math.toIntExact(noteLikesMapper.selectCount(countQuery));

        return new LikeDTO(isLiked, likeCount);
    }




    @Override
    public int countLikesByNoteId(Integer noteId) {
        if (noteId == null) {
            throw new BusinessException(ResultType.INVALID_REQUEST_BODY.getCode(), "笔记号不能为空");
        }

        QueryWrapper<NoteLikes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note_id", noteId);
        try {
            return Math.toIntExact(noteLikesMapper.selectCount(queryWrapper));
        } catch (Exception e) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR.getCode(), "获取数量失败， " + e.getMessage());
        }
    }


    @Override
    public boolean isNoteLikedByUser(Integer noteId, Integer userId) {
        if (noteId == null || userId == null) {
            throw new BusinessException(ResultType.INVALID_REQUEST_BODY.getCode(), "笔记号或账号不能为空");
        }

        QueryWrapper<NoteLikes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note_id", noteId).eq("user_id", userId);
        try {
            NoteLikes existingLike = noteLikesMapper.selectOne(queryWrapper);
            return existingLike != null;
        } catch (Exception e) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR.getCode(), "无法查看是否点赞， " + e.getMessage());
        }
    }


}
