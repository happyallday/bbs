package com.company.bbs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.bbs.dto.UserRequest;
import com.company.bbs.entity.SysUser;
import com.company.bbs.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class UserManagementService {
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private RoleManagementService roleManagementService;
    
    @Autowired
    private WhiteListManagementService whiteListManagementService;
    
    public Page<SysUser> list(Integer current, Integer size, UserRequest request) {
        Page<SysUser> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(request.getUsername())) {
            wrapper.like(SysUser::getUsername, request.getUsername());
        }
        if (StringUtils.hasText(request.getRealName())) {
            wrapper.like(SysUser::getRealName, request.getRealName());
        }
        if (StringUtils.hasText(request.getDepartment())) {
            wrapper.like(SysUser::getDepartment, request.getDepartment());
        }
        if (request.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, request.getStatus());
        }
        if (request.getIsWhiteList() != null) {
            wrapper.eq(SysUser::getIsWhiteList, request.getIsWhiteList());
        }
        
        wrapper.orderByDesc(SysUser::getCreatedTime);
        
        return sysUserMapper.selectPage(page, wrapper);
    }
    
    public SysUser getById(Long id) {
        return sysUserMapper.selectById(id);
    }
    
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
        );
    }
    
    public Page<SysUser> getActiveUsers(Integer current, Integer size) {
        Page<SysUser> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getStatus, 1)
               .orderByDesc(SysUser::getLastLoginTime);
        return sysUserMapper.selectPage(page, wrapper);
    }
    
    public void add(UserRequest request, Long operatorId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        if (sysUserMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setRealName(request.getRealName());
        
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setDepartment(request.getDepartment());
        user.setPosition(request.getPosition());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        user.setIsWhiteList(0);
        
        sysUserMapper.insert(user);
        
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            for (Long roleId : request.getRoleIds()) {
                sysUserMapper.insertUserRole(user.getId(), roleId);
            }
        }
        
        if (request.getIsWhiteList() != null && request.getIsWhiteList() == 1) {
            whiteListManagementService.add(user.getId(), 1, "管理员添加白名单", operatorId);
        }
        
        log.info("添加用户成功 - 用户名: {}, 操作人: {}", request.getUsername(), operatorId);
    }
    
    public void update(UserRequest request, Long operatorId) {
        if (request.getId() == null) {
            throw new RuntimeException("ID不能为空");
        }
        
        SysUser exists = sysUserMapper.selectById(request.getId());
        if (exists == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (StringUtils.hasText(request.getUsername())) {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, request.getUsername())
                   .ne(SysUser::getId, request.getId());
            if (sysUserMapper.selectCount(wrapper) > 0) {
                throw new RuntimeException("用户名已存在");
            }
            exists.setUsername(request.getUsername());
        }
        
        if (StringUtils.hasText(request.getRealName())) {
            exists.setRealName(request.getRealName());
        }
        if (StringUtils.hasText(request.getEmail())) {
            exists.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getMobile())) {
            exists.setMobile(request.getMobile());
        }
        if (StringUtils.hasText(request.getDepartment())) {
            exists.setDepartment(request.getDepartment());
        }
        if (StringUtils.hasText(request.getPosition())) {
            exists.setPosition(request.getPosition());
        }
        if (request.getStatus() != null) {
            exists.setStatus(request.getStatus());
        }
        
        sysUserMapper.updateById(exists);
        
        log.info("更新用户成功 - ID: {}, 操作人: {}", request.getId(), operatorId);
    }
    
    public void delete(Long id, Long operatorId) {
        SysUser exists = sysUserMapper.selectById(id);
        if (exists == null) {
            throw new RuntimeException("用户不存在");
        }
        
        exists.setDeleted(1);
        sysUserMapper.updateById(exists);
        
        log.info("删除用户成功 - 用户名: {}, 操作人: {}", exists.getUsername(), operatorId);
    }
    
    public void enable(Long id, Long operatorId) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setStatus(1);
        sysUserMapper.updateById(user);
        
        log.info("启用用户成功 - 用户名: {}, 操作人: {}", user.getUsername(), operatorId);
    }
    
    public void disable(Long id, Long operatorId) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setStatus(0);
        sysUserMapper.updateById(user);
        
        log.info("禁用用户成功 - 用户名: {}, 操作人: {}", user.getUsername(), operatorId);
    }
    
    public void resetPassword(Long id, String newPassword, Long operatorId) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserMapper.updateById(user);
        
        log.info("重置用户密码成功 - 用户名: {}, 操作人: {}", user.getUsername(), operatorId);
    }
    
    public void addToWhiteList(Long userId, String reason, Long operatorId) {
        whiteListManagementService.add(userId, 1, reason, operatorId);
    }
    
    public void removeFromWhiteList(Long userId, Long operatorId) {
        whiteListManagementService.removeByUserId(userId, 1, operatorId);
    }
}