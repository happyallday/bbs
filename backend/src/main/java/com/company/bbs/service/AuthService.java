package com.company.bbs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.bbs.dto.LoginRequest;
import com.company.bbs.dto.LoginResponse;
import com.company.bbs.dto.UsernamePasswordLoginRequest;
import com.company.bbs.entity.SysUser;
import com.company.bbs.entity.SysWhiteList;
import com.company.bbs.mapper.SysUserMapper;
import com.company.bbs.mapper.SysWhiteListMapper;
import com.company.bbs.utils.JwtUtils;
import com.company.bbs.wechat.WeChatService;
import com.company.bbs.wechat.dto.WeChatUserInfoResponse;
import com.company.bbs.wechat.dto.WeChatDepartmentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Service
public class AuthService {
    
    @Autowired
    private WeChatService weChatService;
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Autowired
    private SysWhiteListMapper sysWhiteListMapper;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        WeChatUserInfoResponse wechatUserInfo = weChatService.getUserInfo(request.getCode());
        
        String wechatUserid = wechatUserInfo.getUserid();
        
        SysUser user = sysUserMapper.findByWechatUserid(wechatUserid);
        
        if (user == null) {
            user = createNewUser(wechatUserid);
        }
        
        String clientIp = getClientIp(httpRequest);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(clientIp);
        sysUserMapper.updateById(user);
        
        sysUserMapper.updateLastLogin(user.getId(), clientIp);
        
        String token = jwtUtils.generateToken(user.getUsername(), user.getId());
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setDepartment(user.getDepartment());
        userInfo.setPosition(user.getPosition());
        
        int whiteListCount = sysWhiteListMapper.countByUserIdAndType(user.getId(), 1);
        userInfo.setIsWhiteList(whiteListCount > 0 ? 1 : 0);
        
        response.setUserInfo(userInfo);
        
        return response;
    }
    
    public LoginResponse loginByUsernamePassword(UsernamePasswordLoginRequest request) {
        SysUser user = sysUserMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername())
        );
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("该用户可以使用企业微信或用户名密码登录");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用，请联系管理员");
        }
        
        String token = jwtUtils.generateToken(user.getUsername(), user.getId());
        
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp("local");
        sysUserMapper.updateById(user);
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setDepartment(user.getDepartment());
        userInfo.setPosition(user.getPosition());
        
        int whiteListCount = sysWhiteListMapper.countByUserIdAndType(user.getId(), 1);
        userInfo.setIsWhiteList(whiteListCount > 0 ? 1 : 0);
        
        response.setUserInfo(userInfo);
        
        log.info("用户名密码登录成功 - 用户名: {}", request.getUsername());
        
        return response;
    }
    
    private SysUser createNewUser(String wechatUserid) {
        try {
            WeChatUserInfoResponse detailInfo = weChatService.getDetailUserInfo(wechatUserid);
            
            SysUser user = new SysUser();
            user.setUsername("user_" + wechatUserid);
            user.setWechatUserid(wechatUserid);
            user.setRealName(detailInfo.getName() != null ? detailInfo.getName() : "Unknown");
            user.setMobile(detailInfo.getMobile());
            user.setAvatar(detailInfo.getAvatar());
            user.setPosition(detailInfo.getPosition());
            user.setStatus(1);
            user.setIsWhiteList(0);
            user.setDepartment(departmentService.resolveDepartment(wechatUserid));
            
            sysUserMapper.insert(user);
            
            sysUserMapper.insertUserRole(user.getId(), 2L);
            
            log.info("创建新用户: {}", user.getUsername());
            return user;
        } catch (Exception e) {
            log.error("创建新用户失败", e);
            throw new RuntimeException("创建用户失败", e);
        }
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    public SysUser getCurrentUser(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId != null) {
            return sysUserMapper.selectById(Long.valueOf(userId.toString()));
        }
        return null;
    }
    
    public boolean isWhiteListUser(Long userId) {
        int count = sysWhiteListMapper.countByUserIdAndType(userId, 1);
        return count > 0;
    }
    
    public void updateLastLogin(Long userId, String ip) {
        sysUserMapper.updateLastLogin(userId, ip);
    }
}