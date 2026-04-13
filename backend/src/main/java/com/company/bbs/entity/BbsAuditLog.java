package com.company.bbs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("bbs_audit_log")
public class BbsAuditLog {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String contentType;
    
    private Long contentId;
    
    private Long userId;
    
    private Long auditorId;
    
    private Integer auditStatus;
    
    private String sensitiveWords;
    
    private String contentSnippet;
    
    private String auditRemark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    private LocalDateTime auditedTime;
}