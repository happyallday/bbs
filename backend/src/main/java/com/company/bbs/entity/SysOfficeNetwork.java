package com.company.bbs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_office_network")
public class SysOfficeNetwork {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String ipRange;
    
    private String description;
    
    private Integer networkType;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    private Long operatorId;
}