package com.company.bbs.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.bbs.entity.BbsBoard;
import com.company.bbs.mapper.BbsBoardMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BoardService {
    
    @Autowired
    private BbsBoardMapper boardMapper;
    
    public List<BbsBoard> getAllBoards() {
        return boardMapper.selectList(
            new LambdaQueryWrapper<BbsBoard>()
                .eq(BbsBoard::getStatus, 1)
                .orderByAsc(BbsBoard::getSortOrder)
        );
    }
    
    public BbsBoard getById(Long id) {
        return boardMapper.selectById(id);
    }
    
    public BbsBoard getByCode(String boardCode) {
        return boardMapper.selectOne(
            new LambdaQueryWrapper<BbsBoard>()
                .eq(BbsBoard::getBoardCode, boardCode)
                .eq(BbsBoard::getStatus, 1)
        );
    }
    
    public void incrementPostCount(Long boardId) {
        BbsBoard board = boardMapper.selectById(boardId);
        if (board != null) {
            board.setPostCount(board.getPostCount() + 1);
            boardMapper.updateById(board);
        }
    }
    
    public void decrementPostCount(Long boardId) {
        BbsBoard board = boardMapper.selectById(boardId);
        if (board != null && board.getPostCount() > 0) {
            board.setPostCount(board.getPostCount() - 1);
            boardMapper.updateById(board);
        }
    }
}