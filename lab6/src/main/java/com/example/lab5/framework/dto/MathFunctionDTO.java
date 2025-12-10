// MathFunctionDTO.java
package com.example.lab5.framework.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data // Эта аннотация генерирует ВСЕ: геттеры, сеттеры, equals, hashCode, toString
public class MathFunctionDTO {
    private String key;
    private String label;
    private String description;
    private String example;
    private String category;
    private String functionType;
}