package com.example.lab5.framework.dto;

import lombok.Data;

@Data
public class CreateFromMathRequest {
    private String mathFunctionKey;
    private Integer pointsCount;
    private Double leftBound;
    private Double rightBound;
    private String factoryType;
    private String name;
    private Long userId;
}