package com.example.lab5.framework.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "functions")
public class Function {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "signature")
    private String signature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "function", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Point> points = new ArrayList<>();

    // НОВЫЕ ПОЛЯ ДЛЯ LAB 7
    @Column(name = "factory_type")
    private String factoryType; // "array" или "linked_list"

    @Column(name = "math_function_key")
    private String mathFunctionKey; // Ключ математической функции

    @Column(name = "creation_method")
    private String creationMethod; // "from_arrays" или "from_math_function"

    @Column(name = "left_bound")
    private Double leftBound;

    @Column(name = "right_bound")
    private Double rightBound;

    @Column(name = "points_count")
    private Integer pointsCount;

    // Конструкторы
    public Function() {}

    public Function(String name, String signature, User user) {
        this.name = name;
        this.signature = signature;
        this.user = user;
    }

    // Полный конструктор для Lab 7
    public Function(String name, String signature, User user, String factoryType,
                    String mathFunctionKey, String creationMethod, Double leftBound,
                    Double rightBound, Integer pointsCount) {
        this.name = name;
        this.signature = signature;
        this.user = user;
        this.factoryType = factoryType;
        this.mathFunctionKey = mathFunctionKey;
        this.creationMethod = creationMethod;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.pointsCount = pointsCount;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Point> getPoints() { return points; }
    public void setPoints(List<Point> points) { this.points = points; }

    // Новые геттеры и сеттеры для Lab 7
    public String getFactoryType() { return factoryType; }
    public void setFactoryType(String factoryType) { this.factoryType = factoryType; }

    public String getMathFunctionKey() { return mathFunctionKey; }
    public void setMathFunctionKey(String mathFunctionKey) { this.mathFunctionKey = mathFunctionKey; }

    public String getCreationMethod() { return creationMethod; }
    public void setCreationMethod(String creationMethod) { this.creationMethod = creationMethod; }

    public Double getLeftBound() { return leftBound; }
    public void setLeftBound(Double leftBound) { this.leftBound = leftBound; }

    public Double getRightBound() { return rightBound; }
    public void setRightBound(Double rightBound) { this.rightBound = rightBound; }

    public Integer getPointsCount() { return pointsCount; }
    public void setPointsCount(Integer pointsCount) { this.pointsCount = pointsCount; }

    public void addPoint(Point point) {
        points.add(point);
        point.setFunction(this);
    }

    public void removePoint(Point point) {
        points.remove(point);
        point.setFunction(null);
    }
}
