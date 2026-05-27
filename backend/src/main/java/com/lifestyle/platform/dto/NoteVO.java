package com.lifestyle.platform.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NoteVO {

    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String coverImage;
    private String images;
    private Long categoryId;
    private Integer status;
    private String rejectReason;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String authorName;
    private String authorAvatar;
    private String categoryName;
    private List<String> tags;
    private boolean liked;
    private boolean favorited;
}
