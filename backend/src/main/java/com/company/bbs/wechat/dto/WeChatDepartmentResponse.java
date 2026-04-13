package com.company.bbs.wechat.dto;

import lombok.Data;

import java.util.Map;

@Data
public class WeChatDepartmentResponse {
    
    private Integer errcode;
    private String errmsg;
    private java.util.List<Department> department;
    
    @Data
    public static class Department {
        private Integer id;
        private String name;
        private Integer parentid;
        private Integer order;
    }
}