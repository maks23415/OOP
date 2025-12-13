package com.example.lab5.framework.dto;

import lombok.Data;

@Data
public class PreviewRequest {
    private String mathFunctionKey;
    private Integer pointsCount;
    private Double leftBound;
    private Double rightBound;
}
