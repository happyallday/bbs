package com.company.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.bbs.entity.SysOfficeNetwork;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysOfficeNetworkMapper extends BaseMapper<SysOfficeNetwork> {
    
    @Select("SELECT ip_range FROM sys_office_network WHERE status = 1 ORDER BY network_type ASC")
    List<String> findAllEnabledIpRanges();
}