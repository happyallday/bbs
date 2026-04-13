package com.company.bbs.controller;

import com.company.bbs.dto.LoginRequest;
import com.company.bbs.dto.LoginResponse;
import com.company.bbs.dto.ResponseResult;
import com.company.bbs.entity.SysUser;
import com.company.bbs.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseResult<LoginResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            log.info("用户登录请求: {}", request.getCode());
            LoginResponse response = authService.login(request, httpRequest);
            return ResponseResult.success("登录成功", response);
        } catch (Exception e) {
            log.error("登录失败", e);
            return ResponseResult.error("登录失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/info")
    public ResponseResult<SysUser> getUserInfo(HttpServletRequest request) {
        try {
            SysUser user = authService.getCurrentUser(request);
            if (user == null) {
                return ResponseResult.error(401, "未登录或Token已过期");
            }
            user.setPassword(null);
            return ResponseResult.success(user);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return ResponseResult.error("获取用户信息失败");
        }
    }
    
    @PostMapping("/logout")
    public ResponseResult<Void> logout(HttpServletRequest request) {
        return ResponseResult.success("退出成功");
    }
}