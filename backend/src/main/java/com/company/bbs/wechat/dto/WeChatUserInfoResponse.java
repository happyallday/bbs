package com.company.bbs.wechat.dto;

import lombok.Data;

@Data
public class WeChatUserInfoResponse {
    
    private Integer errcode;
    private String errmsg;
    private String userid;
    private String name;
    private String mobile;
    private String gender;
    private String email;
    private String avatar;
    private String status;
    private Integer enable;
    private String position;
}