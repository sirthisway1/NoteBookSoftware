package com.example.markdown_demo.common.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeywordFrequencyDTO {



        private String keyword; // 关键词

        private Long value; // 关键词的频率

}


