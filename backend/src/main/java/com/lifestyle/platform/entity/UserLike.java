package com.lifestyle.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("likes")
public class UserLike {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long noteId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
