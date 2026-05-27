package com.lifestyle.platform.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVO {

    private Long id;
    private Long noteId;
    private Long userId;
    private Long parentId;
    private String content;
    private LocalDateTime createdAt;

    private String userNickname;
    private String userAvatar;
    private List<CommentVO> children;
}
