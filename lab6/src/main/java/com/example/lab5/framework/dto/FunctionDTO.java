package com.example.lab5.framework.dto;

import lombok.Data;

@Data
public class FunctionDTO {
    private Long id;
    private String name;
    private String signature;
    private Long userId;

    // Поля для Lab 7
    private String factoryType;
    private String mathFunctionKey;
    private String creationMethod;
    private Double leftBound;
    private Double rightBound;
    private Integer pointsCount;
}