package com.company.bbs.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class SensitiveWordMatchResult {
    
    private String word;
    
    private Integer startIndex;
    
    private Integer endIndex;
    
    private String category;
    
    private Integer severity;
    
    public SensitiveWordMatchResult(String word, int startIndex, int endIndex, String category, Integer severity) {
        this.word = word;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.category = category;
        this.severity = severity;
    }
}