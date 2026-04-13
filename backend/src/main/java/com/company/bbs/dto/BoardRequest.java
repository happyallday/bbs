package com.company.bbs.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class BoardRequest {
    
    private Long id;
    
    @NotBlank(message = "板块名称不能为空", groups = {Create.class})
    private String boardName;
    
    @NotBlank(message = "板块编码不能为空", groups = {Create.class})
    private String boardCode;
    
    private String description;
    
    private String iconUrl;
    
    private Integer sortOrder;
    
    private Integer status;
    
    private Integer requireAuth;
    
    public interface Create {}
}