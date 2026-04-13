package com.company.bbs.wechat;

import com.company.bbs.wechat.dto.WeChatMessageRequest;
import com.company.bbs.wechat.dto.WeChatMessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WeChatMessageService {
    
    @Autowired
    private WeChatProperties weChatProperties;
    
    @Autowired
    private WeChatService weChatService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Async
    public void sendTextMessage(String userId, String content) {
        try {
            String accessToken = weChatService.getAccessToken();
            
            WeChatMessageRequest request = new WeChatMessageRequest();
            request.setTouser(userId);
            request.setMsgtype("text");
            
            WeChatMessageRequest.Text text = new WeChatMessageRequest.Text();
            text.setContent(content);
            request.setText(text);
            
            sendRequest(accessToken, request);
            
            log.info("发送文本消息成功 - 用户: {}", userId);
        } catch (Exception e) {
            log.error("发送文本消息失败 - 用户: {}", userId, e);
        }
    }
    
    @Async
    public void sendMarkdownMessage(String userId, String content) {
        try {
            String accessToken = weChatService.getAccessToken();
            
            WeChatMessageRequest request = new WeChatMessageRequest();
            request.setTouser(userId);
            request.setMsgtype("markdown");
            
            WeChatMessageRequest.Markdown markdown = new WeChatMessageRequest.Markdown();
            markdown.setContent(content);
            request.setMarkdown(markdown);
            
            sendRequest(accessToken, request);
            
            log.info("发送Markdown消息成功 - 用户: {}", userId);
        } catch (Exception e) {
            log.error("发送Markdown消息失败 - 用户: {}", userId, e);
        }
    }
    
    private void sendRequest(String accessToken, WeChatMessageRequest request) throws Exception {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + accessToken;
        
        Map<String, Object> body = new HashMap<>();
        body.put("touser", request.getTouser());
        body.put("msgtype", request.getMsgtype());
        body.put("agentid", Integer.parseInt(weChatProperties.getAgentId()));
        
        if ("text".equals(request.getMsgtype())) {
            Map<String, String> textContent = new HashMap<>();
            textContent.put("content", request.getText().getContent());
            body.put("text", textContent);
        } else if ("markdown".equals(request.getMsgtype())) {
            Map<String, String> markdownContent = new HashMap<>();
            markdownContent.put("content", request.getMarkdown().getContent());
            body.put("markdown", markdownContent);
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);
        
        String response = restTemplate.postForObject(url, entity, String.class);
        
        WeChatMessageResponse msgResponse = objectMapper.readValue(response, WeChatMessageResponse.class);
        
        if (msgResponse.getErrcode() != 0) {
            log.error("企业微信消息发送失败: errcode={}, errmsg={}", 
                msgResponse.getErrcode(), msgResponse.getErrmsg());
            throw new RuntimeException("消息发送失败: " + msgResponse.getErrmsg());
        }
    }
    
    public void sendPostNotification(String toUserId, String postTitle, String authorName, String url) {
        String content = String.format(
            "您关注的帖子有新动态\n" +
            "====================================\n" +
            "标题: %s\n" +
            "作者: %s\n" +
            "点击查看详情: %s\n" +
            "====================================\n" +
            "此消息由员工论坛系统自动发送",
            postTitle, authorName, url
        );
        
        sendTextMessage(toUserId, content);
    }
    
    public void sendAuditNotification(String toUserId, String contentType, String result, String remark) {
        String content = String.format(
            "您发布的内容审核完成\n" +
            "====================================\n" +
            "内容类型: %s\n" +
            "审核结果: %s\n" +
            "%s" +
            "====================================\n" +
            "此消息由员工论坛系统自动发送",
            contentType, result, 
            remark != null && !remark.isEmpty() ? "审核意见: " + remark + "\n" : ""
        );
        
        sendTextMessage(toUserId, content);
    }
    
    public void sendReplyNotification(String toUserId, String postTitle, String authorName, String replyContent) {
        String content = String.format(
            "您的帖子有新回复\n" +
            "====================================\n" +
            "帖子标题: %s\n" +
            "回复人: %s\n" +
            "回复内容: %s\n" +
            "====================================\n" +
            "此消息由员工论坛系统自动发送",
            postTitle, authorName, 
            replyContent.length() > 100 ? replyContent.substring(0, 100) + "..." : replyContent
        );
        
        sendTextMessage(toUserId, content);
    }
    
    public void sendSystemAlert(String toUserId, String title, String message) {
        String mdContent = String.format(
            "## %s\n\n" +
            "%s\n\n" +
            "---\n\n" +
            "> 此消息由员工论坛系统自动发送",
            title, message
        );
        
        sendMarkdownMessage(toUserId, mdContent);
    }
}