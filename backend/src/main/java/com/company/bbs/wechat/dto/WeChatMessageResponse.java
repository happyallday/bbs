package com.company.bbs.wechat.dto;

import lombok.Data;

@Data
public class WeChatMessageResponse {
    
    private Integer errcode;
    
    private String errmsg;
    
    private String invaliduser;
    
    private String invalidparty;
    
    private String invalidtag;
    
    private String msgid;
}