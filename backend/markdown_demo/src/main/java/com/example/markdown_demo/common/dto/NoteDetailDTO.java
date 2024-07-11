package com.example.markdown_demo.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDetailDTO {

    private Integer id;
    private Integer notebookId;
    private String title;
    private String content;
    private List<String> tags;
    private Boolean isPrivate;
    private String createdAt;
    private String updatedAt;
    private Integer type;
}