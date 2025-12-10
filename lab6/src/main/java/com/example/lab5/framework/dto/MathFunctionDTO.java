package com.example.lab5.framework.dto;

import lombok.Data;

@Data
public class MathFunctionDTO {
    private String key;
    private String label;
    private String description;
    private String example;
    private String category;
    private String functionType;
}