package com.company.bbs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.bbs.entity.SysOfficeNetwork;
import com.company.bbs.mapper.SysOfficeNetworkMapper;
import com.company.bbs.utils.IpAddressUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class NetworkAccessService {
    
    @Autowired
    private SysOfficeNetworkMapper sysOfficeNetworkMapper;
    
    private final List<String> ipRangesCache = new CopyOnWriteArrayList<>();
    
    @PostConstruct
    public void init() {
        loadIpRanges();
    }
    
    @Cacheable(value = "officeIps", key = "'all'")
    public List<String> getAllIpRanges() {
        return sysOfficeNetworkMapper.findAllEnabledIpRanges();
    }
    
    public void loadIpRanges() {
        try {
            List<String> ipRanges = getAllIpRanges();
            ipRangesCache.clear();
            ipRangesCache.addAll(ipRanges);
            log.info("加载办公网IP白名单成功，共{}条", ipRanges.size());
        } catch (Exception e) {
            log.error("加载办公网IP白名单失败", e);
        }
    }
    
    public boolean isOfficeNetwork(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        
        String cleanIp = IpAddressUtils.extractClientIp(ip);
        
        if (!IpAddressUtils.isValidIp(cleanIp)) {
            log.warn("无效的IP地址: {}", ip);
            return false;
        }
        
        for (String ipRange : ipRangesCache) {
            if (IpAddressUtils.isIpInCidr(cleanIp, ipRange)) {
                log.debug("IP {} 在办公网范围内: {}", cleanIp, ipRange);
                return true;
            }
        }
        
        log.debug("IP {} 不在任何办公网范围内", cleanIp);
        return false;
    }
    
    public boolean isPrivateNetwork(String ip) {
        return IpAddressUtils.isPrivateIp(ip) || IpAddressUtils.isLocalhost(ip);
    }
    
    @CacheEvict(value = "officeIps", allEntries = true)
    public void addOfficeNetwork(SysOfficeNetwork network) {
        sysOfficeNetworkMapper.insert(network);
        loadIpRanges();
    }
    
    @CacheEvict(value = "officeIps", allEntries = true)
    public void updateOfficeNetwork(SysOfficeNetwork network) {
        sysOfficeNetworkMapper.updateById(network);
        loadIpRanges();
    }
    
    @CacheEvict(value = "officeIps", allEntries = true)
    public void deleteOfficeNetwork(Long id) {
        sysOfficeNetworkMapper.deleteById(id);
        loadIpRanges();
    }
    
    @CacheEvict(value = "officeIps", allEntries = true)
    public void refreshCache() {
        loadIpRanges();
    }
}