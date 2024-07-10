package com.example.markdown_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    public boolean likeOrUnlikeNote(Integer noteId, Integer userId) {
        QueryWrapper<NoteLikes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note_id", noteId).eq("user_id", userId);
        NoteLikes existingLike = noteLikesMapper.selectOne(queryWrapper);
        if (existingLike == null) {
            // 点赞
            NoteLikes noteLike = new NoteLikes();
            noteLike.setNoteId(noteId);
            noteLike.setUserId(userId);
            noteLikesMapper.insert(noteLike);
            return true;
        } else {
            // 取消点赞
            noteLikesMapper.delete(queryWrapper);
            return false;
        }
    }


    @Override
    public int countLikesByNoteId(Integer noteId) {
        if (noteId == null) {
            throw new BusinessException(ResultType.INVALID_REQUEST_BODY.getCode(), "笔记号不能为空");
        }

        QueryWrapper<NoteLikes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("笔记号", noteId);
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
        queryWrapper.eq("笔记号", noteId).eq("账号", userId);
        try {
            NoteLikes existingLike = noteLikesMapper.selectOne(queryWrapper);
            return existingLike != null;
        } catch (Exception e) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR.getCode(), "无法查看是否点赞， " + e.getMessage());
        }
    }


}
