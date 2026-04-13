package com.company.bbs.audit;

import com.company.bbs.dto.SensitiveWordMatchResult;

import java.util.List;

public interface SensitiveWordFilter {
    
    List<SensitiveWordMatchResult> filter(String text);
    
    List<SensitiveWordMatchResult> filter(String text, String category);
    
    boolean containsSensitiveWord(String text);
    
    String replaceSensitiveWords(String text, String replacement);
    
    String replaceSensitiveWords(String text, String replacement, String category);
}