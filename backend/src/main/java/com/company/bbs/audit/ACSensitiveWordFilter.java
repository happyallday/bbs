package com.company.bbs.audit;

import com.company.bbs.dto.SensitiveWordMatchResult;
import com.company.bbs.entity.BbsSensitiveWord;
import com.company.bbs.mapper.BbsSensitiveWordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ACSensitiveWordFilter implements SensitiveWordFilter {
    
    @Autowired
    private BbsSensitiveWordMapper sensitiveWordMapper;
    
    private TrieNode root;
    
    private Set<String> keywords;
    
    private int maxWordLength;
    
    @PostConstruct
    public void init() {
        reloadKeywords();
    }
    
    public void reloadKeywords() {
        log.info("开始加载敏感词库...");
        long startTime = System.currentTimeMillis();
        
        try {
            List<BbsSensitiveWord> sensitiveWords = loadSensitiveWords();
            if (sensitiveWords == null || sensitiveWords.isEmpty()) {
                log.warn("敏感词库为空");
                return;
            }
            
            this.keywords = new HashSet<>();
            this.maxWordLength = 0;
            
            root = new TrieNode(0);
            
            for (BbsSensitiveWord word : sensitiveWords) {
                if (word.getStatus() == 1 && word.getWord() != null && !word.getWord().isEmpty()) {
                    addKeyword(word);
                }
            }
            
            buildFailureLinks();
            
            log.info("敏感词库加载完成，共{}个词，耗时{}ms", keywords.size(), System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("加载敏感词库失败", e);
        }
    }
    
    @Cacheable(value = "sensitiveWords", key = "'all'")
    public List<BbsSensitiveWord> loadSensitiveWords() {
        return sensitiveWordMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BbsSensitiveWord>()
                .eq(BbsSensitiveWord::getStatus, 1)
        );
    }
    
    private void addKeyword(BbsSensitiveWord wordEntity) {
        String word = wordEntity.getWord();
        TrieNode current = root;
        
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            current = current.addChild(ch);
        }
        
        current.setEnd(true);
        current.setWord(word);
        current.setCategory(wordEntity.getCategory());
        current.setSeverity(wordEntity.getSeverity());
        
        keywords.add(word);
        maxWordLength = Math.max(maxWordLength, word.length());
    }
    
    private void buildFailureLinks() {
        Queue<TrieNode> queue = new LinkedList<>();
        
        for (TrieNode child : root.getChildren().values()) {
            child.setFailure(root);
            queue.offer(child);
        }
        
        while (!queue.isEmpty()) {
            TrieNode current = queue.poll();
            
            for (Map.Entry<Character, TrieNode> entry : current.getChildren().entrySet()) {
                char ch = entry.getKey();
                TrieNode child = entry.getValue();
                
                TrieNode failure = current.getFailure();
                while (failure != root && !failure.hasChild(ch)) {
                    failure = failure.getFailure();
                }
                
                if (failure.hasChild(ch)) {
                    child.setFailure(failure.getChild(ch));
                } else {
                    child.setFailure(root);
                }
                
                queue.offer(child);
            }
        }
    }
    
    @Override
    public List<SensitiveWordMatchResult> filter(String text) {
        return filter(text, null);
    }
    
    @Override
    public List<SensitiveWordMatchResult> filter(String text, String category) {
        if (text == null || text.isEmpty() || keywords.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<SensitiveWordMatchResult> results = new ArrayList<>();
        TrieNode current = root;
        
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            
            while (current != root && !current.hasChild(ch)) {
                current = current.getFailure();
            }
            
            if (current.hasChild(ch)) {
                current = current.getChild(ch);
            } else {
                current = root;
                continue;
            }
            
            TrieNode temp = current;
            while (temp != root && temp.isEnd()) {
                if (category == null || category.equals(temp.getCategory())) {
                    String matchedWord = temp.getWord();
                    int startIndex = i - matchedWord.length() + 1;
                    SensitiveWordMatchResult result = new SensitiveWordMatchResult(
                        matchedWord,
                        startIndex,
                        i + 1,
                        temp.getCategory(),
                        temp.getSeverity()
                    );
                    results.add(result);
                }
                temp = temp.getFailure();
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
        List<SensitiveWordMatchResult> matches = filter(text, category);
        if (matches.isEmpty()) {
            return text;
        }
        
        StringBuilder result = new StringBuilder(text);
        Collections.sort(matches, (a, b) -> a.getStartIndex() - b.getStartIndex());
        
        int offset = 0;
        for (SensitiveWordMatchResult match : matches) {
            int start = match.getStartIndex() + offset;
            int end = match.getEndIndex() + offset;
            String rep = replacement != null ? replacement : "***";
            result.replace(start, end, rep);
            offset += rep.length() - (end - start);
        }
        
        return result.toString();
    }
    
    public int getMaxWordLength() {
        return maxWordLength;
    }
    
    public int getKeywordCount() {
        return keywords != null ? keywords.size() : 0;
    }
}