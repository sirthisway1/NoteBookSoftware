package com.example.markdown_demo.controller;

import com.example.markdown_demo.common.dto.CommentCreateDTO;
import com.example.markdown_demo.common.dto.CommentViewDTO;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.common.utils.JwtUtil;
import com.example.markdown_demo.service.CommentsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xmg
 * @since 2024-07-04
 */
@RestController
@RequestMapping("/api/comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    private Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("token");
        return Integer.parseInt(JwtUtil.validateToken(token));
    }
    /**
     * 发表评论
     *
     * @param noteId 笔记ID

     * @param content 评论内容
     * @return ResponseEntity representing the outcome of the operation
     */
    @PostMapping("/post")
    public Result<Map<String, String>> postComment(HttpServletRequest request, @RequestParam Integer noteId, @RequestParam String content) {
        try {
            Integer userId = getUserIdFromRequest(request);
            commentsService.postComment(noteId, userId, content);
            return Result.success("评论发表成功");
        } catch (BusinessException e) {
            return Result.fail(e.getStatusCode(), e.getMessage());
        }
    }
    /**
     * 查看针对某篇笔记的所有评论
     *
     * @param noteId 笔记ID

     */
    @GetMapping("/view/{noteId}")
    public Result<List<CommentViewDTO>> viewComments(@PathVariable Integer noteId) {
        try {
            List<CommentViewDTO> comments = commentsService.viewComments(noteId);
            // 直接将评论列表作为 data 传递
            return Result.success("Fetched comments successfully", comments);
        } catch (BusinessException e) {
            return Result.fail(e.getStatusCode(), e.getMessage());
        }
    }

    /**
     * 查询特定笔记的评论数量
     *
     * @param noteId 笔记ID
     * @return ResponseEntity with the count of comments
     */

    @GetMapping("/count/{noteId}")
    public Result<Map<String, Object>> countCommentsByNoteId(@PathVariable Integer noteId) {
        try {
            int count = commentsService.countCommentsByNoteId(noteId);
            return Result.success(ResultType.SUCCESS.asMap("Count", count));
        } catch (BusinessException e) {
            return Result.fail(e.getStatusCode(), e.getMessage());
        }
    }
    /**
     * 删除评论
     *
     * @param noteId 笔记ID
     * @param commentId 评论ID

     * @return ResponseEntity indicating success or failure
     */
    @DeleteMapping("/delete/{noteId}/{commentId}")
    public Result<Map<String, Object>> deleteComment(HttpServletRequest request, @PathVariable Integer noteId, @PathVariable String commentId) {
        try {
            Integer userId = getUserIdFromRequest(request);
            boolean deleted = commentsService.deleteComment(noteId, commentId, userId);
            if (deleted) {
                return Result.success("评论删除成功");
            } else {
                return Result.fail(ResultType.NOT_FOUND.getCode(),"删除评论失败");
            }
        } catch (BusinessException e) {
            return Result.fail(e.getStatusCode(), e.getMessage());
        }
    }
}


