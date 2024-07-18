package com.example.markdown_demo.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteFetchTimeDTO {
    private String content;
    private String updatedAt;
    private String title;
}
