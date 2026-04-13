package com.company.bbs.wechat.dto;

import lombok.Data;

@Data
public class WeChatAccessTokenResponse {
    
    private Integer errcode;
    private String errmsg;
    private String access_token;
    private Integer expires_in;
}