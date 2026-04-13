package com.company.bbs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.bbs.audit.CompositeSensitiveWordFilter;
import com.company.bbs.audit.FilterResult;
import com.company.bbs.entity.BbsAuditLog;
import com.company.bbs.entity.BbsPost;
import com.company.bbs.mapper.BbsAuditLogMapper;
import com.company.bbs.mapper.BbsPostMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Slf4j
@Service
public class AuditService {
    
    @Autowired
    private BbsAuditLogMapper auditLogMapper;
    
    @Autowired
    private BbsPostMapper postMapper;
    
    @Autowired
    private CompositeSensitiveWordFilter sensitiveWordFilter;
    
    @Autowired
    private AuthService authService;
    
    private static final int CONTENT_SNIPPET_LENGTH = 200;
    
    @Async
    @Transactional
    public void submitForAudit(Long contentId, String contentType, Long userId, String contentText) {
        try {
            log.info("提交审核 - 类型: {}, 内容ID: {}, 用户: {}", contentType, contentId, userId);
            
            FilterResult filterResult = sensitiveWordFilter.analyzeText(contentText);
            
            BbsAuditLog auditLog = new BbsAuditLog();
            auditLog.setContentType(contentType);
            auditLog.setContentId(contentId);
            auditLog.setUserId(userId);
            auditLog.setAuditStatus(1);
            
            if (filterResult.getMatchedWords() != null && !filterResult.getMatchedWords().isEmpty()) {
                String sensitiveWords = String.join(",", filterResult.getMatchedWords());
                auditLog.setSensitiveWords(sensitiveWords);
            }
            
            String snippet = contentText.length() > CONTENT_SNIPPET_LENGTH 
                ? contentText.substring(0, CONTENT_SNIPPET_LENGTH) + "..." 
                : contentText;
            auditLog.setContentSnippet(snippet);
            
            auditLogMapper.insert(auditLog);
            
            updateContentAuditStatus(contentId, contentType, 1);
            
            log.info("审核提交成功 - 日志ID: {}", auditLog.getId());
        } catch (Exception e) {
            log.error("提交审核失败", e);
            throw new RuntimeException("提交审核失败", e);
        }
    }
    
    @Transactional
    public void approve(Long auditLogId, Long auditorId, String remark) {
        try {
            BbsAuditLog auditLog = auditLogMapper.selectById(auditLogId);
            if (auditLog == null) {
                throw new RuntimeException("审核记录不存在");
            }
            
            if (auditLog.getAuditStatus() != 1) {
                throw new RuntimeException("该内容已被审核");
            }
            
            auditLog.setAuditStatus(2);
            auditLog.setAuditorId(auditorId);
            auditLog.setAuditRemark(remark);
            auditLog.setAuditedTime(java.time.LocalDateTime.now());
            auditLogMapper.updateById(auditLog);
            
            publishContent(auditLog.getContentId(), auditLog.getContentType());
            
            log.info("审核通过 - 日志ID: {}, 内容ID: {}, 审核人: {}", 
                auditLogId, auditLog.getContentId(), auditorId);
        } catch (Exception e) {
            log.error("审核通过失败", e);
            throw new RuntimeException("审核通过失败", e);
        }
    }
    
    @Transactional
    public void reject(Long auditLogId, Long auditorId, String remark) {
        try {
            BbsAuditLog auditLog = auditLogMapper.selectById(auditLogId);
            if (auditLog == null) {
                throw new RuntimeException("审核记录不存在");
            }
            
            if (auditLog.getAuditStatus() != 1) {
                throw new RuntimeException("该内容已被审核");
            }
            
            auditLog.setAuditStatus(3);
            auditLog.setAuditorId(auditorId);
            auditLog.setAuditRemark(remark);
            auditLog.setAuditedTime(java.time.LocalDateTime.now());
            auditLogMapper.updateById(auditLog);
            
            rejectContent(auditLog.getContentId(), auditLog.getContentType(), remark);
            
            log.info("审核驳回 - 日志ID: {}, 内容ID: {}, 审核人: {}, 原因: {}", 
                auditLogId, auditLog.getContentId(), auditorId, remark);
        } catch (Exception e) {
            log.error("审核驳回失败", e);
            throw new RuntimeException("审核驳回失败", e);
        }
    }
    
    public Page<BbsAuditLog> getPendingAudits(Integer current, Integer size) {
        Page<BbsAuditLog> page = new Page<>(current, size);
        LambdaQueryWrapper<BbsAuditLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BbsAuditLog::getAuditStatus, 1)
               .orderByAsc(BbsAuditLog::getCreatedTime);
        return auditLogMapper.selectPage(page, wrapper);
    }
    
    public Page<BbsAuditLog> getAuditHistory(Long userId, Integer current, Integer size) {
        Page<BbsAuditLog> page = new Page<>(current, size);
        LambdaQueryWrapper<BbsAuditLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BbsAuditLog::getUserId, userId)
               .orderByDesc(BbsAuditLog::getCreatedTime);
        return auditLogMapper.selectPage(page, wrapper);
    }
    
    public AuditStatistics getStatistics() {
        AuditStatistics stats = new AuditStatistics();
        
        Long totalCount = auditLogMapper.selectCount(
            new LambdaQueryWrapper<BbsAuditLog>()
        );
        
        Long pendingCount = auditLogMapper.selectCount(
            new LambdaQueryWrapper<BbsAuditLog>()
                .eq(BbsAuditLog::getAuditStatus, 1)
        );
        
        Long approvedCount = auditLogMapper.selectCount(
            new LambdaQueryWrapper<BbsAuditLog>()
                .eq(BbsAuditLog::getAuditStatus, 2)
        );
        
        Long rejectedCount = auditLogMapper.selectCount(
            new LambdaQueryWrapper<BbsAuditLog>()
                .eq(BbsAuditLog::getAuditStatus, 3)
        );
        
        stats.setTotalCount(totalCount != null ? totalCount : 0);
        stats.setPendingCount(pendingCount != null ? pendingCount : 0);
        stats.setApprovedCount(approvedCount != null ? approvedCount : 0);
        stats.setRejectedCount(rejectedCount != null ? rejectedCount : 0);
        
        return stats;
    }
    
    public AuditCheckResult checkBeforeSubmit(Long userId, String content) {
        AuditCheckResult result = new AuditCheckResult();
        
        if (authService.isWhiteListUser(userId)) {
            result.setNeedAudit(false);
            result.setReason("白名单用户，豁免审核");
            result.setAuditRequired(false);
            return result;
        }
        
        FilterResult filterResult = sensitiveWordFilter.analyzeText(content);
        
        if (filterResult.isNeedAudit()) {
            result.setNeedAudit(true);
            result.setReason("包含敏感词");
            result.setMatchedWords(filterResult.getMatchedWords());
            result.setAuditRequired(true);
            if (filterResult.isHighSeverity()) {
                result.setRecommendation("建议驳回");
            } else {
                result.setRecommendation("建议人工审核");
            }
        } else {
            result.setNeedAudit(false);
            result.setReason("内容干净");
            result.setAuditRequired(false);
        }
        
        return result;
    }
    
    private void updateContentAuditStatus(Long contentId, String contentType, Integer auditStatus) {
        if ("post".equals(contentType)) {
            BbsPost post = postMapper.selectById(contentId);
            if (post != null) {
                post.setAuditStatus(auditStatus);
                if (auditStatus == 1) {
                    post.setStatus(2);
                }
                postMapper.updateById(post);
            }
        }
    }
    
    private void publishContent(Long contentId, String contentType) {
        if ("post".equals(contentType)) {
            BbsPost post = postMapper.selectById(contentId);
            if (post != null) {
                post.setAuditStatus(2);
                post.setStatus(1);
                post.setPublishedTime(java.time.LocalDateTime.now());
                postMapper.updateById(post);
                
                updateBoardPostCount(post.getBoardId(), 1);
            }
        }
    }
    
    private void rejectContent(Long contentId, String contentType, String reason) {
        if ("post".equals(contentType)) {
            BbsPost post = postMapper.selectById(contentId);
            if (post != null) {
                post.setAuditStatus(3);
                post.setStatus(3);
                post.setAuditRemark(reason);
                postMapper.updateById(post);
            }
        }
    }
    
    private void updateBoardPostCount(Long boardId, int delta) {
        
    }
    
    public static class AuditStatistics {
        private Long totalCount;
        private Long pendingCount;
        private Long approvedCount;
        private Long rejectedCount;
        
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        public Long getPendingCount() { return pendingCount; }
        public void setPendingCount(Long pendingCount) { this.pendingCount = pendingCount; }
        public Long getApprovedCount() { return approvedCount; }
        public void setApprovedCount(Long approvedCount) { this.approvedCount = approvedCount; }
        public Long getRejectedCount() { return rejectedCount; }
        public void setRejectedCount(Long rejectedCount) { this.rejectedCount = rejectedCount; }
    }
    
    public static class AuditCheckResult {
        private boolean needAudit;
        private boolean auditRequired;
        private String reason;
        private String recommendation;
        private java.util.List<String> matchedWords;
        
        public boolean isNeedAudit() { return needAudit; }
        public void setNeedAudit(boolean needAudit) { this.needAudit = needAudit; }
        public boolean isAuditRequired() { return auditRequired; }
        public void setAuditRequired(boolean auditRequired) { this.auditRequired = auditRequired; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
        public java.util.List<String> getMatchedWords() { return matchedWords; }
        public void setMatchedWords(java.util.List<String> matchedWords) { this.matchedWords = matchedWords; }
    }
}