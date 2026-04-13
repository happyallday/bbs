package com.company.bbs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.bbs.audit.FilterResult;
import com.company.bbs.dto.ResponseResult;
import com.company.bbs.dto.SensitiveWordRequest;
import com.company.bbs.entity.BbsSensitiveWord;
import com.company.bbs.service.SensitiveWordService;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/sensitive-words")
@PreAuthorize("hasRole('ADMIN')")
public class SensitiveWordController {
    
    @Autowired
    private SensitiveWordService sensitiveWordService;
    
    @GetMapping("/list")
    public ResponseResult<Page<BbsSensitiveWord>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            SensitiveWordRequest request) {
        
        Page<BbsSensitiveWord> page = sensitiveWordService.list(current, size, request);
        return ResponseResult.success(page);
    }
    
    @GetMapping("/categories")
    public ResponseResult<List<String>> getCategories() {
        List<String> categories = sensitiveWordService.getAllCategories();
        return ResponseResult.success(categories);
    }
    
    @GetMapping("/{id}")
    public ResponseResult<BbsSensitiveWord> getById(@PathVariable Long id) {
        BbsSensitiveWord word = sensitiveWordService.getById(id);
        if (word == null) {
            return ResponseResult.error("记录不存在");
        }
        return ResponseResult.success(word);
    }
    
    @PostMapping("/add")
    public ResponseResult<Void> add(@Validated(SensitiveWordRequest.Create.class) @RequestBody SensitiveWordRequest request, 
                                     HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            sensitiveWordService.add(request, userId);
            return ResponseResult.<Void>success("添加成功");
        } catch (Exception e) {
            log.error("添加敏感词失败", e);
            return ResponseResult.<Void>error("添加失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/update")
    public ResponseResult<Void> update(@RequestBody SensitiveWordRequest request, 
                                        HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            sensitiveWordService.update(request, userId);
            return ResponseResult.<Void>success("更新成功");
        } catch (Exception e) {
            log.error("更新敏感词失败", e);
            return ResponseResult.<Void>error("更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id, 
                                        HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            sensitiveWordService.delete(id, userId);
            return ResponseResult.<Void>success("删除成功");
        } catch (Exception e) {
            log.error("删除敏感词失败", e);
            return ResponseResult.<Void>error("删除失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/test")
    public ResponseResult<FilterResult> test(@RequestBody Map<String, String> data) {
        String text = data.get("text");
        String category = data.get("category");
        
        if (text == null || text.isEmpty()) {
            return ResponseResult.error("测试文本不能为空");
        }
        
        FilterResult result;
        if (category != null && !category.isEmpty()) {
            result = sensitiveWordService.filterText(text, category);
        } else {
            result = sensitiveWordService.filterText(text);
        }
        
        return ResponseResult.success(result);
    }
    
    @PostMapping("/batch-import")
    public ResponseResult<Map<String, Object>> batchImport(@RequestBody Map<String, Object> data, 
                                                             HttpServletRequest httpRequest) {
        try {
            List<String> words = (List<String>) data.get("words");
            String category = (String) data.get("category");
            Integer wordType = data.get("wordType") != null ? 
                Integer.valueOf(data.get("wordType").toString()) : null;
            
            if (words == null || words.isEmpty()) {
                return ResponseResult.error("敏感词列表不能为空");
            }
            
            Long userId = (Long) httpRequest.getAttribute("userId");
            sensitiveWordService.batchImport(words, category, wordType, userId);
            
            Long totalWordCount = (long) sensitiveWordService.getTotalWordCount();
            Map<String, Object> result = Map.of(
                "successCount", words.size(),
                "totalWordCount", totalWordCount
            );
            
            return ResponseResult.success("导入成功", result);
        } catch (Exception e) {
            log.error("批量导入敏感词失败", e);
            return ResponseResult.error("导入失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/reload")
    public ResponseResult<Void> reload() {
        try {
            sensitiveWordService.reloadFilter();
            return ResponseResult.<Void>success("重新加载成功");
        } catch (Exception e) {
            log.error("重新加载敏感词过滤器失败", e);
            return ResponseResult.<Void>error("重新加载失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/stats")
    public ResponseResult<Map<String, Object>> stats() {
        int totalWords = sensitiveWordService.getTotalWordCount();
        Map<String, Object> stats = Map.of(
            "totalWords", totalWords,
            "status", "active"
        );
        return ResponseResult.success(stats);
    }
}