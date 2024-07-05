package com.example.markdown_demo.service;

import com.example.markdown_demo.entity.NoteLikes;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xmg
 * @since 2024-07-04
 */
public interface NoteLikesService {

    boolean likeOrUnlikeNote(Integer noteId, Integer userId);

    int countLikesByNoteId(Integer noteId);
    boolean isNoteLikedByUser(Integer noteId, Integer userId);
}
