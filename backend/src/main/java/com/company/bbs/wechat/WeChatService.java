package com.company.bbs.wechat;

import com.company.bbs.wechat.dto.WeChatAccessTokenResponse;
import com.company.bbs.wechat.dto.WeChatDepartmentResponse;
import com.company.bbs.wechat.dto.WeChatUserInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WeChatService {
    
    @Autowired
    private WeChatProperties weChatProperties;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String ACCESS_TOKEN_KEY = "wechat:access:token";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public String getAccessToken() {
        try {
            Object cachedToken = redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY);
            if (cachedToken != null) {
                return cachedToken.toString();
            }
            
            String url = weChatProperties.getTokenUrl();
            Map<String, String> params = new HashMap<>();
            params.put("corpid", weChatProperties.getCorpId());
            params.put("corpsecret", weChatProperties.getSecret());
            
            ResponseEntity<String> response = restTemplate.getForEntity(url + "?corpid={corpid}&corpsecret={corpsecret}", String.class, params);
            
            WeChatAccessTokenResponse tokenResponse = objectMapper.readValue(response.getBody(), WeChatAccessTokenResponse.class);
            
            if (tokenResponse.getErrcode() == 0) {
                String accessToken = tokenResponse.getAccess_token();
                redisTemplate.opsForValue().set(ACCESS_TOKEN_KEY, accessToken, 7200, TimeUnit.SECONDS);
                return accessToken;
            } else {
                log.error("获取企业微信Access Token失败: {}", tokenResponse.getErrmsg());
                throw new RuntimeException("获取Access Token失败");
            }
        } catch (Exception e) {
            log.error("获取Access Token异常", e);
            throw new RuntimeException("获取Access Token异常", e);
        }
    }
    
    public WeChatUserInfoResponse getUserInfo(String code) {
        try {
            String accessToken = getAccessToken();
            String url = weChatProperties.getUserInfoUrl();
            
            Map<String, String> params = new HashMap<>();
            params.put("access_token", accessToken);
            params.put("code", code);
            
            ResponseEntity<String> response = restTemplate.getForEntity(
                url + "?access_token={access_token}&code={code}", 
                String.class, 
                params
            );
            
            WeChatUserInfoResponse userInfo = objectMapper.readValue(response.getBody(), WeChatUserInfoResponse.class);
            
            if (userInfo.getErrcode() != 0) {
                log.error("获取用户信息失败: {}", userInfo.getErrmsg());
                throw new RuntimeException("获取用户信息失败");
            }
            
            return userInfo;
        } catch (Exception e) {
            log.error("获取用户信息异常", e);
            throw new RuntimeException("获取用户信息异常", e);
        }
    }
    
    public WeChatUserInfoResponse getDetailUserInfo(String userId) {
        try {
            String accessToken = getAccessToken();
            String url = "https://qyapi.weixin.qq.com/cgi-bin/user/get";
            
            Map<String, String> params = new HashMap<>();
            params.put("access_token", accessToken);
            params.put("userid", userId);
            
            ResponseEntity<String> response = restTemplate.getForEntity(
                url + "?access_token={access_token}&userid={userid}",
                String.class,
                params
            );
            
            WeChatUserInfoResponse userInfo = objectMapper.readValue(response.getBody(), WeChatUserInfoResponse.class);
            
            if (userInfo.getErrcode() != 0) {
                log.error("获取用户详细信息失败: {}", userInfo.getErrmsg());
                throw new RuntimeException("获取用户详细信息失败");
            }
            
            return userInfo;
        } catch (Exception e) {
            log.error("获取用户详细信息异常", e);
            throw new RuntimeException("获取用户详细信息异常", e);
        }
    }
    
    public WeChatDepartmentResponse getDepartments() {
        try {
            String accessToken = getAccessToken();
            String url = "https://qyapi.weixin.qq.com/cgi-bin/department/list";
            
            Map<String, String> params = new HashMap<>();
            params.put("access_token", accessToken);
            params.put("id", "");
            
            ResponseEntity<String> response = restTemplate.getForEntity(
                url + "?access_token={access_token}",
                String.class,
                params
            );
            
            WeChatDepartmentResponse deptResponse = objectMapper.readValue(response.getBody(), WeChatDepartmentResponse.class);
            
            if (deptResponse.getErrcode() != 0) {
                log.error("获取部门列表失败: {}", deptResponse.getErrmsg());
                throw new RuntimeException("获取部门列表失败");
            }
            
            return deptResponse;
        } catch (Exception e) {
            log.error("获取部门列表异常", e);
            throw new RuntimeException("获取部门列表异常", e);
        }
    }
}