package com.example.markdown_demo.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteShowDTO {
    private Integer notebookId;
    private Integer noteId;
    private String title;
    private String createdAt;
    private String updatedAt;
    private Boolean isPrivate;
    private List<String> tags;
    private Integer likeCount; // 存储点赞数

}