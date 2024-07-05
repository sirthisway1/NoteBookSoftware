package com.example.markdown_demo.controller;

import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.service.NoteLikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xmg
 * @since 2024-07-04
 */
@Controller
@RequestMapping("/noteLikes")
public class NoteLikesController {

    @Autowired
    private NoteLikesService noteLikesService;

    /**
     * 点赞或取消点赞笔记
     * @param noteId 笔记ID
     * @param userId 用户ID
     * @return ResponseEntity with operation result
     */
    @PostMapping("/toggleLike")
    public ResponseEntity<?> likeOrUnlikeNote(@RequestParam Integer noteId, @RequestParam Integer userId) {
        try {
            boolean isLiked = noteLikesService.likeOrUnlikeNote(noteId, userId);
            if (isLiked) {
                return ResponseEntity.ok(ResultType.SUCCESS.asMap("Message", "Note liked successfully"));
            } else {
                return ResponseEntity.ok(ResultType.SUCCESS.asMap("Message", "Like removed successfully"));
            }
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.valueOf(Integer.parseInt(e.getStatusCode())))
                    .body(ResultType.fromCode(e.getStatusCode()).asMap("Error", e.getMessage()));
        }
    }

    /**
     * 统计笔记的点赞数量
     * @param noteId 笔记ID
     * @return ResponseEntity with the count of likes
     */
    @GetMapping("/count/{noteId}")
    public ResponseEntity<?> countLikesByNoteId(@PathVariable Integer noteId) {
        try {
            int count = noteLikesService.countLikesByNoteId(noteId);
            return ResponseEntity.ok(ResultType.SUCCESS.asMap("Count", count));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.valueOf(Integer.parseInt(e.getStatusCode())))
                    .body(ResultType.fromCode(e.getStatusCode()).asMap("Error", e.getMessage()));
        }
    }

    /**
     * 检查用户是否已经点赞了笔记
     * @param noteId 笔记ID
     * @param userId 用户ID
     * @return ResponseEntity indicating if the note is liked by the user
     */
    @GetMapping("/isLiked/{noteId}/{userId}")
    public ResponseEntity<?> isNoteLikedByUser(@PathVariable Integer noteId, @PathVariable Integer userId) {
        try {
            boolean isLiked = noteLikesService.isNoteLikedByUser(noteId, userId);
            return ResponseEntity.ok(ResultType.SUCCESS.asMap("Liked", isLiked));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.valueOf(Integer.parseInt(e.getStatusCode())))
                    .body(ResultType.fromCode(e.getStatusCode()).asMap("Error", e.getMessage()));
        }
    }
}
