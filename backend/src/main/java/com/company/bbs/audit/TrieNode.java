package com.company.bbs.audit;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TrieNode {
    
    private int depth;
    
    private TrieNode failure;
    
    private Map<Character, TrieNode> children;
    
    private boolean isEnd;
    
    private String word;
    
    private String category;
    
    private Integer severity;
    
    private int wordId;
    
    private static final Map<Character, TrieNode> EMPTY_CHILDREN = new HashMap<>();
    
    public TrieNode(int depth) {
        this.depth = depth;
        this.children = new HashMap<>();
        this.isEnd = false;
        this.failure = null;
    }
    
    public TrieNode addChild(char ch) {
        return children.computeIfAbsent(ch, k -> new TrieNode(depth + 1));
    }
    
    public TrieNode getChild(char ch) {
        return children.getOrDefault(ch, null);
    }
    
    public boolean hasChild(char ch) {
        return children.containsKey(ch);
    }
    
    public Map<Character, TrieNode> getChildren() {
        return children.isEmpty() ? EMPTY_CHILDREN : children;
    }
}