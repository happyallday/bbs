package com.company.bbs.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String code;
    private String state;
}