package com.company.bbs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.bbs.dto.RoleRequest;
import com.company.bbs.entity.SysRole;
import com.company.bbs.mapper.SysRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class RoleManagementService {
    
    @Autowired
    private SysRoleMapper sysRoleMapper;
    
    public Page<SysRole> list(Integer current, Integer size, RoleRequest request) {
        Page<SysRole> page = new Page<>(current, size);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(request.getRoleName())) {
            wrapper.like(SysRole::getRoleName, request.getRoleName());
        }
        if (StringUtils.hasText(request.getRoleCode())) {
            wrapper.like(SysRole::getRoleCode, request.getRoleCode());
        }
        
        wrapper.orderByDesc(SysRole::getCreatedTime);
        
        return sysRoleMapper.selectPage(page, wrapper);
    }
    
    public SysRole getById(Long id) {
        return sysRoleMapper.selectById(id);
    }
    
    public List<SysRole> getAll() {
        return sysRoleMapper.selectList(
            new LambdaQueryWrapper<SysRole>()
        );
    }
    
    public void add(RoleRequest request, Long operatorId) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, request.getRoleCode());
        if (sysRoleMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("角色编码已存在");
        }
        
        SysRole role = new SysRole();
        role.setRoleName(request.getRoleName());
        role.setRoleCode(request.getRoleCode());
        role.setDescription(request.getDescription());
        
        sysRoleMapper.insert(role);
        
        log.info("添加角色成功 - 角色名: {}, 编码: {}, 操作人: {}", 
            request.getRoleName(), request.getRoleCode(), operatorId);
    }
    
    public void update(RoleRequest request, Long operatorId) {
        if (request.getId() == null) {
            throw new RuntimeException("ID不能为空");
        }
        
        SysRole exists = sysRoleMapper.selectById(request.getId());
        if (exists == null) {
            throw new RuntimeException("角色不存在");
        }
        
        if (StringUtils.hasText(request.getRoleCode())) {
            LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysRole::getRoleCode, request.getRoleCode())
                   .ne(SysRole::getId, request.getId());
            if (sysRoleMapper.selectCount(wrapper) > 0) {
                throw new RuntimeException("角色编码已存在");
            }
            exists.setRoleCode(request.getRoleCode());
        }
        
        if (StringUtils.hasText(request.getRoleName())) {
            exists.setRoleName(request.getRoleName());
        }
        if (request.getDescription() != null) {
            exists.setDescription(request.getDescription());
        }
        
        sysRoleMapper.updateById(exists);
        
        log.info("更新角色成功 - ID: {}, 操作人: {}", request.getId(), operatorId);
    }
    
    public void delete(Long id, Long operatorId) {
        SysRole exists = sysRoleMapper.selectById(id);
        if (exists == null) {
            throw new RuntimeException("角色不存在");
        }
        
        sysRoleMapper.deleteById(id);
        
        log.info("删除角色成功 - 角色名: {}, 操作人: {}", exists.getRoleName(), operatorId);
    }
}