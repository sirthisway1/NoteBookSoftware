package com.example.markdown_demo.service;

import com.example.markdown_demo.entity.Notes;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.markdown_demo.common.dto.*;

import java.util.List;

public interface NotesService extends IService<Notes> {

    /**
     * 添加笔记
     * @param createNoteDTO 创建笔记的DTO
     * @param userId 用户ID
     * @return 创建的笔记ID
     */
    void createNote(NoteCreateDTO createNoteDTO, Integer userId);

    /**
     * 修改笔记
     * @param noteId 笔记ID
     * @param updateNoteDTO 更新笔记的DTO
     * @param userId 用户ID
     * @return 是否修改成功
     */
    void updateNote(String noteId, NoteUpdateDTO updateNoteDTO, Integer userId);

    /**
     * 查看笔记
     * @param noteId 笔记ID
     * @param userId 用户ID
     * @return 笔记详情
     */
    NoteDetailDTO getNoteDetail(String noteId, Integer userId);



    List<NoteShowDTO> noteShow(Integer userId);
    List<NoteShowWithUserDTO> noteShowWithUser();
    /**
     * 向笔记添加标签
     * @param noteId 笔记ID
     * @param tags 要添加的标签列表
     * @param userId 用户ID
     * @return 是否添加成功
     */
    boolean addTagsToNote(Integer noteId, String tags, Integer userId);

    /**
     * 删除笔记的标签
     * @param noteId 笔记ID
     * @param tags 要删除的标签列表
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean removeTagsFromNote(String noteId, String tags, Integer userId);


    /**
     * 设置笔记私密属性
     * @param noteId 笔记ID
     * @param isPrivate 是否私密
     * @param userId 用户ID
     * @return 更新后的笔记详情
     */
    boolean setNotePrivacy(String noteId, boolean isPrivate, Integer userId);
    // ... 其他方法保持不变 ...

    /**
     * 关键字搜索笔记
     * @param keyword 搜索关键字
     * @param userId 用户ID
     * @return 匹配的笔记ID列表
     */
    List<NoteShowDTO> searchNotesByKeyword(String keyword, Integer userId);

    /**
     * 根据标签搜索笔记
     * @param tags 标签列表
     * @param userId 用户ID
     * @return 匹配的笔记ID列表
     */
    List<NoteShowDTO> searchNotesByTags(String tags, Integer userId);
}