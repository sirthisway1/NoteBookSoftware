package com.example.markdown_demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.markdown_demo.common.typehandler.LocalDateTimeToStringTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author xmg
 * @since 2024-07-05
 */
@Data
@TableName(value="m_thought_notes", autoResultMap = true)
public class ThoughtNotes implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer noteId;

    /**
     * 0-4代表5种类型的笔记
     */
    private Integer type;

    @TableField(typeHandler = LocalDateTimeToStringTypeHandler.class)
    private String createdAt;

    @TableField(typeHandler = LocalDateTimeToStringTypeHandler.class)
    private String updatedAt;


    @Override
    public String toString() {
        return "ThoughtNotes{" +
            "id = " + id +
            ", noteId = " + noteId +
            ", type = " + type +
            ", createdAt = " + createdAt +
            ", updatedAt = " + updatedAt +
        "}";
    }
}
