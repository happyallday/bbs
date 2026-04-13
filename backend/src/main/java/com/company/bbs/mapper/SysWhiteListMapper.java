package com.company.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.bbs.entity.SysWhiteList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysWhiteListMapper extends BaseMapper<SysWhiteList> {
    
    @Select("SELECT COUNT(*) FROM sys_white_list WHERE user_id = #{userId} AND white_type = #{whiteType} AND status = 1")
    int countByUserIdAndType(@Param("userId") Long userId, @Param("whiteType") Integer whiteType);
}