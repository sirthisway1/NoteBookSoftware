package com.example.markdown_demo.controller;

import com.example.markdown_demo.common.dto.NotebookCreateDTO;
import com.example.markdown_demo.common.dto.NotebookDetailDTO;
import com.example.markdown_demo.common.dto.NotebookUpdateDTO;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.common.utils.JwtUtil;
import com.example.markdown_demo.service.NotebooksService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
        return Integer.parseInt(JwtUtil.validateToken(token));
    }

    @PostMapping("/createNotebooks")
    public Result<Map<String, Object>> createNotebook(HttpServletRequest request
            , @Valid  @RequestBody NotebookCreateDTO createNotebookDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError firstError = bindingResult.getFieldError();
            if (firstError != null) {
                return Result.fail(ResultType.INVALID_REQUEST_BODY.getCode(),firstError.getDefaultMessage());
            }
        }
        try {
            Integer userId = getUserIdFromRequest(request);
            notebooksService.createNotebook(createNotebookDTO, userId);
            return Result.success("笔记本创建成功");
        } catch (BusinessException e) {
            return Result.fail(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            return Result.fail(ResultType.INTERNAL_SERVER_ERROR);
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

    @GetMapping("/getAllId")
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
    public ResponseEntity<Map<String, Object>> updateNotebook(HttpServletRequest request, @PathVariable Integer notebookId, @RequestBody NotebookUpdateDTO updateDTO) {
        try {
            Integer userId = getUserIdFromRequest(request);
            boolean updated = notebooksService.updateNotebook(notebookId, updateDTO, userId);
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
    public ResponseEntity<Map<String, Object>> deleteNotebook(HttpServletRequest request, @PathVariable Integer notebookId) {
        try {
            Integer userId = getUserIdFromRequest(request);
            boolean deleted = notebooksService.deleteNotebook(notebookId    , userId);
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

