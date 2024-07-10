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
                return Result.fail(ResultType.INVALID_REQUEST_BODY.getCode(), firstError.getDefaultMessage());
            }
        }

        Integer userId = getUserIdFromRequest(request);
        notebooksService.createNotebook(createNotebookDTO, userId);
        return Result.success("笔记本创建成功");
    }

    @GetMapping("/{notebookId}")
    public Result<Map<String, Object>> getNotebookDetail(HttpServletRequest request, @PathVariable Integer notebookId) {

        Integer userId = getUserIdFromRequest(request);
        NotebookDetailDTO notebookDetail = notebooksService.getNotebookDetail(notebookId, userId);
        return Result.success(Map.of("notebookDetail", notebookDetail));
    }

    @GetMapping("/getAllId")
    public Result<Map<String, Object>> getAllNotebookIds(HttpServletRequest request) {

        Integer userId = getUserIdFromRequest(request);
        List<Integer> ids = notebooksService.getAllNotebookIds(userId);
        return Result.success(Map.of("ids", ids));
    }

    @PutMapping("/{notebookId}")
    public Result<Map<String, Object>> updateNotebook(HttpServletRequest request
            , @PathVariable Integer notebookId, @Valid @RequestBody NotebookUpdateDTO updateDTO,BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            FieldError firstError = bindingResult.getFieldError();
            if (firstError != null) {
                return Result.fail(ResultType.INVALID_REQUEST_BODY.getCode(),firstError.getDefaultMessage());
            }
        }
            Integer userId = getUserIdFromRequest(request);
            boolean updated = notebooksService.updateNotebook(notebookId, updateDTO, userId);
            if (updated) {
                return Result.success("笔记本更新成功");
            } else {
                return Result.fail(ResultType.INTERNAL_SERVER_ERROR.getCode(),"笔记本更新失败");
            }
    }

    @DeleteMapping("/{notebookId}")
    public Result<Map<String, Object>> deleteNotebook(HttpServletRequest request, @PathVariable Integer notebookId) {

        Integer userId = getUserIdFromRequest(request);
        boolean deleted = notebooksService.deleteNotebook(notebookId, userId);
        if (deleted) {
            return Result.success("笔记本删除成功");
        } else {
            return Result.fail(ResultType.NOT_FOUND.getCode(), "删除失败，笔记本不存在或已被删除");
        }
    }
}

