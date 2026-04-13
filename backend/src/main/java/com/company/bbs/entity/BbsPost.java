package com.company.bbs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("bbs_post")
public class BbsPost {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String postNumber;
    
    private Long userId;
    
    private Long boardId;
    
    private String title;
    
    private String content;
    
    private String summary;
    
    private Integer viewCount;
    
    private Integer likeCount;
    
    private Integer replyCount;
    
    private Integer collectCount;
    
    private Integer status;
    
    private Integer auditStatus;
    
    private Integer isTop;
    
    private Integer isEssence;
    
    private String sensitiveWords;
    
    private Long auditUserId;
    
    private LocalDateTime auditTime;
    
    private String auditRemark;
    
    private LocalDateTime publishedTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
}