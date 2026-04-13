package com.company.bbs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.bbs.entity.BbsBoard;
import com.company.bbs.mapper.BbsBoardMapper;
import com.company.bbs.mapper.BbsPostMapper;
import com.company.bbs.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StatisticsService {
    
    @Autowired
    private BbsPostMapper postMapper;
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private BbsBoardMapper boardMapper;
    
    @Autowired
    private AuditService auditService;
    
    public Map<String, Object> getOverallStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        Long totalPosts = postMapper.selectCount(
            new LambdaQueryWrapper<>().eq(BbsPost::getStatus, 1)
        );
        stats.put("totalPosts", totalPosts);
        
        Long totalUsers = userMapper.selectCount(
            new LambdaQueryWrapper<>().eq(com.company.bbs.entity.SysUser::getStatus, 1)
        );
        stats.put("totalUsers", totalUsers);
        
        Long totalBoards = boardMapper.selectCount(
            new LambdaQueryWrapper<>().eq(BbsBoard::getStatus, 1)
        );
        stats.put("totalBoards", totalBoards);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.withHour(0).withMinute(0).withSecond(0);
        
        Long todayPosts = postMapper.selectCount(
            new LambdaQueryWrapper<>()
                .eq(BbsPost::getStatus, 1)
                .ge(BbsPost::getPublishedTime, todayStart)
        );
        stats.put("todayPosts", todayPosts);
        
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        Long weekPosts = postMapper.selectCount(
            new LambdaQueryWrapper<>()
                .eq(BbsPost::getStatus, 1)
                .ge(BbsPost::getPublishedTime, sevenDaysAgo)
        );
        stats.put("weekPosts", weekPosts);
        
        AuditService.AuditStatistics auditStats = auditService.getStatistics();
        stats.put("pendingAudits", auditStats.getPendingCount());
        stats.put("approvedAudits", auditStats.getApprovedCount());
        stats.put("rejectedAudits", auditStats.getRejectedCount());
        
        return stats;
    }
    
    public Map<String, Object> getPostTrend(Integer days) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime endDate = LocalDateTime.now();
        
        List<Map<String, Object>> trendData = new java.util.ArrayList<>();
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime startDate = endDate.minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime finishDate = startDate.plusDays(1);
            
            Long count = postMapper.selectCount(
                new LambdaQueryWrapper<>()
                    .eq(BbsPost::getStatus, 1)
                    .ge(BbsPost::getPublishedTime, startDate)
                    .lt(BbsPost::getPublishedTime, finishDate)
            );
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", startDate.format(DateTimeFormatter.ofPattern("MM-dd")));
            dayData.put("count", count);
            trendData.add(dayData);
        }
        
        result.put("trend", trendData);
        result.put("days", days);
        
        return result;
    }
    
    public List<Map<String, Object>> getBoardStatistics() {
        List<BbsBoard> boards = boardMapper.selectList(
            new LambdaQueryWrapper<>().eq(BbsBoard::getStatus, 1)
        );
        
        List<Map<String, Object>> boardStats = new java.util.ArrayList<>();
        
        for (BbsBoard board : boards) {
            Long postCount = postMapper.selectCount(
                new LambdaQueryWrapper<>()
                    .eq(BbsPost::getBoardId, board.getId())
                    .eq(BbsPost::getStatus, 1)
            );
            
            Map<String, Object> stat = new HashMap<>();
            stat.put("boardId", board.getId());
            stat.put("boardName", board.getBoardName());
            stat.put("boardCode", board.getBoardCode());
            stat.put("postCount", postCount);
            stat.put("description", board.getDescription());
            
            boardStats.add(stat);
        }
        
        boardStats.sort((a, b) -> {
            Long countA = (Long) a.get("postCount");
            Long countB = (Long) b.get("postCount");
            return countB.compareTo(countA);
        });
        
        return boardStats;
    }
    
    public List<Map<String, Object>> getTopPosts(Integer limit) {
        List<com.company.bbs.entity.BbsPost> posts = postMapper.selectList(
            new LambdaQueryWrapper<>()
                .eq(com.company.bbs.entity.BbsPost::getStatus, 1)
                .orderByDesc(com.company.bbs.entity.BbsPost::getViewCount)
                .last("LIMIT " + limit)
        );
        
        List<Map<String, Object>> topPosts = new java.util.ArrayList<>();
        
        for (com.company.bbs.entity.BbsPost post : posts) {
            Map<String, Object> postData = new HashMap<>();
            postData.put("id", post.getId());
            postData.put("postNumber", post.getPostNumber());
            postData.put("title", post.getTitle());
            postData.put("viewCount", post.getViewCount());
            postData.put("likeCount", post.getLikeCount());
            postData.put("replyCount", post.getReplyCount());
            postData.put("boardId", post.getBoardId());
            postData.put("userId", post.getUserId());
            postData.put("publishedTime", post.getPublishedTime());
            
            topPosts.add(postData);
        }
        
        return topPosts;
    }
    
    public List<Map<String, Object>> getActiveUsers(Integer limit) {
        List<com.company.bbs.entity.SysUser> users = userMapper.selectList(
            new LambdaQueryWrapper<>()
                .eq(com.company.bbs.entity.SysUser::getStatus, 1)
                .isNotNull(com.company.bbs.entity.SysUser::getLastLoginTime)
                .orderByDesc(com.company.bbs.entity.SysUser::getLastLoginTime)
                .last("LIMIT " + limit)
        );
        
        List<Map<String, Object>> activeUsers = new java.util.ArrayList<>();
        
        for (com.company.bbs.entity.SysUser user : users) {
            Long userPostCount = postMapper.selectCount(
                new LambdaQueryWrapper<>()
                    .eq(com.company.bbs.entity.BbsPost::getUserId, user.getId())
                    .eq(com.company.bbs.entity.BbsPost::getStatus, 1)
            );
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("realName", user.getRealName());
            userData.put("department", user.getDepartment());
            userData.put("position", user.getPosition());
            userData.put("postCount", userPostCount);
            userData.put("lastLoginTime", user.getLastLoginTime());
            userData.put("lastLoginIp", user.getLastLoginIp());
            
            activeUsers.add(userData);
        }
        
        return activeUsers;
    }
    
    public Map<String, Object> getUserActivityStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        Long totalPosts = postMapper.selectCount(
            new LambdaQueryWrapper<>()
                .eq(com.company.bbs.entity.BbsPost::getUserId, userId)
        );
        stats.put("totalPosts", totalPosts);
        
        Long publishedPosts = postMapper.selectCount(
            new LambdaQueryWrapper<>()
                .eq(com.company.bbs.entity.BbsPost::getUserId, userId)
                .eq(com.company.bbs.entity.BbsPost::getStatus, 1)
        );
        stats.put("publishedPosts", publishedPosts);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthAgo = now.minusMonths(1);
        
        Long monthPosts = postMapper.selectCount(
            new LambdaQueryWrapper<>()
                .eq(com.company.bbs.entity.BbsPost::getUserId, userId)
                .eq(com.company.bbs.entity.BbsPost::getStatus, 1)
                .ge(com.company.bbs.entity.BbsPost::getPublishedTime, monthAgo)
        );
        stats.put("monthPosts", monthPosts);
        
        List<Map<String, Object>> trendData = new java.util.ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime startDate = now.minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime finishDate = startDate.plusDays(1);
            
            Long count = postMapper.selectCount(
                new LambdaQueryWrapper<>()
                    .eq(com.company.bbs.entity.BbsPost::getUserId, userId)
                    .eq(com.company.bbs.entity.BbsPost::getStatus, 1)
                    .ge(com.company.bbs.entity.BbsPost::getPublishedTime, startDate)
                    .lt(com.company.bbs.entity.BbsPost::getPublishedTime, finishDate)
            );
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", startDate.format(DateTimeFormatter.ofPattern("MM-dd")));
            dayData.put("count", count);
            trendData.add(dayData);
        }
        stats.put("trend", trendData);
        
        return stats;
    }
}