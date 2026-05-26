package com.example.martyrs.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String role;
    private String nickname;
}
