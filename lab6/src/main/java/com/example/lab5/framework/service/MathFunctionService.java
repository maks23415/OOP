package com.example.lab5.framework.service;

import com.example.lab5.framework.dto.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MathFunctionService {

    public List<MathFunctionDTO> getAllMathFunctions() {
        List<MathFunctionDTO> functions = new ArrayList<>();

        functions.add(createFunctionDTO("sqr", "Квадратичная функция",
                "Функция y = x²", "f(x) = x²", "алгебраические", "SqrFunction"));

        functions.add(createFunctionDTO("identity", "Тождественная функция",
                "Функция y = x", "f(x) = x", "алгебраические", "IdentityFunction"));

        functions.add(createFunctionDTO("sin", "Синус",
                "Тригонометрическая функция синус", "f(x) = sin(x)", "тригонометрические", "SinFunction"));

        functions.add(createFunctionDTO("cos", "Косинус",
                "Тригонометрическая функция косинус", "f(x) = cos(x)", "тригонометрические", "CosFunction"));

        functions.add(createFunctionDTO("exp", "Экспонента",
                "Экспоненциальная функция", "f(x) = e^x", "экспоненциальные", "ExpFunction"));

        functions.add(createFunctionDTO("log", "Натуральный логарифм",
                "Логарифмическая функция", "f(x) = ln(x)", "логарифмические", "LogFunction"));

        // Сортировка по алфавиту
        functions.sort(Comparator.comparing(MathFunctionDTO::getLabel));

        return functions;
    }

    public Map<String, MathFunctionDTO> getFunctionMap() {
        Map<String, MathFunctionDTO> map = new HashMap<>();
        getAllMathFunctions().forEach(func -> map.put(func.getKey(), func));
        return map;
    }

    public PreviewResponse previewMathFunction(String functionKey, Integer pointsCount,
                                               Double leftBound, Double rightBound) {
        PreviewResponse response = new PreviewResponse();
        List<PreviewResponse.PointData> points = new ArrayList<>();

        double step = (rightBound - leftBound) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double x = leftBound + i * step;
            double y = calculateFunction(functionKey, x);

            PreviewResponse.PointData point = new PreviewResponse.PointData();
            point.setX(x);
            point.setY(y);
            points.add(point);
        }

        response.setPoints(points);
        return response;
    }

    private double calculateFunction(String functionKey, double x) {
        switch (functionKey) {
            case "sqr":
                return x * x;
            case "identity":
                return x;
            case "sin":
                return Math.sin(x);
            case "cos":
                return Math.cos(x);
            case "exp":
                return Math.exp(x);
            case "log":
                return x > 0 ? Math.log(x) : Double.NaN;
            default:
                return 0;
        }
    }

    private MathFunctionDTO createFunctionDTO(String key, String label, String description,
                                              String example, String category, String functionType) {
        MathFunctionDTO dto = new MathFunctionDTO();
        dto.setKey(key);
        dto.setLabel(label);
        dto.setDescription(description);
        dto.setExample(example);
        dto.setCategory(category);
        dto.setFunctionType(functionType);
        return dto;
    }
}