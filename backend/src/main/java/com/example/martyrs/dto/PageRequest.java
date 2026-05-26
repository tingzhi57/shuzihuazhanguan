package com.example.martyrs.dto;

import lombok.Data;

@Data
public class PageRequest {
    private int page = 0;
    private int size = 10;
    private String keyword;
    private String type;
    private Integer status;
}
