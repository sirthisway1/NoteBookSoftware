package com.example.markdown_demo.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotebookCreateDTO {

    @NotBlank(message = "笔记本名称不能为空")
    @Size(max = 100, message = "笔记本名称不能超过100个字符")
    private String name;

    @Size(max = 500, message = "描述不能超过500个字符")
    private String summary;

    // You can add other fields here if needed
}