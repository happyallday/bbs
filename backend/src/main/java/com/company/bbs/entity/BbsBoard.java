package com.company.bbs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("bbs_board")
public class BbsBoard {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String boardName;
    
    private String boardCode;
    
    private String description;
    
    private String iconUrl;
    
    private Integer sortOrder;
    
    private Integer postCount;
    
    private Integer status;
    
    private Integer requireAuth;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}