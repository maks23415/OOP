package com.example.lab5.framework.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "points")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "func_id", nullable = false)
    private Long funcId;

    @Column(name = "x_value", nullable = false)
    private Double xValue;

    @Column(name = "y_value", nullable = false)
    private Double yValue;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Конструкторы
    public Point() {}

    public Point(Long funcId, Double xValue, Double yValue) {
        this.funcId = funcId;
        this.xValue = xValue;
        this.yValue = yValue;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFuncId() { return funcId; }
    public void setFuncId(Long funcId) { this.funcId = funcId; }

    public Double getXValue() { return xValue; }
    public void setXValue(Double xValue) { this.xValue = xValue; }

    public Double getYValue() { return yValue; }
    public void setYValue(Double yValue) { this.yValue = yValue; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}