package com.example.markdown_demo.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteShowWithUserDTO {

    private Integer id;
    private String username;
    private String title;
    private String content;
    private String updatedAt;
}