package com.company.bbs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.bbs.dto.LoginRequest;
import com.company.bbs.dto.LoginResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        WeChatUserInfoResponse wechatUserInfo = weChatService.getUserInfo(request.getCode());
        
        String wechatUserid = wechatUserInfo.getUserid();
        
        SysUser user = sysUserMapper.findByWechatUserid(wechatUserid);
        
        if (user == null) {
            user = createNewUser(wechatUserid);
        }
        
        String clientIp = getClientIp(httpRequest);
        user.setLastLoginTime(java.time.LocalDateTime.now());
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
            user.setDepartment(resolveDepartment(wechatUserid));
            
            sysUserMapper.insert(user);
            
            assignDefaultRole(user.getId());
            
            log.info("创建新用户: {}", user.getUsername());
            return user;
        } catch (Exception e) {
            log.error("创建新用户失败", e);
            throw new RuntimeException("创建用户失败", e);
        }
    }
    
    private String resolveDepartment(String wechatUserid) {
        try {
            WeChatDepartmentResponse deptResponse = weChatService.getDepartments();
            if (deptResponse.getDepartment() != null && !deptResponse.getDepartment().isEmpty()) {
                return deptResponse.getDepartment().get(0).getName();
            }
        } catch (Exception e) {
            log.warn("获取部门信息失败", e);
        }
        return "未设置部门";
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
    
    private void assignDefaultRole(Long userId) {
        sysUserMapper.insertUserRole(userId, 2L);
    }
}