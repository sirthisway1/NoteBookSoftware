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
import java.util.Set;
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
    public void updateNote(Integer noteId, NoteUpdateDTO updateNoteDTO, Integer userId) {
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
    public NoteDetailDTO getNoteDetail(Integer noteId, Integer userId) {
        Notes note = getById(noteId);
        if (note == null) {
            throw new BusinessException(ResultType.NOT_FOUND);
        }
        // 修改权限判断逻辑
        if (!note.getUserId().equals(userId) && note.getIsPrivate()) {
            throw new BusinessException(ResultType.NO_PERMISSION);
        }

        NoteDetailDTO dto = new NoteDetailDTO();
        dto.setNoteId(note.getId());
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
        dto.setNoteId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setUpdatedAt(note.getUpdatedAt());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setTags(note.getTags());
        return dto;
    }

    @Override
    public List<NoteShowWithUserDTO> noteShowWithUser() {
        Set<Integer> noteIdsInThoughtNotes = thoughtNotesMapper.selectList(new QueryWrapper<ThoughtNotes>()
                        .select("note_id")) // Only select the note_id column
                .stream()
                .map(ThoughtNotes::getNoteId)
                .collect(Collectors.toSet());

        // Step 2: Query Notes table for non-private notes and exclude those with noteId in the set if the set is not empty
        QueryWrapper<Notes> notesQueryWrapper = new QueryWrapper<>();
        notesQueryWrapper.eq("is_private", false);
        if (!noteIdsInThoughtNotes.isEmpty()) {
            notesQueryWrapper.notIn("id", noteIdsInThoughtNotes); // Exclude noteIds that are in ThoughtNotes only if the set is not empty
        }
        List<Notes> notesList = notesMapper.selectList(notesQueryWrapper);

        // Map Notes to NoteShowWithUserDTO
        List<NoteShowWithUserDTO> result = notesList.stream()
                .map(this::mapToNoteShowWithUserDTO)
                .collect(Collectors.toList());

        return result;
    }

    private NoteShowWithUserDTO mapToNoteShowWithUserDTO(Notes note) {
        NoteShowWithUserDTO dto = new NoteShowWithUserDTO();
        dto.setNoteId(note.getId());
        Users user = usersMapper.selectById(note.getUserId()); // Retrieve the user by ID
        dto.setUsername(user.getUsername()); // Set the username from the user entity
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setUpdatedAt(note.getUpdatedAt());
        return dto;
    }

    @Override
    public boolean addTagsToNote(Integer noteId, String tag, Integer userId) {
        Notes note = getById(noteId);
        if (note == null) {
            throw new BusinessException(ResultType.NOT_FOUND);
        }
        if (!note.getUserId().equals(userId)) {
            throw new BusinessException(ResultType.NO_PERMISSION);
        }

        // 确保当前标签列表是可修改的
        List<String> currentTags = note.getTags();
        if (currentTags == null) {
            currentTags = new ArrayList<>();
        } else {
            currentTags = new ArrayList<>(currentTags);  // 创建一个可修改的副本
        }

        // 添加单个标签到当前标签列表
        if (!currentTags.contains(tag)) { // 检查标签是否已存在
            currentTags.add(tag);
        }

        note.setTags(currentTags);
        return updateById(note);
    }

    @Override
    public boolean removeTagsFromNote(Integer noteId, String tag, Integer userId) {
        Notes note = getById(noteId);
        if (note == null) {
            throw new BusinessException(ResultType.NOT_FOUND);
        }
        if (!note.getUserId().equals(userId)) {
            throw new BusinessException(ResultType.NO_PERMISSION);
        }

        // 确保当前标签列表是可修改的
        List<String> currentTags = note.getTags();
        if (currentTags == null) {
            currentTags = new ArrayList<>();
        } else {
            currentTags = new ArrayList<>(currentTags);  // 创建一个可修改的副本
        }

        // 从当前标签列表中移除单个标签
        currentTags.remove(tag);

        note.setTags(currentTags);

        return updateById(note);
    }

    @Override
    public boolean setNotePrivacy(Integer noteId, boolean isPrivate, Integer userId) {
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
    public List<NoteShowDTO> searchNotesByKeyword(String keyword, Integer userId) {


        QueryWrapper<Notes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .and(w -> w.like("title", keyword).or().like("content", keyword));
        List<Notes> notesList = notesMapper.selectList(queryWrapper);

        if (notesList.isEmpty()) {
            throw new BusinessException(ResultType.NOT_FOUND);
        }
        return notesList.stream().map(this::mapToNoteShowDTO).collect(Collectors.toList());
    }

    @Override
    public List<NoteShowDTO> searchNotesByTags(String tags, Integer userId) {
        QueryWrapper<Notes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .apply("FIND_IN_SET({0}, tags)", tags);

        List<Notes> notesList = notesMapper.selectList(queryWrapper);
        if (notesList.isEmpty()) {
            throw new BusinessException(ResultType.NOT_FOUND);
        }
        return notesList.stream().map(this::mapToNoteShowDTO).collect(Collectors.toList());
    }


}