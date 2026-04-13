package com.company.bbs.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
public class IpAddressUtils {
    
    private static final Pattern IPV4_PATTERN = Pattern.compile(
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );
    
    private static final Pattern CIDR_PATTERN = Pattern.compile(
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)/(\\d{1,2})$"
    );
    
    public static boolean isValidIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        return IPV4_PATTERN.matcher(ip).matches();
    }
    
    public static boolean isValidCidr(String cidr) {
        if (cidr == null || cidr.isEmpty()) {
            return false;
        }
        return CIDR_PATTERN.matcher(cidr).matches();
    }
    
    public static boolean isIpInCidr(String ip, String cidr) {
        if (!isValidIp(ip) || !isValidCidr(cidr)) {
            return false;
        }
        
        try {
            String[] cidrParts = cidr.split("/");
            String networkIp = cidrParts[0];
            int prefixLength = Integer.parseInt(cidrParts[1]);
            
            if (prefixLength < 0 || prefixLength > 32) {
                return false;
            }
            
            long ipNumeric = ipToLong(ip);
            long networkNumeric = ipToLong(networkIp);
            long mask = 0xFFFFFFFFL << (32 - prefixLength);
            
            return (ipNumeric & mask) == (networkNumeric & mask);
        } catch (Exception e) {
            log.error("CIDR匹配失败: ip={}, cidr={}", ip, cidr, e);
            return false;
        }
    }
    
    public static long ipToLong(String ip) {
        String[] parts = ip.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = result * 256 + Long.parseLong(parts[i]);
        }
        return result & 0xFFFFFFFFL;
    }
    
    public static String longToIp(long ipLong) {
        StringBuilder sb = new StringBuilder();
        sb.append((ipLong >> 24) & 0xFF).append(".");
        sb.append((ipLong >> 16) & 0xFF).append(".");
        sb.append((ipLong >> 8) & 0xFF).append(".");
        sb.append(ipLong & 0xFF);
        return sb.toString();
    }
    
    public static String extractClientIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "0.0.0.0";
        }
        
        int index = ip.indexOf(',');
        if (index > 0) {
            return ip.substring(0, index).trim();
        }
        return ip.trim();
    }
    
    public static boolean isPrivateIp(String ip) {
        if (!isValidIp(ip)) {
            return false;
        }
        
        long ipLong = ipToLong(ip);
        
        return (ipLong >= 0x0A000000L && ipLong <= 0x0AFFFFFFL) ||
               (ipLong >= 0xAC100000L && ipLong <= 0xAC1FFFFFL) ||
               (ipLong >= 0xC0A80000L && ipLong <= 0xC0A8FFFFL) ||
               ipLong == 0x7F000001L;
    }
    
    public static boolean isLocalhost(String ip) {
        return "127.0.0.1".equals(ip) || "localhost".equalsIgnoreCase(ip) || "::1".equals(ip);
    }
}