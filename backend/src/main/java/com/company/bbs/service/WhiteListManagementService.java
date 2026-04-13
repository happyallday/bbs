package com.company.bbs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.bbs.entity.SysWhiteList;
import com.company.bbs.mapper.SysWhiteListMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class WhiteListManagementService {
    
    @Autowired
    private SysWhiteListMapper sysWhiteListMapper;
    
    public Page<SysWhiteList> list(Integer current, Integer size, Integer whiteType) {
        Page<SysWhiteList> page = new Page<>(current, size);
        LambdaQueryWrapper<SysWhiteList> wrapper = new LambdaQueryWrapper<>();
        
        if (whiteType != null) {
            wrapper.eq(SysWhiteList::getWhiteType, whiteType);
        }
        
        wrapper.eq(SysWhiteList::getStatus, 1)
               .orderByDesc(SysWhiteList::getCreatedTime);
        
        return sysWhiteListMapper.selectPage(page, wrapper);
    }
    
    public void add(Long userId, Integer whiteType, String reason, Long operatorId) {
        LambdaQueryWrapper<SysWhiteList> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysWhiteList::getUserId, userId)
               .eq(SysWhiteList::getWhiteType, whiteType);
        if (sysWhiteListMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("该用户已在白名单中");
        }
        
        SysWhiteList whiteList = new SysWhiteList();
        whiteList.setUserId(userId);
        whiteList.setWhiteType(whiteType);
        whiteList.setReason(reason);
        whiteList.setOperatorId(operatorId);
        whiteList.setStatus(1);
        
        sysWhiteListMapper.insert(whiteList);
        
        log.info("添加白名单用户成功 - 用户ID: {}, 类型: {}, 原因: {}, 操作人: {}", 
            userId, whiteType, reason, operatorId);
    }
    
    public void disable(Long id, Long operatorId) {
        SysWhiteList whiteList = sysWhiteListMapper.selectById(id);
        if (whiteList == null) {
            throw new RuntimeException("白名单记录不存在");
        }
        
        whiteList.setStatus(0);
        sysWhiteListMapper.updateById(whiteList);
        
        log.info("禁用白名单用户成功 - 记录ID: {}, 操作人: {}", id, operatorId);
    }
    
    public void removeByUserId(Long userId, Integer whiteType, Long operatorId) {
        List<SysWhiteList> whiteLists = sysWhiteListMapper.selectList(
            new LambdaQueryWrapper<SysWhiteList>()
                .eq(SysWhiteList::getUserId, userId)
                .eq(SysWhiteList::getWhiteType, whiteType)
                .eq(SysWhiteList::getStatus, 1)
        );
        
        for (SysWhiteList whiteList : whiteLists) {
            whiteList.setStatus(0);
            sysWhiteListMapper.updateById(whiteList);
        }
        
        log.info("移除用户白名单成功 - 用户ID: {}, 类型: {}, 操作人: {}", userId, whiteType, operatorId);
    }
    
    public boolean isWhiteListUser(Long userId, Integer whiteType) {
        int count = sysWhiteListMapper.countByUserIdAndType(userId, whiteType);
        return count > 0;
    }
    
    public List<SysWhiteList> getUserWhiteLists(Long userId) {
        return sysWhiteListMapper.selectList(
            new LambdaQueryWrapper<SysWhiteList>()
                .eq(SysWhiteList::getUserId, userId)
                .eq(SysWhiteList::getStatus, 1)
        );
    }
}