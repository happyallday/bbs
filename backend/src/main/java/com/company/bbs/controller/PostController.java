package com.company.bbs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.bbs.dto.CreatePostRequest;
import com.company.bbs.dto.ResponseResult;
import com.company.bbs.entity.BbsPost;
import com.company.bbs.service.PostService;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/posts")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    @PostMapping
    public ResponseResult<BbsPost> createPost(@RequestBody CreatePostRequest request, 
                                              HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            BbsPost post = postService.createPost(request, userId);
            
            String message = post.getAuditStatus() == 1 ? 
                "帖子发布成功，等待审核" : 
                "帖子发布成功";
            
            return ResponseResult.success(message, post);
        } catch (Exception e) {
            log.error("创建帖子失败", e);
            return ResponseResult.error("创建帖子失败: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseResult<Page<BbsPost>> getPosts(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long boardId,
            @RequestParam(required = false) Integer status) {
        
        try {
            Page<BbsPost> page = postService.getPosts(current, size, boardId, status);
            return ResponseResult.success(page);
        } catch (Exception e) {
            log.error("获取帖子列表失败", e);
            return ResponseResult.error("获取帖子列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/number/{postNumber}")
    public ResponseResult<BbsPost> getPostByNumber(@PathVariable String postNumber) {
        try {
            BbsPost post = postService.getPostByNumber(postNumber);
            if (post == null) {
                return ResponseResult.error("帖子不存在");
            }
            postService.incrementViewCount(post.getId());
            return ResponseResult.success(post);
        } catch (Exception e) {
            log.error("获取帖子详情失败", e);
            return ResponseResult.error("获取帖子详情失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseResult<BbsPost> getPostById(@PathVariable Long id) {
        try {
            BbsPost post = postService.getPostById(id);
            if (post == null) {
                return ResponseResult.error("帖子不存在");
            }
            postService.incrementViewCount(id);
            return ResponseResult.success(post);
        } catch (Exception e) {
            log.error("获取帖子详情失败", e);
            return ResponseResult.error("获取帖子详情失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/like")
    public ResponseResult<Void> likePost(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            postService.incrementLikeCount(id);
            return ResponseResult.success("点赞成功");
        } catch (Exception e) {
            log.error("点赞失败", e);
            return ResponseResult.error("点赞失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/unlike")
    public ResponseResult<Void> unlikePost(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            postService.decrementLikeCount(id);
            return ResponseResult.success("取消点赞成功");
        } catch (Exception e) {
            log.error("取消点赞失败", e);
            return ResponseResult.error("取消点赞失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/hot")
    public ResponseResult<Page<BbsPost>> getHotPosts(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            Page<BbsPost> page = postService.getHotPosts(current, size);
            return ResponseResult.success(page);
        } catch (Exception e) {
            log.error("获取热门帖子失败", e);
            return ResponseResult.error("获取热门帖子失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/recent")
    public ResponseResult<Page<BbsPost>> getRecentPosts(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            Page<BbsPost> page = postService.getRecentPosts(current, size);
            return ResponseResult.success(page);
        } catch (Exception e) {
            log.error("获取最新帖子失败", e);
            return ResponseResult.error("获取最新帖子失败: " + e.getMessage());
        }
    }
}