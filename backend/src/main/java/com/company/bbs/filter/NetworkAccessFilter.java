package com.company.bbs.filter;

import com.company.bbs.service.NetworkAccessService;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Order(1)
public class NetworkAccessFilter extends OncePerRequestFilter {
    
    @Autowired
    private NetworkAccessService networkAccessService;
    
    @Value("${network.enable-office-ip-check:true}")
    private Boolean enableOfficeIpCheck;
    
    private static final List<String> WHITELIST_PATHS = Arrays.asList(
        "/health",
        "/auth/wechat",
        "/auth/login",
        "/auth/login-by-password",
        "/auth/info",
        "/swagger-ui",
        "/swagger-resources",
        "/v3/api-docs",
        "/api/auth",
        "/api/posts",
        "/api/admin"
    );
    
    @Value("${network.exterior-access-paths:/home,/public}")
    private String exteriorAccessPaths;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String requestUri = request.getRequestURI();
        String clientIp = getClientIp(request);
        
        if (isWhitelistPath(requestUri, clientIp)) {
            log.debug("白名单路径放行: {}", requestUri);
            filterChain.doFilter(request, response);
            return;
        }
        
        if (!enableOfficeIpCheck) {
            log.debug("IP白名单检查已禁用，放行所有请求");
            filterChain.doFilter(request, response);
            return;
        }
        
        boolean isOfficeNetwork = networkAccessService.isOfficeNetwork(clientIp);
        boolean isExteriorPath = isExteriorAccessPath(requestUri);
        boolean isWeChatRequest = isWeChatWorkbenchRequest(request);
        
        if (isOfficeNetwork || isExteriorPath || isWeChatRequest) {
            
            request.setAttribute("ipType", isOfficeNetwork ? "office" : "exterior");
            request.setAttribute("clientIp", clientIp);
            
            log.info("访问放行 - IP: {}, URI: {}, 类型: {}", 
                clientIp, requestUri, 
                isOfficeNetwork ? "办公网" : (isExteriorPath ? "外部路径" : "企业微信"));
            
            filterChain.doFilter(request, response);
        } else {
            
            log.warn("访问被拒绝 - IP: {}, URI: {}", clientIp, requestUri);
            
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(String.format(
                "{\"code\":403,\"message\":\"访问被拒绝，仅允许办公网或通过企业微信工作台访问\"}"
            ));
        }
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return com.company.bbs.utils.IpAddressUtils.extractClientIp(ip);
    }
    
    private boolean isWhitelistPath(String requestUri, String clientIp) {
        if (WHITELIST_PATHS.stream().anyMatch(requestUri::startsWith)) {
            return true;
        }
        if ("0:0:0:0:0:0:0:1".equals(clientIp) || "127.0.0.1".equals(clientIp) || "localhost".equalsIgnoreCase(clientIp) || "0.0.0.0".equals(clientIp)) {
            return true;
        }
        return false;
    }
    
    private boolean isExteriorAccessPath(String requestUri) {
        if (!StringUtils.hasText(exteriorAccessPaths)) {
            return false;
        }
        String[] paths = exteriorAccessPaths.split(",");
        return Arrays.stream(paths)
            .map(String::trim)
            .anyMatch(requestUri::startsWith);
    }
    
    private boolean isWeChatWorkbenchRequest(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (!StringUtils.hasText(userAgent)) {
            return false;
        }
        return userAgent.contains("wxwork") || 
               userAgent.contains("MicroMessenger") ||
               userAgent.contains("EnterpriseWechat");
    }
}