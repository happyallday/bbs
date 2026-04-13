package com.company.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.bbs.entity.BbsPost;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BbsPostMapper extends BaseMapper<BbsPost> {
}