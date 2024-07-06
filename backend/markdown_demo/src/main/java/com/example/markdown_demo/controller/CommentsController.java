package com.example.markdown_demo.controller;

import com.example.markdown_demo.common.dto.CommentCreateDTO;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.common.utils.JwtUtil;
import com.example.markdown_demo.service.CommentsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

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
@Controller
@RequestMapping("/comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;
    private Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            throw new BusinessException(ResultType.UNAUTHORIZED);
        }
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
    public ResponseEntity<?> postComment(HttpServletRequest request, @RequestParam Integer noteId, @RequestParam String content) {
        try {
            Integer userId = getUserIdFromRequest(request); // 从请求中获取用户 ID
            commentsService.postComment(noteId, userId, content);
            return ResponseEntity.ok().body(ResultType.SUCCESS.asMap("Message", "Comment posted successfully"));
        } catch (BusinessException e) {
            ResultType resultType = ResultType.fromCode(e.getStatusCode());
            return ResponseEntity.status(HttpStatus.valueOf(Integer.parseInt(e.getStatusCode())))
                    .body(resultType.asMap("Error", e.getMessage()));
        }
    }

    /**
     * 查看针对某篇笔记的所有评论
     *
     * @param noteId 笔记ID
     * @return ResponseEntity containing the comments
     */
    @GetMapping("/view/{noteId}")
    public ResponseEntity<?> viewComments(@PathVariable Integer noteId) {
        try {
            List<CommentCreateDTO> comments = commentsService.viewComments(noteId);
            return ResponseEntity.ok(ResultType.SUCCESS.asMap("Comments", comments));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.valueOf(Integer.parseInt(e.getStatusCode()))).body(ResultType.fromCode(e.getStatusCode()).asMap("Error", e.getMessage()));
        }
    }

    /**
     * 查询特定笔记的评论数量
     *
     * @param noteId 笔记ID
     * @return ResponseEntity with the count of comments
     */
    @GetMapping("/count/{noteId}")
    public HttpEntity<Map<String, Object>> countCommentsByNoteId(@PathVariable Integer noteId) {
        try {
            int count = commentsService.countCommentsByNoteId(noteId);
            return ResponseEntity.ok(ResultType.SUCCESS.asMap("Count", count));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.valueOf(Integer.parseInt(e.getStatusCode())))
                    .body(ResultType.fromCode(e.getStatusCode()).asMap("Error", e.getMessage()));
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
    public ResponseEntity<?> deleteComment(HttpServletRequest request, @PathVariable Integer noteId, @PathVariable String commentId) {
        try {
            Integer userId = getUserIdFromRequest(request); // 从请求中获取用户 ID
            boolean deleted = commentsService.deleteComment(noteId, commentId, userId);
            if (deleted) {
                return ResponseEntity.ok(ResultType.SUCCESS.asMap("Message", "Comment deleted successfully"));
            } else {
                return ResponseEntity.badRequest().body(ResultType.NOT_FOUND.asMap("Error", "Failed to delete comment"));
            }
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.valueOf(Integer.parseInt(e.getStatusCode())))
                    .body(ResultType.fromCode(e.getStatusCode()).asMap("Error", e.getMessage()));
        }
    }

}


