package com.example.markdown_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.markdown_demo.common.dto.NotebookCreateDTO;
import com.example.markdown_demo.common.dto.NotebookDetailDTO;
import com.example.markdown_demo.common.dto.NotebookUpdateDTO;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.entity.Notebooks;
import com.example.markdown_demo.entity.Notes;
import com.example.markdown_demo.mapper.NotebooksMapper;
import com.example.markdown_demo.mapper.NotesMapper;
import com.example.markdown_demo.service.NotebooksService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xmg
 * @since 2024-07-04
 */
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.common.lang.BusinessException;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class NotebooksServiceImpl extends ServiceImpl<NotebooksMapper, Notebooks> implements NotebooksService {
    @Autowired
    private NotesMapper notesMapper;

    @Autowired
    private  NotebooksMapper notebooksMapper;



    @Override
    public void createNotebook(NotebookCreateDTO createNotebookDTO, Integer userId) {
        validateNotebookData(createNotebookDTO); // 提取数据验证逻辑到单独的方法
        Notebooks notebook = new Notebooks();
        notebook.setName(createNotebookDTO.getName());
        notebook.setSummary(createNotebookDTO.getSummary());
        notebook.setUserId(userId);
        if (!this.save(notebook)) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR, "无法保存笔记本");
        }
    }

    private void validateNotebookData(NotebookCreateDTO createNotebookDTO) {
        if (createNotebookDTO.getName() == null || createNotebookDTO.getName().trim().isEmpty() || createNotebookDTO.getName().length() > 100) {
            throw new BusinessException(ResultType.INVALID_REQUEST_BODY, "笔记本名称不合法");
        }
        if (createNotebookDTO.getSummary() != null && createNotebookDTO.getSummary().length() > 500) {
            throw new BusinessException(ResultType.INVALID_REQUEST_BODY, "描述不能超过500字符");
        }
    }

    @Override
    public List<Integer> getAllNotebookIds(Integer userId) {
        if (userId == null) {
            throw new BusinessException(ResultType.INVALID_REQUEST_BODY, "用户ID不能为空");
        }
        List<Notebooks> notebooks = this.list(new QueryWrapper<Notebooks>().eq("user_id", userId));
        return notebooks.stream().map(Notebooks::getId).collect(Collectors.toList());
    }

    @Override
    public NotebookDetailDTO getNotebookDetail(Integer notebookId, Integer userId) {
        Notebooks notebook = validateNotebookAccess(notebookId, userId);
        List<Integer> noteIds = notesMapper.selectList(new QueryWrapper<Notes>().select("id").eq("notebook_id", notebookId))
                .stream().map(Notes::getId).collect(Collectors.toList());
        return mapToNotebookDetailDTO(notebook, noteIds);
    }

    private Notebooks validateNotebookAccess(Integer notebookId, Integer userId) {
        if (notebookId == null) {
            throw new BusinessException(ResultType.INVALID_REQUEST_BODY, "笔记本ID不能为空");
        }
        Notebooks notebook = notebooksMapper.selectOne(new QueryWrapper<Notebooks>().eq("id", notebookId).eq("user_id", userId));
        if (notebook == null) {
            throw new BusinessException(ResultType.NOT_FOUND, "找不到对应的笔记本或无权访问");
        }
        return notebook;
    }

    private NotebookDetailDTO mapToNotebookDetailDTO(Notebooks notebook, List<Integer> noteIds) {
        NotebookDetailDTO detailDTO = new NotebookDetailDTO();
        detailDTO.setNotebookId(notebook.getId());
        detailDTO.setName(notebook.getName());
        detailDTO.setLastModified(notebook.getUpdatedAt());
        detailDTO.setSummary(notebook.getSummary());
        detailDTO.setNoteId(noteIds);
        return detailDTO;
    }

    @Override
    public boolean updateNotebook(Integer Id, NotebookUpdateDTO NotebookUpdateDTO, Integer userId) {
        if (Id == null || userId == null) {
            throw new BusinessException(ResultType.INVALID_REQUEST_BODY, "笔记本ID和用户ID不能为空");
        }
        Notebooks updateNote = new Notebooks();
        updateNote.setName(NotebookUpdateDTO.getName());
        updateNote.setSummary(NotebookUpdateDTO.getSummary());
        updateNote.setUpdatedAt(LocalDateTime.now());
        int result = notebooksMapper.update(updateNote, new UpdateWrapper<Notebooks>().eq("id", Id).eq("user_id", userId));
        if (result <= 0) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR, "更新失败，影响行数为0");
        }
        return true;
    }

    @Override
    public boolean deleteNotebook(Integer Id, Integer userId) {
        if (Id == null || userId == null) {
            throw new BusinessException(ResultType.INVALID_REQUEST_BODY, "笔记本ID和用户ID不能为空");
        }
        int result = notebooksMapper.delete(new QueryWrapper<Notebooks>().eq("id", Id).eq("user_id", userId));
        if (result <= 0) {
            throw new BusinessException(ResultType.NOT_FOUND, "删除失败，笔记本不存在或已被删除");
        }
        return true;
    }
}











































