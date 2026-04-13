package com.company.bbs.wechat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WeChatProperties {
    
    private String corpId;
    private String agentId;
    private String secret;
    private String tokenUrl;
    private String userInfoUrl;
    private String convertToOpenidUrl;
}