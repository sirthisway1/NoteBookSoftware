package com.example.markdown_demo.common.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class NotebookDetailDTO {
    private String notebookId;
    private String name;
    private LocalDateTime lastModified;
    private String summary;
    private List<NoteSearchResultDTO> notes;

}