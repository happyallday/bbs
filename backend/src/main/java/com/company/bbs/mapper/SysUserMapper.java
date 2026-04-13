package com.company.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.bbs.entity.SysUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    
    @Select("SELECT * FROM sys_user WHERE wechat_userid = #{wechatUserid} AND deleted = 0")
    SysUser findByWechatUserid(@Param("wechatUserid") String wechatUserid);
    
    @Select("SELECT r.role_code FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> findRoleCodesByUserId(@Param("userId") Long userId);
    
    @Update("UPDATE sys_user SET last_login_time = NOW(), last_login_ip = #{ip} WHERE id = #{userId}")
    void updateLastLogin(@Param("userId") Long userId, @Param("ip") String ip);
    
    @Insert("INSERT INTO sys_user_role (user_id, role_id, created_time) VALUES (#{userId}, #{roleId}, NOW())")
    void insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}