package com.example.markdown_demo.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteCreateDTO {

    @NotNull(message = "Notebook ID cannot be null")
    private Integer notebookId;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    private String content;

    private List<String> tags;
}