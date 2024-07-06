package com.example.markdown_demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.markdown_demo.entity.Notes;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotesMapper extends BaseMapper<Notes> {
    void insertNoteRight(Notes note);
}
