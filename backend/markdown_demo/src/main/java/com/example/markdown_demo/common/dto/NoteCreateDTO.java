package com.example.markdown_demo.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteCreateDTO {
    @NotNull(message = "笔记本 ID 不能为空")
    private Integer notebookId;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;
    private List<String> tags;

    @NotNull(message = "类型不能为空")
    private Integer type;
}