package com.example.markdown_demo.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteSearchResultDTO {

    private String id;
    private String title;
    private String snippet;  // A short excerpt of the content containing the keyword
    private List<String> tags;
}