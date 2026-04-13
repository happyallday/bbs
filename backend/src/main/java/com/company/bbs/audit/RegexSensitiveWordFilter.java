package com.company.bbs.audit;

import com.company.bbs.dto.SensitiveWordMatchResult;
import com.company.bbs.entity.BbsSensitiveWord;
import com.company.bbs.mapper.BbsSensitiveWordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class RegexSensitiveWordFilter implements SensitiveWordFilter {
    
    @Autowired
    private BbsSensitiveWordMapper sensitiveWordMapper;
    
    private List<Pattern> patterns;
    
    private List<SensitiveWordInfo> wordInfos;
    
    @PostConstruct
    public void init() {
        reloadPatterns();
    }
    
    public void reloadPatterns() {
        log.info("开始加载敏感词正则表达式...");
        long startTime = System.currentTimeMillis();
        
        try {
            List<BbsSensitiveWord> sensitiveWords = loadRegexSensitiveWords();
            
            this.patterns = new ArrayList<>();
            this.wordInfos = new ArrayList<>();
            
            for (BbsSensitiveWord word : sensitiveWords) {
                try {
                    Pattern pattern = Pattern.compile(word.getWord(), Pattern.CASE_INSENSITIVE);
                    patterns.add(pattern);
                    
                    SensitiveWordInfo info = new SensitiveWordInfo();
                    info.setWord(word.getWord());
                    info.setCategory(word.getCategory());
                    info.setSeverity(word.getSeverity());
                    info.setWordId(word.getId());
                    wordInfos.add(info);
                } catch (Exception e) {
                    log.warn("无效的正则表达式: {}", word.getWord());
                }
            }
            
            log.info("敏感词正则表达式加载完成，共{}个，耗时{}ms", patterns.size(), System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("加载敏感词正则表达式失败", e);
        }
    }
    
    @Cacheable(value = "regexSensitiveWords", key = "'all'")
    public List<BbsSensitiveWord> loadRegexSensitiveWords() {
        return sensitiveWordMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BbsSensitiveWord>()
                .eq(BbsSensitiveWord::getStatus, 1)
                .eq(BbsSensitiveWord::getIsRegex, 1)
        );
    }
    
    @Override
    public List<SensitiveWordMatchResult> filter(String text) {
        return filter(text, null);
    }
    
    @Override
    public List<SensitiveWordMatchResult> filter(String text, String category) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<SensitiveWordMatchResult> results = new ArrayList<>();
        
        for (int i = 0; i < patterns.size(); i++) {
            SensitiveWordInfo info = wordInfos.get(i);
            
            if (category != null && !category.equals(info.category)) {
                continue;
            }
            
            Pattern pattern = patterns.get(i);
            Matcher matcher = pattern.matcher(text);
            
            while (matcher.find()) {
                SensitiveWordMatchResult result = new SensitiveWordMatchResult(
                    matcher.group(),
                    matcher.start(),
                    matcher.end(),
                    info.category,
                    info.severity
                );
                results.add(result);
            }
        }
        
        return results;
    }
    
    @Override
    public boolean containsSensitiveWord(String text) {
        return !filter(text).isEmpty();
    }
    
    @Override
    public String replaceSensitiveWords(String text, String replacement) {
        return replaceSensitiveWords(text, replacement, null);
    }
    
    @Override
    public String replaceSensitiveWords(String text, String replacement, String category) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        String result = text;
        String rep = replacement != null ? replacement : "***";
        
        List<SensitiveWordMatchResult> matches = filter(text, category);
        
        for (SensitiveWordMatchResult match : matches) {
            result = result.substring(0, match.getStartIndex()) + rep + 
                     result.substring(match.getEndIndex());
        }
        
        return result;
    }
    
    public int getPatternCount() {
        return patterns != null ? patterns.size() : 0;
    }
    
    private static class SensitiveWordInfo {
        String word;
        String category;
        Integer severity;
        Long wordId;
        
        public void setWord(String word) { this.word = word; }
        public void setCategory(String category) { this.category = category; }
        public void setSeverity(Integer severity) { this.severity = severity; }
        public void setWordId(Long wordId) { this.wordId = wordId; }
    }
}