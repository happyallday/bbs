package com.company.bbs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.bbs.dto.BoardRequest;
import com.company.bbs.dto.ResponseResult;
import com.company.bbs.entity.BbsBoard;
import com.company.bbs.service.BoardManagementService;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/boards")
@PreAuthorize("hasRole('ADMIN')")
public class BoardManagementController {
    
    @Autowired
    private BoardManagementService boardManagementService;
    
    @GetMapping("/list")
    public ResponseResult<Page<BbsBoard>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            BoardRequest request) {
        
        Page<BbsBoard> page = boardManagementService.list(current, size, request);
        return ResponseResult.success(page);
    }
    
    @GetMapping("/all")
    public ResponseResult<List<BbsBoard>> getAll() {
        List<BbsBoard> boards = boardManagementService.getAllEnabled();
        return ResponseResult.success(boards);
    }
    
    @GetMapping("/{id}")
    public ResponseResult<BbsBoard> getById(@PathVariable Long id) {
        BbsBoard board = boardManagementService.getById(id);
        if (board == null) {
            return ResponseResult.error("板块不存在");
        }
        return ResponseResult.success(board);
    }
    
    @PostMapping("/add")
    public ResponseResult<Void> add(@Validated(BoardRequest.Create.class) @RequestBody BoardRequest request,
                                     HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            boardManagementService.add(request, userId);
            return ResponseResult.<Void>success("添加成功");
        } catch (Exception e) {
            log.error("添加板块失败", e);
            return ResponseResult.<Void>error("添加失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/update")
    public ResponseResult<Void> update(@RequestBody BoardRequest request,
                                        HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            boardManagementService.update(request, userId);
            return ResponseResult.<Void>success("更新成功");
        } catch (Exception e) {
            log.error("更新板块失败", e);
            return ResponseResult.<Void>error("更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id,
                                        HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            boardManagementService.delete(id, userId);
            return ResponseResult.<Void>success("删除成功");
        } catch (Exception e) {
            log.error("删除板块失败", e);
            return ResponseResult.<Void>error("删除失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/enable/{id}")
    public ResponseResult<Void> enable(@PathVariable Long id,
                                        HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            boardManagementService.enable(id, userId);
            return ResponseResult.<Void>success("启用成功");
        } catch (Exception e) {
            log.error("启用板块失败", e);
            return ResponseResult.<Void>error("启用失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/disable/{id}")
    public ResponseResult<Void> disable(@PathVariable Long id,
                                         HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            boardManagementService.disable(id, userId);
            return ResponseResult.<Void>success("禁用成功");
        } catch (Exception e) {
            log.error("禁用板块失败", e);
            return ResponseResult.<Void>error("禁用失败: " + e.getMessage());
        }
    }
}