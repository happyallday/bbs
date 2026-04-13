package com.company.bbs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("bbs_notification")
public class BbsNotification {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String notificationType;
    
    private String title;
    
    private String content;
    
    private Long relatedPostId;
    
    private Long relatedUserId;
    
    private Integer isRead;
    
    private Integer pushStatus;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}