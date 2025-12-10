package com.example.lab5.framework.dto;

import lombok.Data;

@Data
public class EvaluateResponse {
    private Double x;
    private Double y;
    private String functionName;
    private Long functionId;

    // Конструктор для удобства
    public EvaluateResponse(Double x, Double y, String functionName, Long functionId) {
        this.x = x;
        this.y = y;
        this.functionName = functionName;
        this.functionId = functionId;
    }
}