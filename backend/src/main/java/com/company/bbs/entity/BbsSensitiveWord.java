package com.company.bbs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("bbs_sensitive_word")
public class BbsSensitiveWord {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String word;
    
    private Integer wordType;
    
    private String category;
    
    private Integer severity;
    
    private Integer isRegex;
    
    private Integer hitCount;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}