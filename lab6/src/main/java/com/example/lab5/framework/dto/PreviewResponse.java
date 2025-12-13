package com.example.lab5.framework.dto;

import lombok.Data;
import java.util.List;

@Data
public class PreviewResponse {
    private List<PointData> points;

    @Data
    public static class PointData {
        private Double x;
        private Double y;
    }
}