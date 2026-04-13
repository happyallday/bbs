package com.company.bbs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.bbs.dto.BoardRequest;
import com.company.bbs.entity.BbsBoard;
import com.company.bbs.mapper.BbsBoardMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class BoardManagementService {
    
    @Autowired
    private BbsBoardMapper boardMapper;
    
    public Page<BbsBoard> list(Integer current, Integer size, BoardRequest request) {
        Page<BbsBoard> page = new Page<>(current, size);
        LambdaQueryWrapper<BbsBoard> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(request.getBoardName())) {
            wrapper.like(BbsBoard::getBoardName, request.getBoardName());
        }
        if (request.getStatus() != null) {
            wrapper.eq(BbsBoard::getStatus, request.getStatus());
        }
        
        wrapper.orderByAsc(BbsBoard::getSortOrder)
               .orderByDesc(BbsBoard::getCreatedTime);
        
        return boardMapper.selectPage(page, wrapper);
    }
    
    public BbsBoard getById(Long id) {
        return boardMapper.selectById(id);
    }
    
    public List<BbsBoard> getAllEnabled() {
        return boardMapper.selectList(
            new LambdaQueryWrapper<BbsBoard>()
                .eq(BbsBoard::getStatus, 1)
                .orderByAsc(BbsBoard::getSortOrder)
        );
    }
    
    public void add(BoardRequest request, Long operatorId) {
        LambdaQueryWrapper<BbsBoard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BbsBoard::getBoardCode, request.getBoardCode());
        if (boardMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("板块编码已存在");
        }
        
        BbsBoard board = new BbsBoard();
        board.setBoardName(request.getBoardName());
        board.setBoardCode(request.getBoardCode());
        board.setDescription(request.getDescription());
        board.setIconUrl(request.getIconUrl());
        board.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        board.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        board.setRequireAuth(request.getRequireAuth() != null ? request.getRequireAuth() : 0);
        board.setPostCount(0);
        
        boardMapper.insert(board);
        
        log.info("添加板块成功 - 名称: {}, 编码: {}, 操作人: {}", 
            request.getBoardName(), request.getBoardCode(), operatorId);
    }
    
    public void update(BoardRequest request, Long operatorId) {
        if (request.getId() == null) {
            throw new RuntimeException("ID不能为空");
        }
        
        BbsBoard exists = boardMapper.selectById(request.getId());
        if (exists == null) {
            throw new RuntimeException("板块不存在");
        }
        
        if (StringUtils.hasText(request.getBoardCode())) {
            LambdaQueryWrapper<BbsBoard> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BbsBoard::getBoardCode, request.getBoardCode())
                   .ne(BbsBoard::getId, request.getId());
            if (boardMapper.selectCount(wrapper) > 0) {
                throw new RuntimeException("板块编码已存在");
            }
            exists.setBoardCode(request.getBoardCode());
        }
        
        if (StringUtils.hasText(request.getBoardName())) {
            exists.setBoardName(request.getBoardName());
        }
        if (request.getDescription() != null) {
            exists.setDescription(request.getDescription());
        }
        if (request.getIconUrl() != null) {
            exists.setIconUrl(request.getIconUrl());
        }
        if (request.getSortOrder() != null) {
            exists.setSortOrder(request.getSortOrder());
        }
        if (request.getStatus() != null) {
            exists.setStatus(request.getStatus());
        }
        if (request.getRequireAuth() != null) {
            exists.setRequireAuth(request.getRequireAuth());
        }
        
        boardMapper.updateById(exists);
        
        log.info("更新板块成功 - ID: {}, 操作人: {}", request.getId(), operatorId);
    }
    
    public void delete(Long id, Long operatorId) {
        BbsBoard exists = boardMapper.selectById(id);
        if (exists == null) {
            throw new RuntimeException("板块不存在");
        }
        
        if (exists.getPostCount() > 0) {
            throw new RuntimeException("板块下有帖子，无法删除");
        }
        
        boardMapper.deleteById(id);
        
        log.info("删除板块成功 - 名称: {}, 操作人: {}", exists.getBoardName(), operatorId);
    }
    
    public void enable(Long id, Long operatorId) {
        BbsBoard board = boardMapper.selectById(id);
        if (board == null) {
            throw new RuntimeException("板块不存在");
        }
        
        board.setStatus(1);
        boardMapper.updateById(board);
        
        log.info("启用板块成功 - 名称: {}, 操作人: {}", board.getBoardName(), operatorId);
    }
    
    public void disable(Long id, Long operatorId) {
        BbsBoard board = boardMapper.selectById(id);
        if (board == null) {
            throw new RuntimeException("板块不存在");
        }
        
        board.setStatus(0);
        boardMapper.updateById(board);
        
        log.info("禁用板块成功 - 名称: {}, 操作人: {}", board.getBoardName(), operatorId);
    }
}