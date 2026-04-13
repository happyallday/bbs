package com.company.bbs.service;

import com.company.bbs.entity.SysUser;
import com.company.bbs.mapper.SysUserMapper;
import com.company.bbs.wechat.WeChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DepartmentService {
    
    @Autowired
    private WeChatService weChatService;
    
    public String resolveDepartment(String wechatUserid) {
        try {
            com.company.bbs.wechat.dto.WeChatDepartmentResponse deptResponse = weChatService.getDepartments();
            if (deptResponse.getDepartment() != null && !deptResponse.getDepartment().isEmpty()) {
                return deptResponse.getDepartment().get(0).getName();
            }
        } catch (Exception e) {
            log.warn("获取部门信息失败", e);
        }
        return "未设置部门";
    }
}