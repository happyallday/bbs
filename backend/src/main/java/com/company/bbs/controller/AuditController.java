package com.company.bbs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.bbs.dto.ResponseResult;
import com.company.bbs.entity.BbsAuditLog;
import com.company.bbs.service.AuditService;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/audit")
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {
    
    @Autowired
    private AuditService auditService;
    
    @GetMapping("/pending")
    public ResponseResult<Page<BbsAuditLog>> getPendingAudits(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Page<BbsAuditLog> page = auditService.getPendingAudits(current, size);
        return ResponseResult.success(page);
    }
    
    @GetMapping("/history")
    public ResponseResult<Page<BbsAuditLog>> getAuditHistory(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Page<BbsAuditLog> page = auditService.getAuditHistory(userId, current, size);
        return ResponseResult.success(page);
    }
    
    @PostMapping("/approve/{auditLogId}")
    public ResponseResult<Void> approve(
            @PathVariable Long auditLogId,
            @RequestBody(required = false) Map<String, String> data,
            HttpServletRequest httpRequest) {
        
        try {
            Long auditorId = (Long) httpRequest.getAttribute("userId");
            String remark = data != null ? data.get("remark") : "";
            auditService.approve(auditLogId, auditorId, remark);
            return ResponseResult.<Void>success("审核通过");
        } catch (Exception e) {
            log.error("审核通过失败", e);
            return ResponseResult.<Void>error("审核通过失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/reject/{auditLogId}")
    public ResponseResult<Void> reject(
            @PathVariable Long auditLogId,
            @RequestBody Map<String, String> data,
            HttpServletRequest httpRequest) {
        
        try {
            Long auditorId = (Long) httpRequest.getAttribute("userId");
            String remark = data != null && data.containsKey("remark") ? data.get("remark") : "内容不符合发布要求";
            auditService.reject(auditLogId, auditorId, remark);
            return ResponseResult.<Void>success("审核驳回");
        } catch (Exception e) {
            log.error("审核驳回失败", e);
            return ResponseResult.<Void>error("审核驳回失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/statistics")
    public ResponseResult<AuditService.AuditStatistics> getStatistics() {
        AuditService.AuditStatistics stats = auditService.getStatistics();
        return ResponseResult.success(stats);
    }
    
    @PostMapping("/check")
    public ResponseResult<AuditService.AuditCheckResult> checkBeforeSubmit(
            @RequestBody Map<String, Object> data,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            String content = (String) data.get("content");
            
            if (content == null || content.isEmpty()) {
                return ResponseResult.error("内容不能为空");
            }
            
            AuditService.AuditCheckResult result = auditService.checkBeforeSubmit(userId, content);
            return ResponseResult.success(result);
        } catch (Exception e) {
            log.error("审核检查失败", e);
            return ResponseResult.error("审核检查失败: " + e.getMessage());
        }
    }
}