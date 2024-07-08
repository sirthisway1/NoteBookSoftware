package com.example.markdown_demo.controller;

import com.example.markdown_demo.common.dto.*;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.common.utils.JwtUtil;
import com.example.markdown_demo.service.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NotesController {

    @Autowired
    private NotesService notesService;

    private Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            throw new BusinessException(ResultType.UNAUTHORIZED);
        }
        return Integer.parseInt(JwtUtil.validateToken(token));
    }

    @PostMapping("/create")
    public Result<Void> createNote(@Valid @RequestBody NoteCreateDTO createNoteDTO, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.fail(ResultType.INVALID_REQUEST_BODY);
        }
        Integer userId = getUserIdFromRequest(request);
        notesService.createNote(createNoteDTO, userId);
        return Result.success(null);
    }

    @PutMapping("/update/{noteId}")
    public Result<Void> updateNote(@PathVariable String noteId, @Valid @RequestBody NoteUpdateDTO updateNoteDTO, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.fail(ResultType.INVALID_REQUEST_BODY);
        }
        Integer userId = getUserIdFromRequest(request);
        boolean success = notesService.updateNote(noteId, updateNoteDTO, userId);
        if (success) {
            return Result.success(null);
        } else {
            return Result.fail(ResultType.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/detail/{noteId}")
    public Result<NoteDetailDTO> getNoteDetail(@PathVariable String noteId, HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        NoteDetailDTO noteDetail = notesService.getNoteDetail(noteId, userId);
        return Result.success(noteDetail);
    }

    @GetMapping("/NoteShow")
    public Result<List<NoteShowDTO>> noteShowDTO( HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        List<NoteShowDTO>  noteShowDTO = notesService.noteShow(userId);
        return Result.success(noteShowDTO);
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
        boolean success = notesService.addTagsToNote(noteId, tags, userId);
        if (success) {
            return Result.success(null);
        } else {
            return Result.fail(ResultType.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/removeTags")
    public Result<Void> removeTagsFromNote(@RequestParam String noteId, @RequestParam String tags, HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        boolean success = notesService.removeTagsFromNote(noteId, tags, userId);
        if (success) {
            return Result.success(null);
        } else {
            return Result.fail(ResultType.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{noteId}/privacy")
    public Result<Void> setNotePrivacy(@PathVariable String noteId,
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
    public Result<List<Integer>> searchNotesByKeyword(@RequestParam String keyword, HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        List<Integer> noteIds = notesService.searchNotesByKeyword(keyword, userId);
        return Result.success(noteIds);
    }

    @GetMapping("/searchByTags")
    public Result<List<Integer>> searchNotesByTags(@RequestParam String tags, HttpServletRequest request) {
        Integer userId = getUserIdFromRequest(request);
        List<Integer> noteIds = notesService.searchNotesByTags(tags, userId);
        return Result.success(noteIds);
    }
}
