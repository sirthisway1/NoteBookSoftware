package com.example.markdown_demo.service.impl;

import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.entity.NoteLikes;
import com.example.markdown_demo.entity.Notes;
import com.example.markdown_demo.entity.ThoughtNotes;
import com.example.markdown_demo.entity.Users;
import com.example.markdown_demo.mapper.NoteLikesMapper;
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
    private NoteLikesMapper likesMapper;
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
        // 获取用户信息来添加头像 URL
         Users user = usersMapper.selectById(note.getUserId());
         if (user == null) {
        throw new BusinessException(ResultType.NOT_FOUND);
        }
        NoteDetailDTO dto = new NoteDetailDTO();
        dto.setNoteId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setTags(note.getTags());
        dto.setIsPrivate(note.getIsPrivate());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        dto.setAvatar(user.getAvatar()); // 设置用户的头像 URL
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
    public void deleteNote(Integer noteId, Integer userId)throws BusinessException{
        Notes note = getById(noteId);
        if (note == null) {
            throw new BusinessException(ResultType.NOT_FOUND);
        }
        if (!note.getUserId().equals(userId)) {
            throw new BusinessException(ResultType.NO_PERMISSION);
        }
        try {
            notesMapper.deleteById(noteId);
        } catch (Exception e) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
        }
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
        dto.setIsPrivate(note.getIsPrivate());
        dto.setTags(note.getTags());
        return dto;
    }

    @Override
    public List<NoteShowWithUserDTO> noteShowWithUser() {
        Set<Integer> noteIdsInThoughtNotes = thoughtNotesMapper.selectList(new QueryWrapper<ThoughtNotes>()
                        .select("note_id")) // 仅选择 note_id 列
                .stream()
                .map(ThoughtNotes::getNoteId)
                .collect(Collectors.toSet());

        QueryWrapper<Notes> notesQueryWrapper = new QueryWrapper<>();
        notesQueryWrapper.eq("is_private", false);
        if (!noteIdsInThoughtNotes.isEmpty()) {
            notesQueryWrapper.notIn("id", noteIdsInThoughtNotes); // 排除 ThoughtNotes 中的 noteId
        }
        List<Notes> notesList = notesMapper.selectList(notesQueryWrapper);

        // 映射 Notes 到 NoteShowWithUserDTO
        List<NoteShowWithUserDTO> result = notesList.stream()
                .map(this::mapToNoteShowWithUserDTO)
                .collect(Collectors.toList());
        return result;
    }


    private NoteShowWithUserDTO mapToNoteShowWithUserDTO(Notes note) {
        NoteShowWithUserDTO dto = new NoteShowWithUserDTO();
        dto.setNoteId(note.getId());

        Users user = usersMapper.selectById(note.getUserId()); // 通过 ID 检索用户
        // 确保用户实体不为空
        if (user != null) {
            dto.setUsername(user.getUsername()); // 设置来自用户实体的用户名
            dto.setTitle(note.getTitle());
            dto.setContent(note.getContent());
            dto.setUpdatedAt(note.getUpdatedAt());
            dto.setAvatar(user.getAvatar()); // 设置用户的头像 URL
        }
        return dto;
    }


    // 修改 Notes 类的 addTagsToNote 方法
    @Override
    public Result<Void> addTagsToNote(Integer noteId, String tags, Integer userId) {
        Notes note = getById(noteId);
        if (note == null) {
            return Result.fail(ResultType.NOT_FOUND);
        }
        if (!note.getUserId().equals(userId)) {
            return Result.fail(ResultType.NO_PERMISSION);
        }
        List<String> currentTags = note.getTags() != null ? new ArrayList<>(note.getTags()) : new ArrayList<>();
        if (currentTags.contains(tags)) {
            // 如果标签已存在，返回错误信息
            return Result.fail(ResultType.INTERNAL_SERVER_ERROR);
        }
        currentTags.add(tags);
        note.setTags(currentTags);
        boolean updated = updateById(note);
        if (updated) {
            return Result.success("标签添加成功");
        } else {
            return Result.fail(ResultType.INTERNAL_SERVER_ERROR);
        }
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


    @Override
    public List<NoteShowDTO> getTopLikedNotes() {
        // 查询点赞数最高的三个笔记的ID
        List<Integer> topLikedNoteIds = likesMapper.selectMaps(new QueryWrapper<NoteLikes>()
                        .select("note_id", "count(user_id) as like_count")
                        .groupBy("note_id")
                        .orderByDesc("like_count")
                        .last("LIMIT 3"))
                .stream()
                .map(map -> (Integer) map.get("note_id"))
                .collect(Collectors.toList());

        if (topLikedNoteIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询详细信息
        return getNoteDetailsWithLikes(topLikedNoteIds);
    }

    private List<NoteShowDTO> getNoteDetailsWithLikes(List<Integer> noteIds) {
        List<Notes> notes = notesMapper.selectBatchIds(noteIds);
        return notes.stream().map(note -> {
            NoteShowDTO dto = new NoteShowDTO();
            dto.setNoteId(note.getId());
            dto.setTitle(note.getTitle());
            dto.setCreatedAt(note.getCreatedAt().toString());
            dto.setUpdatedAt(note.getUpdatedAt().toString());
            dto.setIsPrivate(note.getIsPrivate());
            dto.setTags(note.getTags());
            // 查询并设置点赞数
            Integer likeCount = Math.toIntExact(likesMapper.selectCount(new QueryWrapper<NoteLikes>().eq("note_id", note.getId())));
            dto.setLikeCount(likeCount);
            return dto;
        }).collect(Collectors.toList());
    }
}


