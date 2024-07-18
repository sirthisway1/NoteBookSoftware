package com.example.markdown_demo.controller;

import com.example.markdown_demo.common.dto.*;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.common.utils.JwtUtil;
import com.example.markdown_demo.service.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/notes")
public class NotesController {

    @Autowired
    private NotesService notesService;

    private Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("token");
        return Integer.parseInt(JwtUtil.validateToken(token));
    }

    @PostMapping("/create")
    public Result<Void> createNote(@Valid @RequestBody NoteCreateDTO createNoteDTO, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            FieldError firstError = bindingResult.getFieldError();
            if (firstError != null) {
                return Result.fail(ResultType.INVALID_REQUEST_BODY.getCode(), firstError.getDefaultMessage());
            }
        }
        Integer userId = getUserIdFromRequest(request);
        notesService.createNote(createNoteDTO, userId);
        return Result.success("笔记创建成功");
    }

    @PutMapping("/update/{noteId}")
    public Result<Void> updateNote(@PathVariable Integer noteId, @Valid @RequestBody NoteUpdateDTO updateNoteDTO, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            FieldError firstError = bindingResult.getFieldError();
            if (firstError != null) {
                return Result.fail(ResultType.INVALID_REQUEST_BODY.getCode(), firstError.getDefaultMessage());
            }
        }
        Integer userId = getUserIdFromRequest(request);
        notesService.updateNote(noteId, updateNoteDTO, userId);
        return Result.success("笔记更新成功");
    }

    @GetMapping("/detail/{noteId}")
    public Result<NoteDetailDTO> getNoteDetail(@PathVariable Integer noteId, HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        NoteDetailDTO noteDetail = notesService.getNoteDetail(noteId, userId);
        return Result.success(noteDetail);
    }

    @DeleteMapping("/delete/{noteId}")
    public Result<?>noteDelete(@PathVariable Integer noteId, HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        try{
            notesService.deleteNote(noteId, userId);
            return Result.success("笔记删除成功");
        }catch (BusinessException e){
            return Result.fail(ResultType.INTERNAL_SERVER_ERROR.getCode(),"笔记删除失败");
        }
    }
    @GetMapping("/NoteShow")
    public Result<List<NoteShowDTO>> noteShowDTO( HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        List<NoteShowDTO>  noteShowDTO = notesService.noteShow(userId);
        return Result.success(noteShowDTO);
    }
    @GetMapping("/noteTopTags")
    public Result<?>noteCountTags(HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        List<String> maxTags=notesService.noteCountTags(userId);
        return Result.success(Map.of("topTags",maxTags));
    }
    @GetMapping("/NoteShowWithUser")
    public Result<List<NoteShowWithUserDTO>> noteShowWithUserDTO(HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        List<NoteShowWithUserDTO> noteShowWithUserDTO = notesService.noteShowWithUser();
        return Result.success(noteShowWithUserDTO);
    }



    @PostMapping("/addTags")
    public Result<Void> addTagsToNote(@RequestParam Integer noteId, @RequestParam String tags, HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        // 假设 tags 是以逗号分隔的字符串，这里需要分割并调用服务层方法
        String[] tagArray = tags.split(",");
        Result<Void> result = null;

        for (String tag : tagArray) {
            // 对每个标签调用服务层方法
            Result<Void> currentResult = notesService.addTagsToNote(noteId, tag.trim(), userId);
            // 如果当前标签添加失败，立即返回错误结果
            if (currentResult.getCode().equals(ResultType.INTERNAL_SERVER_ERROR.getCode())
                    || currentResult.getCode().equals(ResultType.NOT_FOUND.getCode())
                    || currentResult.getCode().equals(ResultType.NO_PERMISSION.getCode())
                    || currentResult.getCode().equals(ResultType. TAG_ALREADY_EXISTS.getCode())) { // 假设已存在标签的错误代码
                return currentResult; // 直接返回错误结果
            }
            // 如果是第一次成功添加标签，或者后续添加标签也成功，更新 result 变量
            if (result == null) {
                result = currentResult;
            }
        }

        // 如果所有标签都添加成功，返回最后一个成功的结果
        return result != null ? result : Result.fail(ResultType.INTERNAL_SERVER_ERROR);
    }


    @PostMapping("/removeTags")
    public Result<Void> removeTagsFromNote(@RequestParam Integer noteId, @RequestParam String tags, HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        boolean success = notesService.removeTagsFromNote(noteId, tags, userId);
        if (success) {
            return Result.success(null);
        } else {
            return Result.fail(ResultType.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{noteId}/privacy")
    public Result<Void> setNotePrivacy(@PathVariable Integer noteId,
                                       @RequestParam Boolean isPrivate,
                                       HttpServletRequest request) {
        if (isPrivate == null) {
            return Result.fail(ResultType.INVALID_REQUEST_BODY.getCode(), "Privacy setting is required");
        }
        Integer userId = getUserIdFromRequest(request);
        boolean success = notesService.setNotePrivacy(noteId, isPrivate, userId);
        if (success) {
            return Result.success(null);
        } else {
            return Result.fail(ResultType.INTERNAL_SERVER_ERROR.getCode(),ResultType.INTERNAL_SERVER_ERROR.getMessage());
        }
    }


    @GetMapping("/searchByKeyword")
    public Result<List<NoteShowDTO>> searchNotesByKeyword(@RequestParam String keyword, HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        List<NoteShowDTO> notes = notesService.searchNotesByKeyword(keyword, userId);
        return Result.success(notes);
    }

    @GetMapping("/searchByTags")
    public Result<List<NoteShowDTO>> searchNotesByTags(@RequestParam String tags, HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        List<NoteShowDTO> notes = notesService.searchNotesByTags(tags, userId);
        return Result.success(notes);
    }


    @GetMapping("/top-liked")
    public Result<List<NoteShowDTO>> getTopLikedNotes() {
        List<NoteShowDTO> topLikedNotes = notesService.getTopLikedNotes();
        return Result.success(topLikedNotes);  // 返回成功结果，并传入获取到的笔记列表
    }

    @GetMapping("/top-keywords")
    public Result<List<String>> getTopKeywordsFromNotesInLastWeek(HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        List<String> topKeywords = notesService.getTopKeywordsFromNotesInLastWeek(userId);
        return Result.success(topKeywords);
    }







}
