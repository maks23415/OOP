package com.example.lab5.framework.entity;

import javax.persistence.*;

@Entity
@Table(name = "points")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "x_value", nullable = false)
    private Double xValue;

    @Column(name = "y_value", nullable = false)
    private Double yValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "f_id", nullable = false)
    private Function function;

    // Конструкторы
    public Point() {}

    public Point(Double xValue, Double yValue, Function function) {
        this.xValue = xValue;
        this.yValue = yValue;
        this.function = function;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getXValue() { return xValue; }
    public void setXValue(Double xValue) { this.xValue = xValue; }

    public Double getYValue() { return yValue; }
    public void setYValue(Double yValue) { this.yValue = yValue; }

    public Function getFunction() { return function; }
    public void setFunction(Function function) { this.function = function; }
}