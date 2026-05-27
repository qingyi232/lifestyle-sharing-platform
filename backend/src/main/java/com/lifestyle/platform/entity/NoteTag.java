package com.lifestyle.platform.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("note_tags")
public class NoteTag {

    private Long noteId;
    private Long tagId;
}
