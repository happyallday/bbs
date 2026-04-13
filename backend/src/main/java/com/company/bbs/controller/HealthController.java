package com.company.bbs.controller;

import com.company.bbs.dto.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {
    
    @GetMapping
    public ResponseResult<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("application", "员工论坛系统");
        data.put("version", "1.0.0");
        data.put("timestamp", System.currentTimeMillis());
        return ResponseResult.success(data);
    }
}