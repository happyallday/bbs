package com.company.bbs.controller;

import com.company.bbs.dto.ResponseResult;
import com.company.bbs.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/statistics")
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    @GetMapping("/overall")
    public ResponseResult<Map<String, Object>> getOverallStatistics() {
        try {
            Map<String, Object> stats = statisticsService.getOverallStatistics();
            return ResponseResult.success(stats);
        } catch (Exception e) {
            log.error("获取总体统计失败", e);
            return ResponseResult.error("获取总体统计失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/post-trend")
    public ResponseResult<Map<String, Object>> getPostTrend(
            @RequestParam(defaultValue = "7") Integer days) {
        
        try {
            Map<String, Object> trend = statisticsService.getPostTrend(days);
            return ResponseResult.success(trend);
        } catch (Exception e) {
            log.error("获取帖子趋势失败", e);
            return ResponseResult.error("获取帖子趋势失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/boards")
    public ResponseResult<List<Map<String, Object>>> getBoardStatistics() {
        try {
            List<Map<String, Object>> boardStats = statisticsService.getBoardStatistics();
            return ResponseResult.success(boardStats);
        } catch (Exception e) {
            log.error("获取板块统计失败", e);
            return ResponseResult.error("获取板块统计失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/top-posts")
    public ResponseResult<List<Map<String, Object>>> getTopPosts(
            @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<Map<String, Object>> topPosts = statisticsService.getTopPosts(limit);
            return ResponseResult.success(topPosts);
        } catch (Exception e) {
            log.error("获取热门帖子失败", e);
            return ResponseResult.error("获取热门帖子失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/active-users")
    public ResponseResult<List<Map<String, Object>>> getActiveUsers(
            @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<Map<String, Object>> activeUsers = statisticsService.getActiveUsers(limit);
            return ResponseResult.success(activeUsers);
        } catch (Exception e) {
            log.error("获取活跃用户失败", e);
            return ResponseResult.error("获取活跃用户失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseResult<Map<String, Object>> getUserActivityStatistics(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = statisticsService.getUserActivityStatistics(userId);
            return ResponseResult.success(stats);
        } catch (Exception e) {
            log.error("获取用户活跃度统计失败", e);
            return ResponseResult.error("获取用户活跃度统计失败: " + e.getMessage());
        }
    }
}