package com.example.markdown_demo.common.dto;

public class LikeDTO {
    private boolean liked; // true 表示点赞，false 表示取消点赞
    private int likeCount; // 点赞总数

    public LikeDTO(boolean liked, int likeCount) {
        this.liked = liked;
        this.likeCount = likeCount;
    }

    // Getter 和 Setter 方法
    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
