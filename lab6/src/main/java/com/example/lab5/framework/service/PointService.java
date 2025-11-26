package com.example.lab5.framework.service;

import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.entity.Point;
import com.example.lab5.framework.repository.FunctionRepository;
import com.example.lab5.framework.repository.PointRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PointService {
    private static final Logger logger = LoggerFactory.getLogger(PointService.class);

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private FunctionRepository functionRepository;

    public Point createPoint(Long functionId, Double xValue, Double yValue) {
        logger.info("Создание точки: function={}, x={}, y={}", functionId, xValue, yValue);

        Optional<Function> function = functionRepository.findById(functionId);
        if (!function.isPresent()) {
            logger.error("Функция с ID {} не существует", functionId);
            throw new IllegalArgumentException("Function with ID " + functionId + " does not exist");
        }

        Point point = new Point(xValue, yValue, function.get());
        Point savedPoint = pointRepository.save(point);
        logger.debug("Создана точка с ID: {}", savedPoint.getId());
        return savedPoint;
    }

    public int generateFunctionPoints(Long functionId, String functionType, double start, double end, double step) {
        logger.info("Генерация точек для функции {}: type={}, range=[{}, {}], step={}",
                functionId, functionType, start, end, step);

        Optional<Function> function = functionRepository.findById(functionId);
        if (!function.isPresent()) {
            logger.error("Функция с ID {} не существует", functionId);
            return 0;
        }

        List<Point> points = new ArrayList<>();
        int pointCount = 0;

        for (double x = start; x <= end; x += step) {
            double y = calculateFunction(functionType, x);
            points.add(new Point(x, y, function.get()));
            pointCount++;
        }

        if (!points.isEmpty()) {
            pointRepository.saveAll(points);
            logger.info("Сгенерировано {} точек для функции {}", pointCount, functionId);
        }

        return pointCount;
    }

    private double calculateFunction(String functionType, double x) {
        return switch (functionType.toLowerCase()) {
            case "linear" -> x;
            case "quadratic" -> x * x;
            case "cubic" -> x * x * x;
            case "sin" -> Math.sin(x);
            case "cos" -> Math.cos(x);
            case "exp" -> Math.exp(x);
            case "log" -> Math.log(x);
            default -> x;
        };
    }

    public Optional<Point> getPointById(Long id) {
        logger.debug("Поиск точки по ID: {}", id);
        return pointRepository.findById(id);
    }

    public List<Point> getPointsByFunctionId(Long functionId) {
        logger.debug("Поиск точек функции с ID: {}", functionId);
        return pointRepository.findByFunctionId(functionId);
    }

    public List<Point> getAllPoints() {
        logger.debug("Получение всех точек");
        return pointRepository.findAll();
    }
}
