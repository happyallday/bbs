package com.company.bbs.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class OfficeNetworkRequest {
    
    @NotBlank(message = "IP地址段不能为空")
    private String ipRange;
    
    private String description;
    
    private Integer networkType;
}