package com.company.bbs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String realName;
    
    private String wechatUserid;
    
    private String wechatOpenid;
    
    private String email;
    
    private String mobile;
    
    private String avatar;
    
    private String department;
    
    private String position;
    
    private Integer status;
    
    private Integer isWhiteList;
    
    private LocalDateTime lastLoginTime;
    
    private String lastLoginIp;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
}