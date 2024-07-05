package com.example.markdown_demo.service.impl;

import com.example.markdown_demo.entity.Notes;
import com.example.markdown_demo.mapper.NotesMapper;
import com.example.markdown_demo.service.NotesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.markdown_demo.common.dto.*;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.ResultType;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotesServiceImpl extends ServiceImpl<NotesMapper, Notes> implements NotesService {

    @Autowired
    private NotesMapper notesMapper;

    @Override
    public void createNote(NoteCreateDTO createNoteDTO, Integer userId) {
        Notes note = new Notes();
        note.setUserId(userId);
        note.setTitle(createNoteDTO.getTitle());
        note.setContent(createNoteDTO.getContent());
        note.setNotebookId(createNoteDTO.getNotebookId());
        note.setTags(createNoteDTO.getTags());
        // 设置其他必要的字段

        save(note);

    }

    @Override
    public boolean updateNote(String noteId, NoteUpdateDTO updateNoteDTO, Integer userId) {
        Notes note = getById(noteId);
        if (note == null) {
            throw new BusinessException(ResultType.NOT_FOUND);
        }
        if (!note.getUserId().equals(userId)) {
            throw new BusinessException(ResultType.NO_PERMISSION);
        }

        note.setTitle(updateNoteDTO.getTitle());
        note.setContent(updateNoteDTO.getContent());
        note.setTags(updateNoteDTO.getTags());
        // 更新其他字段

        return updateById(note);
    }

    @Override
    public NoteDetailDTO getNoteDetail(String noteId, Integer userId) {
        Notes note = getById(noteId);
        if (note == null) {
            throw new BusinessException(ResultType.NOT_FOUND);
        }
        if (!note.getUserId().equals(userId)) {
            throw new BusinessException(ResultType.NO_PERMISSION);
        }
        NoteDetailDTO dto = new NoteDetailDTO();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setTags(note.getTags());
        dto.setIsPrivate(note.getIsPrivate());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());

        return dto;
    }

    @Override
    public boolean addTagsToNote(String noteId,String tags, Integer userId) {
        Notes note = getById(noteId);
        if (note == null) {
            throw new BusinessException(ResultType.NOT_FOUND);
        }
        if (!note.getUserId().equals(userId)) {
            throw new BusinessException(ResultType.NO_PERMISSION);
        }

        List<String> currentTags = note.getTags();
        currentTags.add(tags);
        note.setTags(currentTags);

        return updateById(note);
    }

    @Override
    public boolean removeTagsFromNote(String noteId, String tags, Integer userId) {
        Notes note = getById(noteId);
        if (note == null) {
            throw new BusinessException(ResultType.NOT_FOUND);
        }
        if (!note.getUserId().equals(userId)) {
            throw new BusinessException(ResultType.NO_PERMISSION);
        }

        List<String> currentTags = note.getTags();
        currentTags.remove(tags);
        note.setTags(currentTags);

        return updateById(note);
    }

    @Override
    public boolean setNotePrivacy(String noteId, boolean isPrivate, Integer userId) {
        Notes note = getById(noteId);
        if (note == null) {
            throw new BusinessException(ResultType.NOT_FOUND);
        }
        if (!note.getUserId().equals(userId)) {
            throw new BusinessException(ResultType.NO_PERMISSION);
        }

        note.setIsPrivate(isPrivate);
        updateById(note);

        return updateById(note);
    }

    @Override
    public List<Integer> searchNotesByKeyword(String keyword, Integer userId) {
        QueryWrapper<Notes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .and(w -> w.like("title", keyword).or().like("content", keyword))
                .select("id"); // 只选择 id 字段

        List<Notes> notes = list(queryWrapper);
        if (notes.isEmpty()) {
            throw new BusinessException(ResultType.NOT_FOUND);
        }

        List<Integer> noteIds = notes.stream()
                .map(Notes::getId)
                .collect(Collectors.toList());

        return noteIds;
    }

    @Override
    public List<Integer> searchNotesByTags(List<String> tags, Integer userId) {
        QueryWrapper<Notes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .select("id"); // 只选择 id 字段
        for (String tag : tags) {
            queryWrapper.apply("FIND_IN_SET({0}, tags)", tag);
        }

        List<Notes> notes = list(queryWrapper);
        if (notes.isEmpty()) {
            throw new BusinessException(ResultType.NOT_FOUND);
        }

        List<Integer> noteIds = notes.stream()
                .map(Notes::getId)
                .collect(Collectors.toList());


        return noteIds;
    }


}