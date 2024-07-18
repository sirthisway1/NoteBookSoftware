package com.example.markdown_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.entity.NoteLikes;
import com.example.markdown_demo.entity.Notes;
import com.example.markdown_demo.entity.ThoughtNotes;
import com.example.markdown_demo.entity.Users;
import com.example.markdown_demo.mapper.NoteLikesMapper;
import com.example.markdown_demo.mapper.NotesMapper;
import com.example.markdown_demo.mapper.ThoughtNotesMapper;
import com.example.markdown_demo.mapper.UsersMapper;
import com.example.markdown_demo.service.KeyWordService;
import com.example.markdown_demo.service.NotesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.markdown_demo.common.dto.*;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.ResultType;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.io.IOException;

import java.util.function.IntFunction;
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

    // 注入ThoughtNotesService


    private final KeyWordService keyWordService;

    @Autowired
    public NotesServiceImpl(KeyWordService keyWordService) {
        this.keyWordService = keyWordService;
    }

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
        } else dto.setType(0);
        return dto;
    }


    @Override
    public List<Object>  noteCountTags(Integer userId){
        LocalDateTime oneWeekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);

        // 查询条件
        LambdaQueryWrapper<Notes> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notes::getUserId, userId);
        queryWrapper.ge(Notes::getUpdatedAt, oneWeekAgo);

        // 查询笔记列表
        List<Notes> notesList = notesMapper.selectList(queryWrapper);

        // 统计标签出现的次数
        Map<String, Integer> tagCounts = new HashMap<>();
        for (Notes note : notesList) {
            List<String> tags = note.getTags(); // 获取标签列表
            for (String tag : tags) {
                tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);
            }
        }

        // 对标签出现次数进行排序并获取前五个标签及其次数
        List<Map.Entry<String, Integer>> sortedTagCounts = tagCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        // 构造标签和次数的数组
        List<String> tags = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedTagCounts) {
            tags.add(entry.getKey());
            counts.add(entry.getValue());
        }

        return Arrays.asList(tags, counts);
    }

    @Override  public List<Integer> noteFetchActivity(Integer userId){
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(6); // 7天前，包括今天
        LocalDate today = LocalDate.now().plusDays(1);

        // 创建一个 Map 来存储每天更新的笔记数量，初始值为 0
        Map<LocalDate, Integer> dailyActivityMap = new HashMap<>();
        for (LocalDate date = sevenDaysAgo; date.isBefore(today); date = date.plusDays(1)) {
            dailyActivityMap.put(date, 0);
        }

        // 查询条件
        LambdaQueryWrapper<Notes> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notes::getUserId, userId);
        queryWrapper.between(Notes::getUpdatedAt, sevenDaysAgo, today); // 查询最近7天内更新的笔记

        // 查询笔记列表
        List<Notes> notesList = this.list(queryWrapper);

        // 定义日期格式，与数据库中存储的格式匹配
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 统计每天更新的笔记数量
        for (Notes note : notesList) {
            // 将字符串转换回 LocalDateTime
            LocalDateTime updatedAt = LocalDateTime.parse(note.getUpdatedAt(), formatter);
            LocalDate updateDate = updatedAt.toLocalDate();
            if (dailyActivityMap.containsKey(updateDate)) {
                dailyActivityMap.put(updateDate, dailyActivityMap.get(updateDate) + 1);
            }
        }

        // 将 Map 转换为 List，按照日期顺序排列
        List<Integer> dailyActivityList = dailyActivityMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        return dailyActivityList;
    }

    @Override
    public void deleteNote(Integer noteId, Integer userId) throws BusinessException {
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

    @Override
    public List<KeywordFrequencyDTO> getTopKeywordsFromNotesInLastWeek(Integer userId) {
        // 获取一周内的笔记，过滤出指定用户ID的笔记
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        QueryWrapper<Notes> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Notes::getUserId, userId) // 添加用户ID过滤条件
                .ge(Notes::getCreatedAt, oneWeekAgo);
        List<Notes> notes = this.list(queryWrapper);

        // 初始化一个Map来存储所有关键词的频率
        Map<String, Long> keywordFrequencyMap = new HashMap<>();

        // 遍历每个笔记，提取关键词并更新频率
        for (Notes note : notes) {
            String content = note.getContent();
            // 检查content是否为null或空字符串
            if (content == null || content.trim().isEmpty()) {
                continue; // 如果为null或空，则跳过当前笔记
            }

            // 检查该笔记是否为思维笔记
            if (isThoughtNote(note.getId())) {
                continue; // 如果是思维笔记，则跳过当前笔记
            }

            List<String> keywords = null; // 提取关键词
            try {
                keywords = keyWordService.extractKeyWords(content, 10);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(keywords==null){
                continue;
            }
            // 更新关键词频率
            for (String keyword : keywords) {
                keywordFrequencyMap.put(keyword, keywordFrequencyMap.getOrDefault(keyword, 0L) + 1);
            }
        }

        // 找出频率最高的前五个关键词，并创建KeywordFrequencyDTO对象列表
        List<KeywordFrequencyDTO> topKeywordsWithFrequency = keywordFrequencyMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> new KeywordFrequencyDTO(entry.getValue(),entry.getKey()))
                .collect(Collectors.toList());

        return topKeywordsWithFrequency;

    }


    // 实现isThoughtNote方法
    private boolean isThoughtNote(Integer noteId) {
        // 调用ThoughtNotesService的方法来检查是否存在对应的思维笔记
        ThoughtNotes thoughtNote = getThoughtNoteByNoteId(noteId);
        // 如果思维笔记存在，并且类型不是0（假设0代表不是思维笔记），则返回true
        return thoughtNote != null && thoughtNote.getType() != null && thoughtNote.getType() != 0;
    }





    public ThoughtNotes getThoughtNoteByNoteId(Integer noteId) {
            // 使用Mapper查询思维笔记
            QueryWrapper<ThoughtNotes> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("note_id", noteId);
            return thoughtNotesMapper.selectOne(queryWrapper);

    }


}



