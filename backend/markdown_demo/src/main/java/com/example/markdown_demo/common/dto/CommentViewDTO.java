package com.example.markdown_demo.common.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentViewDTO {


    private String content;
    private String username;
    private String createdAt;
    private Integer commentId;
}


