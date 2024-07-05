package com.example.markdown_demo.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotePrivacyRequestDTO {

    @NotNull(message = "isPrivate field must not be null")
    private Boolean isPrivate;
}