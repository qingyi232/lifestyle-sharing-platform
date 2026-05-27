package com.lifestyle.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class NoteRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String coverImage;
    private String images;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private List<String> tags;
}
