package com.company.bbs.wechat.dto;

import lombok.Data;

@Data
public class WeChatMessageRequest {
    
    private String touser;
    
    private String msgtype;
    
    private Text text;
    
    private Markdown markdown;
    
    @Data
    public static class Text {
        private String content;
    }
    
    @Data
    public static class Markdown {
        private String content;
    }
}