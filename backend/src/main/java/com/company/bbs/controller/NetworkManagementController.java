package com.company.bbs.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.bbs.dto.OfficeNetworkRequest;
import com.company.bbs.dto.ResponseResult;
import com.company.bbs.entity.SysOfficeNetwork;
import com.company.bbs.mapper.SysOfficeNetworkMapper;
import com.company.bbs.service.NetworkAccessService;
import com.company.bbs.utils.IpAddressUtils;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/network")
@PreAuthorize("hasRole('ADMIN')")
public class NetworkManagementController {
    
    @Autowired
    private NetworkAccessService networkAccessService;
    
    @Autowired
    private SysOfficeNetworkMapper sysOfficeNetworkMapper;
    
    @GetMapping("/list")
    public ResponseResult<Page<SysOfficeNetwork>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        
        Page<SysOfficeNetwork> page = new Page<>(current, size);
        LambdaQueryWrapper<SysOfficeNetwork> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            wrapper.eq(SysOfficeNetwork::getStatus, status);
        }
        
        wrapper.orderByAsc(SysOfficeNetwork::getNetworkType)
               .orderByDesc(SysOfficeNetwork::getCreatedTime);
        
        Page<SysOfficeNetwork> result = sysOfficeNetworkMapper.selectPage(page, wrapper);
        return ResponseResult.success(result);
    }
    
    @GetMapping("/all")
    public ResponseResult<java.util.List<SysOfficeNetwork>> getAll() {
        java.util.List<SysOfficeNetwork> list = sysOfficeNetworkMapper.selectList(
            new LambdaQueryWrapper<SysOfficeNetwork>()
                .eq(SysOfficeNetwork::getStatus, 1)
                .orderByAsc(SysOfficeNetwork::getNetworkType)
        );
        return ResponseResult.success(list);
    }
    
    @PostMapping("/add")
    public ResponseResult<Void> add(@RequestBody OfficeNetworkRequest request, HttpServletRequest httpRequest) {
        if (!IpAddressUtils.isValidCidr(request.getIpRange())) {
            return ResponseResult.error("IP地址段格式不正确，请使用CIDR格式 (如: 192.168.1.0/24)");
        }
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        
        SysOfficeNetwork network = new SysOfficeNetwork();
        network.setIpRange(request.getIpRange());
        network.setDescription(request.getDescription());
        network.setNetworkType(request.getNetworkType() != null ? request.getNetworkType() : 1);
        network.setStatus(1);
        network.setOperatorId(userId);
        
        try {
            networkAccessService.addOfficeNetwork(network);
            log.info("添加办公网IP白名单: {}, 操作人: {}", request.getIpRange(), userId);
            return ResponseResult.successMessage("添加成功");
        } catch (Exception e) {
            log.error("添加办公网IP白名单失败", e);
            return ResponseResult.errorMessage("添加失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/update")
    public ResponseResult<Void> update(@RequestBody SysOfficeNetwork network) {
        if (!IpAddressUtils.isValidCidr(network.getIpRange())) {
            return ResponseResult.error("IP地址段格式不正确");
        }
        
        SysOfficeNetwork exists = sysOfficeNetworkMapper.selectById(network.getId());
        if (exists == null) {
            return ResponseResult.error("记录不存在");
        }
        
        try {
            networkAccessService.updateOfficeNetwork(network);
            log.info("更新办公网IP白名单: {}", network.getIpRange());
            return ResponseResult.successMessage("更新成功");
        } catch (Exception e) {
            log.error("更新办公网IP白名单失败", e);
            return ResponseResult.errorMessage("更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        SysOfficeNetwork exists = sysOfficeNetworkMapper.selectById(id);
        if (exists == null) {
            return ResponseResult.error("记录不存在");
        }
        
        try {
            networkAccessService.deleteOfficeNetwork(id);
            log.info("删除办公网IP白名单: {}", exists.getIpRange());
            return ResponseResult.successMessage("删除成功");
        } catch (Exception e) {
            log.error("删除办公网IP白名单失败", e);
            return ResponseResult.errorMessage("删除失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/enable/{id}")
    public ResponseResult<Void> enable(@PathVariable Long id) {
        SysOfficeNetwork network = sysOfficeNetworkMapper.selectById(id);
        if (network == null) {
            return ResponseResult.error("记录不存在");
        }
        
        network.setStatus(1);
        sysOfficeNetworkMapper.updateById(network);
        networkAccessService.refreshCache();
        
        log.info("启用办公网IP白名单: {}", network.getIpRange());
        return ResponseResult.successMessage("启用成功");
    }
    
    @PostMapping("/disable/{id}")
    public ResponseResult<Void> disable(@PathVariable Long id) {
        SysOfficeNetwork network = sysOfficeNetworkMapper.selectById(id);
        if (network == null) {
            return ResponseResult.error("记录不存在");
        }
        
        network.setStatus(0);
        sysOfficeNetworkMapper.updateById(network);
        networkAccessService.refreshCache();
        
        log.info("禁用办公网IP白名单: {}", network.getIpRange());
        return ResponseResult.successMessage("禁用成功");
    }
    
    @GetMapping("/test/{ip}")
    public ResponseResult<java.util.Map<String, Object>> testIp(@PathVariable String ip) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("ip", ip);
        result.put("isValid", IpAddressUtils.isValidIp(ip));
        result.put("isOfficeNetwork", networkAccessService.isOfficeNetwork(ip));
        result.put("isPrivateNetwork", networkAccessService.isPrivateNetwork(ip));
        
        return ResponseResult.success(result);
    }
}