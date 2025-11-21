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

    // Конструкторы
    public Function() {}

    public Function(String name, String signature, User user) {
        this.name = name;
        this.signature = signature;
        this.user = user;
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

    public void addPoint(Point point) {
        points.add(point);
        point.setFunction(this);
    }

    public void removePoint(Point point) {
        points.remove(point);
        point.setFunction(null);
    }
}
