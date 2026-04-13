package com.company.bbs.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserRequest {
    
    private Long id;
    
    @NotBlank(message = "用户名不能为空", groups = {Create.class})
    private String username;
    
    @NotBlank(message = "真实姓名不能为空", groups = {Create.class})
    private String realName;
    
    private String password;
    
    private String email;
    
    private String mobile;
    
    private String department;
    
    private String position;
    
    private Integer status;
    
    private Integer isWhiteList;
    
    private List<Long> roleIds;
    
    public interface Create {}
}