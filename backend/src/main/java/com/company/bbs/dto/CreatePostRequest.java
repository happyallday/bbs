package com.company.bbs.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CreatePostRequest {
    
    @NotBlank(message = "帖子标题不能为空")
    private String title;
    
    @NotBlank(message = "帖子内容不能为空")
    private String content;
    
    @NotNull(message = "板块ID不能为空")
    private Long boardId;
    
    private Integer status;
}