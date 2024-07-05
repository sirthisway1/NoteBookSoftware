package com.example.markdown_demo.service;

import com.example.markdown_demo.common.dto.NotebookCreateDTO;
import com.example.markdown_demo.common.dto.NotebookDetailDTO;
import com.example.markdown_demo.common.dto.NotebookUpdateDTO;
import com.example.markdown_demo.entity.Notebooks;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xmg
 * @since 2024-07-04
 */
public interface NotebooksService extends IService<Notebooks> {

    /**
     * 创建笔记本
     *
     * @param createNotebookDTO 创建笔记本的DTO
     * @param userId            用户ID
     */
    void createNotebook(NotebookCreateDTO createNotebookDTO, Integer userId);

    /**
     * 获取用户所有笔记本ID
     * @param userId 用户ID
     * @return 笔记本ID列表
     */
    List<Integer> getAllNotebookIds(Integer userId);

    /**
     * 获取特定笔记本详细信息
     * @param Id 笔记本ID
     * @param userId 用户ID
     * @return 笔记本详细信息
     */
    NotebookDetailDTO getNotebookDetail(Integer Id, Integer userId);

    /**
     * 修改笔记本
     * @param Id 笔记本ID
     * @param updateNotebookDTO 更新笔记本的DTO
     * @param userId 用户ID
     * @return 是否修改成功
     */
    boolean updateNotebook(Integer Id, NotebookUpdateDTO updateNotebookDTO, Integer userId);

    /**
     * 删除笔记本
     * @param Id 笔记本ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteNotebook(Integer Id, Integer userId);
}