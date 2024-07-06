package com.example.markdown_demo.controller;

import com.example.markdown_demo.common.dto.NotebookCreateDTO;
import com.example.markdown_demo.common.dto.NotebookDetailDTO;
import com.example.markdown_demo.common.dto.NotebookUpdateDTO;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.common.utils.JwtUtil;
import com.example.markdown_demo.service.NotebooksService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/notebooks")
public class NotebooksController {

    @Autowired
    private NotebooksService notebooksService;

    private Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            throw new BusinessException(ResultType.UNAUTHORIZED);
        }
        return Integer.parseInt(JwtUtil.validateToken(token));
    }

    @PostMapping("/createNotebooks")
    public ResponseEntity<Map<String, Object>> createNotebook(HttpServletRequest request, @RequestBody NotebookCreateDTO createNotebookDTO) {
        try {
            Integer userId = getUserIdFromRequest(request);
            notebooksService.createNotebook(createNotebookDTO, userId);
            return ResponseEntity.ok().body(ResultType.SUCCESS.asMap("message", "笔记本创建成功"));
        } catch (BusinessException e) {
            return ResponseEntity.status(Integer.parseInt(e.getStatusCode()))
                    .body(ResultType.fromCode(e.getStatusCode()).asMap("message", e.getMessage()));
        }
    }

    @GetMapping("/{notebookId}")
    public ResponseEntity<Map<String, Object>> getNotebookDetail(HttpServletRequest request, @PathVariable Integer notebookId) {
        try {
            Integer userId = getUserIdFromRequest(request);
            NotebookDetailDTO notebookDetail = notebooksService.getNotebookDetail(notebookId, userId);
            return ResponseEntity.ok().body(ResultType.SUCCESS.asMap("notebookDetail", notebookDetail));
        } catch (BusinessException e) {
            return ResponseEntity.status(Integer.parseInt(e.getStatusCode()))
                    .body(ResultType.fromCode(e.getStatusCode()).asMap("message", e.getMessage()));
        }
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getAllNotebookIds(HttpServletRequest request) {
        try {
            Integer userId = getUserIdFromRequest(request);
            List<Integer> ids = notebooksService.getAllNotebookIds(userId);
            return ResponseEntity.ok().body(ResultType.SUCCESS.asMap("ids", ids));
        } catch (BusinessException e) {
            return ResponseEntity.status(Integer.parseInt(e.getStatusCode()))
                    .body(ResultType.fromCode(e.getStatusCode()).asMap("message", e.getMessage()));
        }
    }

    @PutMapping("/{notebookId}")
    public ResponseEntity<Map<String, Object>> updateNotebook(HttpServletRequest request, @PathVariable Integer id, @RequestBody NotebookUpdateDTO updateDTO) {
        try {
            Integer userId = getUserIdFromRequest(request);
            boolean updated = notebooksService.updateNotebook(id, updateDTO, userId);
            if (updated) {
                return ResponseEntity.ok().body(ResultType.SUCCESS.asMap("message", "笔记本更新成功"));
            } else {
                return ResponseEntity.badRequest().body(ResultType.INTERNAL_SERVER_ERROR.asMap("message", "更新失败"));
            }
        } catch (BusinessException e) {
            return ResponseEntity.status(Integer.parseInt(e.getStatusCode()))
                    .body(ResultType.fromCode(e.getStatusCode()).asMap("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{notebookId}")
    public ResponseEntity<Map<String, Object>> deleteNotebook(HttpServletRequest request, @PathVariable Integer id) {
        try {
            Integer userId = getUserIdFromRequest(request);
            boolean deleted = notebooksService.deleteNotebook(id, userId);
            if (deleted) {
                return ResponseEntity.ok().body(ResultType.SUCCESS.asMap("message", "笔记本删除成功"));
            } else {
                return ResponseEntity.badRequest().body(ResultType.NOT_FOUND.asMap("message", "删除失败，笔记本不存在或已被删除"));
            }
        } catch (BusinessException e) {
            return ResponseEntity.status(Integer.parseInt(e.getStatusCode()))
                    .body(ResultType.fromCode(e.getStatusCode()).asMap("message", e.getMessage()));
        }
    }
}

