package com.company.bbs.audit;

import com.company.bbs.dto.SensitiveWordMatchResult;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FilterResult {
    
    private String originalText;
    
    private String filteredText;
    
    private Integer matchCount;
    
    private List<SensitiveWordMatchResult> matches;
    
    private List<String> matchedWords;
    
    private boolean needAudit;
    
    private boolean highSeverity;
    
    private String filterStatus;
    
    private String message;
    
    public static FilterResult pass() {
        FilterResult result = new FilterResult();
        result.setNeedAudit(false);
        result.setFilterStatus("PASS");
        result.setMessage("内容干净，无需审核");
        return result;
    }
    
    public static FilterResult needAudit(List<String> matchedWords) {
        FilterResult result = new FilterResult();
        result.setNeedAudit(true);
        result.setFilterStatus("NEED_AUDIT");
        result.setMatchedWords(matchedWords);
        result.setMatchCount(matchedWords.size());
        result.setMessage("包含敏感词，需要审核");
        return result;
    }
    
    public static FilterResult reject(String message) {
        FilterResult result = new FilterResult();
        result.setNeedAudit(true);
        result.setFilterStatus("REJECT");
        result.setMessage(message);
        return result;
    }
}