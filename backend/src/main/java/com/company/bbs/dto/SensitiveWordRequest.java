package com.company.bbs.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SensitiveWordRequest {
    
    private Long id;
    
    @NotBlank(message = "敏感词不能为空", groups = {Create.class})
    private String word;
    
    private Integer wordType;
    
    private String category;
    
    private Integer severity;
    
    private Integer isRegex;
    
    private Integer status;
    
    public interface Create {}
}