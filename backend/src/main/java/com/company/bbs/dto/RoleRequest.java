package com.company.bbs.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class RoleRequest {
    
    private Long id;
    
    @NotBlank(message = "角色名称不能为空", groups = {Create.class})
    private String roleName;
    
    @NotBlank(message = "角色编码不能为空", groups = {Create.class})
    private String roleCode;
    
    private String description;
    
    public interface Create {}
}