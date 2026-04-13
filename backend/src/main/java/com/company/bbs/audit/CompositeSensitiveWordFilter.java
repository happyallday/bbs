package com.company.bbs.audit;

import com.company.bbs.dto.SensitiveWordMatchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CompositeSensitiveWordFilter implements SensitiveWordFilter {
    
    @Autowired
    private ACSensitiveWordFilter acFilter;
    
    @Autowired
    private RegexSensitiveWordFilter regexFilter;
    
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
        
        List<SensitiveWordMatchResult> acMatches = acFilter.filter(text, category);
        List<SensitiveWordMatchResult> regexMatches = regexFilter.filter(text, category);
        
        results.addAll(acMatches);
        results.addAll(regexMatches);
        
        return mergeOverlappingResults(results);
    }
    
    @Override
    public boolean containsSensitiveWord(String text) {
        return acFilter.containsSensitiveWord(text) || regexFilter.containsSensitiveWord(text);
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
        
        List<SensitiveWordMatchResult> matches = filter(text, category);
        if (matches.isEmpty()) {
            return text;
        }
        
        java.util.Collections.sort(matches, (a, b) -> a.getStartIndex() - b.getStartIndex());
        
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;
        for (SensitiveWordMatchResult match : matches) {
            if (match.getStartIndex() < lastEnd) {
                continue;
            }
            
            result.append(text.substring(lastEnd, match.getStartIndex()));
            String rep = replacement != null ? replacement : "***";
            result.append(rep);
            lastEnd = match.getEndIndex();
        }
        
        if (lastEnd < text.length()) {
            result.append(text.substring(lastEnd));
        }
        
        return result.toString();
    }
    
    public FilterResult analyzeText(String text) {
        return analyzeText(text, null);
    }
    
    public FilterResult analyzeText(String text, String category) {
        List<SensitiveWordMatchResult> matches = filter(text, category);
        
        FilterResult result = new FilterResult();
        result.setOriginalText(text);
        result.setMatchCount(matches.size());
        result.setMatches(matches);
        
        if (matches.isEmpty()) {
            result.setNeedAudit(false);
            result.setFilterStatus("PASS");
            result.setFilteredText(text);
        } else {
            result.setNeedAudit(true);
            result.setFilterStatus("SENSITIVE_WORD_DETECTED");
            
            Set<String> matchedWords = matches.stream()
                .map(SensitiveWordMatchResult::getWord)
                .collect(Collectors.toSet());
            result.setMatchedWords(new ArrayList<>(matchedWords));
            
            result.setFilteredText(replaceSensitiveWords(text, "***", category));
            
            boolean hasHighSeverity = matches.stream()
                .anyMatch(m -> m.getSeverity() != null && m.getSeverity() >= 3);
            result.setHighSeverity(hasHighSeverity);
        }
        
        return result;
    }
    
    private List<SensitiveWordMatchResult> mergeOverlappingResults(List<SensitiveWordMatchResult> results) {
        if (results.isEmpty()) {
            return results;
        }
        
        java.util.Collections.sort(results, (a, b) -> a.getStartIndex() - b.getStartIndex());
        
        List<SensitiveWordMatchResult> merged = new ArrayList<>();
        SensitiveWordMatchResult current = results.get(0);
        
        for (int i = 1; i < results.size(); i++) {
            SensitiveWordMatchResult next = results.get(i);
            
            if (next.getStartIndex() < current.getEndIndex()) {
                if (next.getEndIndex() > current.getEndIndex()) {
                    current.setEndIndex(next.getEndIndex());
                }
                if (next.getSeverity() != null && 
                    (current.getSeverity() == null || next.getSeverity() > current.getSeverity())) {
                    current.setSeverity(next.getSeverity());
                }
            } else {
                merged.add(current);
                current = next;
            }
        }
        
        merged.add(current);
        return merged;
    }
    
    public void reloadAll() {
        log.info("重新加载所有敏感词过滤器...");
        acFilter.reloadKeywords();
        regexFilter.reloadPatterns();
        log.info("敏感词过滤器重新加载完成");
    }
    
    public int getTotalWordCount() {
        return acFilter.getKeywordCount() + regexFilter.getPatternCount();
    }
}