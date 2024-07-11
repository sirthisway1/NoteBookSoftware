package com.example.markdown_demo.service.impl;

import com.example.markdown_demo.entity.Notes;
import com.example.markdown_demo.entity.ThoughtNotes;
import com.example.markdown_demo.entity.Users;
import com.example.markdown_demo.mapper.NotesMapper;
import com.example.markdown_demo.mapper.ThoughtNotesMapper;
import com.example.markdown_demo.mapper.UsersMapper;
import com.example.markdown_demo.service.NotesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.markdown_demo.common.dto.*;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.ResultType;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotesServiceImpl extends ServiceImpl<NotesMapper, Notes> implements NotesService {

    @Autowired
    private NotesMapper notesMapper;
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private ThoughtNotesMapper thoughtNotesMapper;

    @Override
    public void createNote(NoteCreateDTO createNoteDTO, Integer userId) {
        Notes note = new Notes();
        note.setUserId(userId);
        note.setTitle(createNoteDTO.getTitle());
        note.setContent(createNoteDTO.getContent());
        note.setNotebookId(createNoteDTO.getNotebookId());
        note.setTags(createNoteDTO.getTags()); // 将标签列表转换为逗号分隔的字符串
        note.setIsPrivate(true); // 默认设置为私密笔记

        // 保存笔记
        this.save(note);

        // 如果 type 大于 0，向 m_thought_notes 表中插入一条数据
        if (createNoteDTO.getType() > 0) {
            ThoughtNotes thoughtNote = new ThoughtNotes();
            thoughtNote.setNoteId(note.getId());
            thoughtNote.setType(createNoteDTO.getType());
            thoughtNotesMapper.insert(thoughtNote);
        }

    }

    @Override
    public void updateNote(String noteId, NoteUpdateDTO updateNoteDTO, Integer userId) {
        Notes note = getById(noteId);
        if (note == null) {
            throw new BusinessException(ResultType.NOT_FOUND);
        }
        if (!note.getUserId().equals(userId)) {
            throw new BusinessException(ResultType.NO_PERMISSION);
        }

        note.setTitle(updateNoteDTO.getTitle());
        note.setContent(updateNoteDTO.getContent());
        // 更新其他字段

        updateById(note);
    }

    @Override
    public NoteDetailDTO getNoteDetail(String noteId, Integer userId) {
        Notes note = getById(noteId);
        if (note == null) {
            throw new BusinessException(ResultType.NOT_FOUND);
        }
        // 修改权限判断逻辑
        if (!note.getUserId().equals(userId) && note.getIsPrivate()) {
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

        // 查询思维笔记类型
        QueryWrapper<ThoughtNotes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note_id", noteId);
        ThoughtNotes thoughtNote = thoughtNotesMapper.selectOne(queryWrapper);
        if (thoughtNote != null) {
            dto.setType(thoughtNote.getType());
        }
        else dto.setType(0);
        return dto;
    }
    @Override
    public List<NoteShowDTO> noteShow(Integer userId) {
        QueryWrapper<Notes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Notes> notesList = notesMapper.selectList(queryWrapper);
        return notesList.stream().map(this::mapToNoteShowDTO).collect(Collectors.toList());
    }

    private NoteShowDTO mapToNoteShowDTO(Notes note) {
        NoteShowDTO dto = new NoteShowDTO();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setUpdatedAt(note.getUpdatedAt());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setTags(note.getTags());
        return dto;
    }

    @Override
    public List<NoteShowWithUserDTO> noteShowWithUser() {
        QueryWrapper<Notes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_private", false); // Assuming 'false' means the note is not private
        List<Notes> notesList = notesMapper.selectList(queryWrapper);
        return notesList.stream().map(this::mapToNoteShowWithUserDTO).collect(Collectors.toList());
    }

    private NoteShowWithUserDTO mapToNoteShowWithUserDTO(Notes note) {
        NoteShowWithUserDTO dto = new NoteShowWithUserDTO();
        dto.setId(note.getId());
        Users user = usersMapper.selectById(note.getUserId()); // Retrieve the user by ID
        dto.setUsername(user.getUsername()); // Set the username from the user entity
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        return dto;
    }

    @Override
    public boolean addTagsToNote(Integer noteId,String tags, Integer userId) {
        Notes note = getById(noteId);
        if (note == null) {
            throw new BusinessException(ResultType.NOT_FOUND);
        }
        if (!note.getUserId().equals(userId)) {
            throw new BusinessException(ResultType.NO_PERMISSION);
        }

        // Ensure the current tags list is modifiable
        List<String> currentTags = note.getTags();
        if (currentTags == null) {
            currentTags = new ArrayList<>();
        } else {
            currentTags = new ArrayList<>(currentTags);  // Make a modifiable copy
        }

        // Add the single tag to the current tags list
        if (!currentTags.contains(tags)) { // Check if the tag already exists
            currentTags.add(tags);
        }

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

        // Ensure the current tags list is modifiable
        List<String> currentTags = note.getTags();
        if (currentTags == null) {
            currentTags = new ArrayList<>();
        } else {
            currentTags = new ArrayList<>(currentTags);  // Make a modifiable copy
        }

        // Remove the single tag from the current tags list
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
    public List<Integer> searchNotesByTags(String tags, Integer userId) {
        QueryWrapper<Notes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .apply("FIND_IN_SET({0}, tags)", tags)
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


}