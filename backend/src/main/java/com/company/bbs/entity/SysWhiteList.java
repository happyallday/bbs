package com.company.bbs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_white_list")
public class SysWhiteList {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Integer whiteType;
    
    private String reason;
    
    private Long operatorId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    private Integer status;
}