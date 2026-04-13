package com.company.bbs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.bbs.dto.ResponseResult;
import com.company.bbs.dto.RoleRequest;
import com.company.bbs.entity.SysRole;
import com.company.bbs.service.RoleManagementService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleManagementController {
    
    @Autowired
    private RoleManagementService roleManagementService;
    
    @GetMapping("/list")
    public ResponseResult<Page<SysRole>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            RoleRequest request) {
        
        Page<SysRole> page = roleManagementService.list(current, size, request);
        return ResponseResult.success(page);
    }
    
    @GetMapping("/all")
    public ResponseResult<List<SysRole>> getAll() {
        List<SysRole> roles = roleManagementService.getAll();
        return ResponseResult.success(roles);
    }
    
    @GetMapping("/{id}")
    public ResponseResult<SysRole> getById(@PathVariable Long id) {
        SysRole role = roleManagementService.getById(id);
        if (role == null) {
            return ResponseResult.error("角色不存在");
        }
        return ResponseResult.success(role);
    }
    
    @PostMapping("/add")
    public ResponseResult<Void> add(@Validated(RoleRequest.Create.class) @RequestBody RoleRequest request,
                                     HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            roleManagementService.add(request, userId);
            return ResponseResult.success("添加成功");
        } catch (Exception e) {
            log.error("添加角色失败", e);
            return ResponseResult.error("添加失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/update")
    public ResponseResult<Void> update(@RequestBody RoleRequest request,
                                        HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            roleManagementService.update(request, userId);
            return ResponseResult.success("更新成功");
        } catch (Exception e) {
            log.error("更新角色失败", e);
            return ResponseResult.error("更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id,
                                        HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            roleManagementService.delete(id, userId);
            return ResponseResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除角色失败", e);
            return ResponseResult.error("删除失败: " + e.getMessage());
        }
    }
}