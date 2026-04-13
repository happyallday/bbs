package com.company.bbs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.bbs.dto.ResponseResult;
import com.company.bbs.dto.UserRequest;
import com.company.bbs.entity.SysUser;
import com.company.bbs.service.UserManagementService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {
    
    @Autowired
    private UserManagementService userManagementService;
    
    @GetMapping("/list")
    public ResponseResult<Page<SysUser>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            UserRequest request) {
        
        Page<SysUser> page = userManagementService.list(current, size, request);
        return ResponseResult.success(page);
    }
    
    @GetMapping("/active")
    public ResponseResult<Page<SysUser>> getActiveUsers(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Page<SysUser> page = userManagementService.getActiveUsers(current, size);
        return ResponseResult.success(page);
    }
    
    @GetMapping("/{id}")
    public ResponseResult<SysUser> getById(@PathVariable Long id) {
        SysUser user = userManagementService.getById(id);
        if (user == null) {
            return ResponseResult.error("用户不存在");
        }
        user.setPassword(null);
        return ResponseResult.success(user);
    }
    
    @PostMapping("/add")
    public ResponseResult<Void> add(@Validated(UserRequest.Create.class) @RequestBody UserRequest request,
                                     HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            userManagementService.add(request, userId);
            return ResponseResult.success("添加成功");
        } catch (Exception e) {
            log.error("添加用户失败", e);
            return ResponseResult.error("添加失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/update")
    public ResponseResult<Void> update(@RequestBody UserRequest request,
                                        HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            userManagementService.update(request, userId);
            return ResponseResult.success("更新成功");
        } catch (Exception e) {
            log.error("更新用户失败", e);
            return ResponseResult.error("更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id,
                                        HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            userManagementService.delete(id, userId);
            return ResponseResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return ResponseResult.error("删除失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/enable/{id}")
    public ResponseResult<Void> enable(@PathVariable Long id,
                                        HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            userManagementService.enable(id, userId);
            return ResponseResult.success("启用成功");
        } catch (Exception e) {
            log.error("启用用户失败", e);
            return ResponseResult.error("启用失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/disable/{id}")
    public ResponseResult<Void> disable(@PathVariable Long id,
                                         HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            userManagementService.disable(id, userId);
            return ResponseResult.success("禁用成功");
        } catch (Exception e) {
            log.error("禁用用户失败", e);
            return ResponseResult.error("禁用失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/reset-password/{id}")
    public ResponseResult<Void> resetPassword(@PathVariable Long id,
                                               @RequestBody java.util.Map<String, String> data,
                                               HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            String newPassword = data.get("password");
            if (newPassword == null || newPassword.isEmpty()) {
                return ResponseResult.error("新密码不能为空");
            }
            
            userManagementService.resetPassword(id, newPassword, userId);
            return ResponseResult.success("重置密码成功");
        } catch (Exception e) {
            log.error("重置密码失败", e);
            return ResponseResult.error("重置密码失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/whitelist-add/{userId}")
    public ResponseResult<Void> addToWhiteList(@PathVariable Long userId,
                                                @RequestBody java.util.Map<String, String> data,
                                                HttpServletRequest httpRequest) {
        try {
            Long operatorId = (Long) httpRequest.getAttribute("userId");
            String reason = data.get("reason");
            userManagementService.addToWhiteList(userId, reason, operatorId);
            return ResponseResult.success("添加白名单成功");
        } catch (Exception e) {
            log.error("添加白名单失败", e);
            return ResponseResult.error("添加白名单失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/whitelist-remove/{userId}")
    public ResponseResult<Void> removeFromWhiteList(@PathVariable Long userId,
                                                      HttpServletRequest httpRequest) {
        try {
            Long operatorId = (Long) httpRequest.getAttribute("userId");
            userManagementService.removeFromWhiteList(userId, operatorId);
            return ResponseResult.success("移除白名单成功");
        } catch (Exception e) {
            log.error("移除白名单失败", e);
            return ResponseResult.error("移除白名单失败: " + e.getMessage());
        }
    }
}