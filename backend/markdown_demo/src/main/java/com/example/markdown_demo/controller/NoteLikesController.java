package com.example.markdown_demo.controller;

import com.example.markdown_demo.common.dto.LikeDTO;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.common.utils.JwtUtil;
import com.example.markdown_demo.service.NoteLikesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
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
@RequestMapping("/api/noteLikes")
public class NoteLikesController {

    @Autowired
    private NoteLikesService noteLikesService;

    private Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("token");
        return Integer.parseInt(JwtUtil.validateToken(token));
    }
    /**
     * 点赞或取消点赞笔记
     * @param noteId 笔记ID

     * @return ResponseEntity with operation result
     */
    @PostMapping("/toggleLike")
    public Result<LikeDTO> toggleLike(HttpServletRequest request, @RequestParam Integer noteId) {
        Integer userId = getUserIdFromRequest(request); // 从请求中获取用户ID
        try {
            LikeDTO likeResult = noteLikesService.likeOrUnlikeNote(noteId, userId); // 调用服务层方法获取点赞操作的结果和点赞总数
            return Result.success("Like status toggled successfully", likeResult); // 使用Result类的success方法，返回成功的消息和数据
        } catch (BusinessException e) {
            return Result.fail(e.getStatusCode(), e.getMessage()); // 错误处理，返回失败的状态码和消息
        }
    }

    /**
     * 统计笔记的点赞数量
     * @param noteId 笔记ID
     * @return ResponseEntity with the count of likes
     */
    @GetMapping("/count/{noteId}")
    public Result<Map<String, Object>> countLikesByNoteId(@PathVariable Integer noteId) {
        try {
            int count = noteLikesService.countLikesByNoteId(noteId);
            return Result.success(ResultType.SUCCESS.asMap("count", count));
        } catch (BusinessException e) {
            return Result.fail(e.getStatusCode(), e.getMessage());
        }
    }

    /**
     * 检查用户是否已经点赞了笔记
     * @param noteId 笔记ID

     * @return ResponseEntity indicating if the note is liked by the user
     */
    @GetMapping("/isLiked/{noteId}")
    public Result<Map<String, Object>> isNoteLikedByUser(HttpServletRequest request, @PathVariable Integer noteId) {
        try {
            Integer userId = getUserIdFromRequest(request);
            boolean isLiked = noteLikesService.isNoteLikedByUser(noteId, userId);
            Map<String, Object> map = Collections.singletonMap("isLiked", isLiked);
            return Result.success(map);
        } catch (BusinessException e) {
            return Result.fail(e.getStatusCode(), e.getMessage());
        }
    }
}

