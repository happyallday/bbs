package com.company.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.bbs.entity.BbsAuditLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BbsAuditLogMapper extends BaseMapper<BbsAuditLog> {
}