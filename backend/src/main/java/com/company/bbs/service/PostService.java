package com.company.bbs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.bbs.audit.CompositeSensitiveWordFilter;
import com.company.bbs.audit.FilterResult;
import com.company.bbs.dto.CreatePostRequest;
import com.company.bbs.entity.BbsBoard;
import com.company.bbs.entity.BbsPost;
import com.company.bbs.mapper.BbsPostMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class PostService {
    
    @Autowired
    private BbsPostMapper postMapper;
    
    @Autowired
    private BoardService boardService;
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private CompositeSensitiveWordFilter sensitiveWordFilter;
    
    @Transactional
    public BbsPost createPost(CreatePostRequest request, Long userId) {
        
        BbsBoard board = boardService.getById(request.getBoardId());
        if (board == null) {
            throw new RuntimeException("板块不存在");
        }
        if (board.getStatus() != 1) {
            throw new RuntimeException("板块已禁用");
        }
        
        AuditService.AuditCheckResult auditCheck = auditService.checkBeforeSubmit(userId, request.getContent());
        
        String postNumber = generatePostNumber();
        
        BbsPost post = new BbsPost();
        post.setPostNumber(postNumber);
        post.setUserId(userId);
        post.setBoardId(request.getBoardId());
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setSummary(generateSummary(request.getContent()));
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setReplyCount(0);
        post.setCollectCount(0);
        post.setIsTop(0);
        post.setIsEssence(0);
        
        if (auditCheck.isAuditRequired()) {
            post.setStatus(2);
            post.setAuditStatus(1);
            post.setSensitiveWords(String.join(",", auditCheck.getMatchedWords()));
        } else {
            post.setStatus(1);
            post.setAuditStatus(2);
            post.setPublishedTime(LocalDateTime.now());
        }
        
        postMapper.insert(post);
        
        if (auditCheck.isAuditRequired()) {
            auditService.submitForAudit(post.getId(), "post", userId, request.getContent());
        } else {
            boardService.incrementPostCount(request.getBoardId());
        }
        
        log.info("创建帖子成功 - 编号: {}, 用户: {}, 需要审核: {}", 
            postNumber, userId, auditCheck.isAuditRequired());
        
        return post;
    }
    
    public Page<BbsPost> getPosts(Integer current, Integer size, Long boardId, Integer status) {
        Page<BbsPost> page = new Page<>(current, size);
        LambdaQueryWrapper<BbsPost> wrapper = new LambdaQueryWrapper<>();
        
        if (boardId != null) {
            wrapper.eq(BbsPost::getBoardId, boardId);
        }
        if (status != null) {
            wrapper.eq(BbsPost::getStatus, status);
        } else {
            wrapper.eq(BbsPost::getStatus, 1);
        }
        
        wrapper.orderByDesc(BbsPost::getIsTop)
               .orderByDesc(BbsPost::getPublishedTime);
        
        return postMapper.selectPage(page, wrapper);
    }
    
    public BbsPost getPostByNumber(String postNumber) {
        return postMapper.selectOne(
            new LambdaQueryWrapper<BbsPost>()
                .eq(BbsPost::getPostNumber, postNumber)
        );
    }
    
    public BbsPost getPostById(Long id) {
        return postMapper.selectById(id);
    }
    
    @Transactional
    public void incrementViewCount(Long postId) {
        BbsPost post = postMapper.selectById(postId);
        if (post != null) {
            post.setViewCount(post.getViewCount() + 1);
            postMapper.updateById(post);
        }
    }
    
    @Transactional
    public void incrementLikeCount(Long postId) {
        BbsPost post = postMapper.selectById(postId);
        if (post != null) {
            post.setLikeCount(post.getLikeCount() + 1);
            postMapper.updateById(post);
        }
    }
    
    @Transactional
    public void decrementLikeCount(Long postId) {
        BbsPost post = postMapper.selectById(postId);
        if (post != null && post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
            postMapper.updateById(post);
        }
    }
    
    @Transactional
    public void incrementCollectCount(Long postId) {
        BbsPost post = postMapper.selectById(postId);
        if (post != null) {
            post.setCollectCount(post.getCollectCount() + 1);
            postMapper.updateById(post);
        }
    }
    
    @Transactional
    public void decrementCollectCount(Long postId) {
        BbsPost post = postMapper.selectById(postId);
        if (post != null && post.getCollectCount() > 0) {
            post.setCollectCount(post.getCollectCount() - 1);
            postMapper.updateById(post);
        }
    }
    
    private String generatePostNumber() {
        String prefix = "P";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", (int)(Math.random() * 10000));
        return prefix + timestamp + random;
    }
    
    private String generateSummary(String content) {
        if (content == null || content.length() <= 200) {
            return content;
        }
        String plainText = content.replaceAll("<[^>]*>", "").trim();
        if (plainText.length() > 200) {
            return plainText.substring(0, 200) + "...";
        }
        return plainText;
    }
    
    public Page<BbsPost> getHotPosts(Integer current, Integer size) {
        Page<BbsPost> page = new Page<>(current, size);
        LambdaQueryWrapper<BbsPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BbsPost::getStatus, 1)
               .eq(BbsPost::getAuditStatus, 2)
               .orderByDesc(BbsPost::getViewCount)
               .orderByDesc(BbsPost::getLikeCount);
        return postMapper.selectPage(page, wrapper);
    }
    
    public Page<BbsPost> getRecentPosts(Integer current, Integer size) {
        Page<BbsPost> page = new Page<>(current, size);
        LambdaQueryWrapper<BbsPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BbsPost::getStatus, 1)
               .eq(BbsPost::getAuditStatus, 2)
               .orderByDesc(BbsPost::getPublishedTime);
        return postMapper.selectPage(page, wrapper);
    }
}