package com.company.bbs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.bbs.audit.CompositeSensitiveWordFilter;
import com.company.bbs.audit.FilterResult;
import com.company.bbs.dto.SensitiveWordRequest;
import com.company.bbs.entity.BbsSensitiveWord;
import com.company.bbs.mapper.BbsSensitiveWordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class SensitiveWordService {
    
    @Autowired
    private BbsSensitiveWordMapper sensitiveWordMapper;
    
    @Autowired
    private CompositeSensitiveWordFilter compositeFilter;
    
    public Page<BbsSensitiveWord> list(
            Integer current, 
            Integer size, 
            SensitiveWordRequest request) {
        
        Page<BbsSensitiveWord> page = new Page<>(current, size);
        LambdaQueryWrapper<BbsSensitiveWord> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(request.getWord())) {
            wrapper.like(BbsSensitiveWord::getWord, request.getWord());
        }
        if (StringUtils.hasText(request.getCategory())) {
            wrapper.eq(BbsSensitiveWord::getCategory, request.getCategory());
        }
        if (request.getWordType() != null) {
            wrapper.eq(BbsSensitiveWord::getWordType, request.getWordType());
        }
        if (request.getSeverity() != null) {
            wrapper.eq(BbsSensitiveWord::getSeverity, request.getSeverity());
        }
        if (request.getStatus() != null) {
            wrapper.eq(BbsSensitiveWord::getStatus, request.getStatus());
        }
        
        wrapper.orderByDesc(BbsSensitiveWord::getCreatedTime);
        
        return sensitiveWordMapper.selectPage(page, wrapper);
    }
    
    public BbsSensitiveWord getById(Long id) {
        return sensitiveWordMapper.selectById(id);
    }
    
    public List<String> getAllCategories() {
        return sensitiveWordMapper.selectObjs(
            new LambdaQueryWrapper<BbsSensitiveWord>()
                .select(BbsSensitiveWord::getCategory)
                .groupBy(BbsSensitiveWord::getCategory)
                .eq(BbsSensitiveWord::getStatus, 1),
            Object::toString
        );
    }
    
    @CacheEvict(value = {"sensitiveWords", "regexSensitiveWords"}, allEntries = true)
    public void add(SensitiveWordRequest request, Long operatorId) {
        if (!StringUtils.hasText(request.getWord())) {
            throw new IllegalArgumentException("敏感词不能为空");
        }
        
        BbsSensitiveWord word = new BbsSensitiveWord();
        word.setWord(request.getWord());
        word.setWordType(request.getWordType() != null ? request.getWordType() : 1);
        word.setCategory(StringUtils.hasText(request.getCategory()) ? request.getCategory() : "default");
        word.setSeverity(request.getSeverity() != null ? request.getSeverity() : 1);
        word.setIsRegex(request.getIsRegex() != null ? request.getIsRegex() : 0);
        word.setHitCount(0);
        word.setStatus(1);
        
        sensitiveWordMapper.insert(word);
        
        log.info("添加敏感词: {} (类型: {}, 正则: {}), 操作人: {}", 
            request.getWord(), request.getWordType(), request.getIsRegex(), operatorId);
    }
    
    @CacheEvict(value = {"sensitiveWords", "regexSensitiveWords"}, allEntries = true)
    public void update(SensitiveWordRequest request, Long operatorId) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("ID不能为空");
        }
        
        BbsSensitiveWord exists = sensitiveWordMapper.selectById(request.getId());
        if (exists == null) {
            throw new IllegalArgumentException("记录不存在");
        }
        
        if (StringUtils.hasText(request.getWord())) {
            exists.setWord(request.getWord());
        }
        if (request.getWordType() != null) {
            exists.setWordType(request.getWordType());
        }
        if (StringUtils.hasText(request.getCategory())) {
            exists.setCategory(request.getCategory());
        }
        if (request.getSeverity() != null) {
            exists.setSeverity(request.getSeverity());
        }
        if (request.getIsRegex() != null) {
            exists.setIsRegex(request.getIsRegex());
        }
        if (request.getStatus() != null) {
            exists.setStatus(request.getStatus());
        }
        
        sensitiveWordMapper.updateById(exists);
        
        log.info("更新敏感词: {} (ID: {}), 操作人: {}", request.getWord(), request.getId(), operatorId);
    }
    
    @CacheEvict(value = {"sensitiveWords", "regexSensitiveWords"}, allEntries = true)
    public void delete(Long id, Long operatorId) {
        BbsSensitiveWord exists = sensitiveWordMapper.selectById(id);
        if (exists == null) {
            throw new IllegalArgumentException("记录不存在");
        }
        
        sensitiveWordMapper.deleteById(id);
        
        log.info("删除敏感词: {} (ID: {}), 操作人: {}", exists.getWord(), id, operatorId);
    }
    
    public FilterResult filterText(String text) {
        return compositeFilter.analyzeText(text);
    }
    
    public FilterResult filterText(String text, String category) {
        return compositeFilter.analyzeText(text, category);
    }
    
    @CacheEvict(value = {"sensitiveWords", "regexSensitiveWords"}, allEntries = true)
    public void batchImport(List<String> words, String category, Integer wordType, Long operatorId) {
        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException("敏感词列表不能为空");
        }
        
        int successCount = 0;
        List<String> duplicates = new java.util.ArrayList<>();
        
        for (String word : words) {
            if (!StringUtils.hasText(word)) {
                continue;
            }
            
            word = word.trim();
            
            LambdaQueryWrapper<BbsSensitiveWord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BbsSensitiveWord::getWord, word);
            if (sensitiveWordMapper.selectCount(wrapper) > 0) {
                duplicates.add(word);
                continue;
            }
            
            BbsSensitiveWord sensitiveWord = new BbsSensitiveWord();
            sensitiveWord.setWord(word);
            sensitiveWord.setWordType(wordType != null ? wordType : 1);
            sensitiveWord.setCategory(StringUtils.hasText(category) ? category : "default");
            sensitiveWord.setSeverity(1);
            sensitiveWord.setIsRegex(0);
            sensitiveWord.setHitCount(0);
            sensitiveWord.setStatus(1);
            
            sensitiveWordMapper.insert(sensitiveWord);
            successCount++;
        }
        
        log.info("批量导入敏感词完成 - 成功: {}, 跳过重复: {}, 操作人: {}", 
            successCount, duplicates.size(), operatorId);
        
        compositeFilter.reloadAll();
    }
    
    public void reloadFilter() {
        compositeFilter.reloadAll();
        log.info("敏感词过滤器已重新加载");
    }
    
    public int getTotalWordCount() {
        return compositeFilter.getTotalWordCount();
    }
}