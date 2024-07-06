package com.example.markdown_demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.markdown_demo.common.typehandler.ListToStringTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Notes entity class
 * </p>
 *
 * @author xmg
 * @since 2024-07-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "m_notes", autoResultMap = true) // autoResultMap is important to enable type handler
public class Notes implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer notebookId;

    private Integer userId;

    private String title;

    private String content;

    @TableField(typeHandler = ListToStringTypeHandler.class)
    private List<String> tags;

    private Boolean isPrivate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
