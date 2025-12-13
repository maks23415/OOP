package com.example.lab5.framework.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateFromArraysRequest {
    private List<PointData> points;
    private String factoryType;
    private String name;
    private Long userId;

    @Data
    public static class PointData {
        private Double x;
        private Double y;
    }
}