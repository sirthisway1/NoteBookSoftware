package com.example.markdown_demo.common.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeDTO {
    @NotNull(message = "点赞状态不能为空")
    private Boolean liked; // true 表示点赞，false 表示取消点赞

    @NotNull(message = "点赞数量不能为空")
    private Integer likeCount; // 点赞总数
}