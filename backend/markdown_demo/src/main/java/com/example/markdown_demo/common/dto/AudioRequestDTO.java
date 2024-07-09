package com.example.markdown_demo.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AudioRequestDTO {

    private String referWavPath;
    private String promptText;
    private String promptLanguage;
    private String text;
    private String textLanguage;
}