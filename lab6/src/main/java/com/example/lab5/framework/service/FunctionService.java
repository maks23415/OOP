package com.example.lab5.framework.service;

import com.example.lab5.framework.dto.*;
import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.entity.Point;
import com.example.lab5.framework.entity.User;
import com.example.lab5.framework.repository.FunctionRepository;
import com.example.lab5.framework.repository.PointRepository;
import com.example.lab5.framework.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FunctionService {
    private static final Logger logger = LoggerFactory.getLogger(FunctionService.class);

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    // Существующие методы...

    public Function createFunction(Long userId, String name, String signature) {
        logger.info("Создание функции: user={}, name={}, signature={}", userId, name, signature);

        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            logger.error("Пользователь с ID {} не существует", userId);
            throw new IllegalArgumentException("User with ID " + userId + " does not exist");
        }

        Function function = new Function(name, signature, user.get());
        Function savedFunction = functionRepository.save(function);
        logger.info("Создана функция с ID: {}", savedFunction.getId());
        return savedFunction;
    }

    // НОВЫЕ МЕТОДЫ ДЛЯ LAB 7
    @Transactional
    public Function createFromArrays(Long userId, String name,
                                     List<CreateFromArraysRequest.PointData> pointsData,
                                     String factoryType) {
        logger.info("Создание функции из массивов: user={}, name={}, points={}, factory={}",
                userId, name, pointsData.size(), factoryType);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", userId);
                    return new IllegalArgumentException("Пользователь не найден");
                });

        // Создаем функцию
        Function function = new Function();
        function.setName(name);
        function.setUser(user);
        function.setSignature("tabulated");
        function.setFactoryType(factoryType);
        function.setCreationMethod("from_arrays");
        function.setPointsCount(pointsData.size());

        // Находим min/max X для границ
        double minX = pointsData.stream().mapToDouble(CreateFromArraysRequest.PointData::getX).min().orElse(0);
        double maxX = pointsData.stream().mapToDouble(CreateFromArraysRequest.PointData::getX).max().orElse(0);
        function.setLeftBound(minX);
        function.setRightBound(maxX);

        Function savedFunction = functionRepository.save(function);
        logger.info("Функция создана с ID: {}", savedFunction.getId());

        // Создаем точки
        for (CreateFromArraysRequest.PointData pointData : pointsData) {
            Point point = new Point();
            point.setXValue(pointData.getX());
            point.setYValue(pointData.getY());
            point.setFunction(savedFunction);
            pointRepository.save(point);
        }

        logger.info("Создано {} точек для функции {}", pointsData.size(), savedFunction.getId());
        return savedFunction;
    }

    @Transactional
    public Function createFromMathFunction(Long userId, String name, String mathFunctionKey,
                                           Integer pointsCount, Double leftBound, Double rightBound,
                                           String factoryType) {
        logger.info("Создание функции из MathFunction: user={}, name={}, functionKey={}, points={}, bounds=[{}, {}], factory={}",
                userId, name, mathFunctionKey, pointsCount, leftBound, rightBound, factoryType);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", userId);
                    return new IllegalArgumentException("Пользователь не найден");
                });

        // Проверяем корректность параметров
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        if (leftBound >= rightBound) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }

        // Создаем функцию
        Function function = new Function();
        function.setName(name);
        function.setUser(user);
        function.setSignature("math_function_tabulated");
        function.setMathFunctionKey(mathFunctionKey);
        function.setFactoryType(factoryType);
        function.setCreationMethod("from_math_function");
        function.setPointsCount(pointsCount);
        function.setLeftBound(leftBound);
        function.setRightBound(rightBound);

        Function savedFunction = functionRepository.save(function);
        logger.info("Функция создана с ID: {}", savedFunction.getId());

        // Генерируем точки из математической функции
        double step = (rightBound - leftBound) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            double x = leftBound + i * step;
            double y = calculateMathFunction(mathFunctionKey, x);

            Point point = new Point();
            point.setXValue(x);
            point.setYValue(y);
            point.setFunction(savedFunction);
            pointRepository.save(point);

            // Логируем каждые 100 точек
            if (i % 100 == 0 || i == pointsCount - 1) {
                logger.debug("Создана точка {}: x={}, y={}", i, x, y);
            }
        }

        logger.info("Сгенерировано {} точек для функции {}", pointsCount, savedFunction.getId());
        return savedFunction;
    }

    public EvaluateResponse evaluateFunction(Long functionId, Double x) {
        logger.debug("Вычисление значения функции {} в точке x={}", functionId, x);

        Optional<Function> functionOpt = functionRepository.findById(functionId);
        if (!functionOpt.isPresent()) {
            logger.warn("Функция с ID {} не найдена", functionId);
            return null;
        }

        Function function = functionOpt.get();

        // Если это табулированная функция, ищем ближайшую точку или интерполируем
        if ("tabulated".equals(function.getSignature()) ||
                "math_function_tabulated".equals(function.getSignature())) {

            List<Point> points = pointRepository.findByFunctionIdOrderByXValueAsc(functionId);

            if (points.isEmpty()) {
                logger.warn("У функции {} нет точек", functionId);
                return null;
            }

            // Если x меньше минимального или больше максимального значения
            if (x < points.get(0).getXValue()) {
                logger.debug("x={} меньше минимального значения {}", x, points.get(0).getXValue());
                return new EvaluateResponse(x, points.get(0).getYValue(), function.getName(), functionId);
            }

            if (x > points.get(points.size() - 1).getXValue()) {
                logger.debug("x={} больше максимального значения {}", x, points.get(points.size() - 1).getXValue());
                return new EvaluateResponse(x, points.get(points.size() - 1).getYValue(), function.getName(), functionId);
            }

            // Поиск интервала для интерполяции
            for (int i = 0; i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);

                if (x >= p1.getXValue() && x <= p2.getXValue()) {
                    // Линейная интерполяция
                    double ratio = (x - p1.getXValue()) / (p2.getXValue() - p1.getXValue());
                    double y = p1.getYValue() + ratio * (p2.getYValue() - p1.getYValue());

                    logger.debug("Интерполяция: x={} между [{}, {}], y={}",
                            x, p1.getXValue(), p2.getXValue(), y);

                    return new EvaluateResponse(x, y, function.getName(), functionId);
                }
            }

            // Точечное совпадение
            for (Point point : points) {
                if (Math.abs(point.getXValue() - x) < 1e-10) {
                    logger.debug("Точное совпадение: x={}, y={}", x, point.getYValue());
                    return new EvaluateResponse(x, point.getYValue(), function.getName(), functionId);
                }
            }
        }

        logger.warn("Не удалось вычислить значение функции {} в точке {}", functionId, x);
        return null;
    }

    private double calculateMathFunction(String functionKey, double x) {
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
                logger.warn("Неизвестная функция: {}", functionKey);
                return 0;
        }
    }

    // Остальные существующие методы...
    public Optional<Function> getFunctionById(Long id) {
        logger.debug("Поиск функции по ID: {}", id);
        return functionRepository.findById(id);
    }

    public List<Function> getFunctionsByUserId(Long userId) {
        logger.debug("Поиск функций пользователя с ID: {}", userId);
        Optional<User> user = userRepository.findById(userId);
        return user.map(functionRepository::findByUser).orElse(List.of());
    }

    public List<Function> getFunctionsByName(String name) {
        logger.debug("Поиск функций по имени: {}", name);
        return functionRepository.findByNameContaining(name);
    }

    public List<Function> getAllFunctions() {
        logger.debug("Получение всех функций");
        return functionRepository.findAll();
    }

    public Function updateFunction(Long functionId, Long userId, String name, String signature) {
        logger.info("Обновление функции с ID: {}", functionId);

        Optional<Function> existingFunction = functionRepository.findById(functionId);
        if (existingFunction.isPresent()) {
            Optional<User> user = userRepository.findById(userId);
            if (!user.isPresent()) {
                logger.error("Пользователь с ID {} не существует", userId);
                return null;
            }

            Function function = existingFunction.get();
            function.setUser(user.get());
            function.setName(name);
            function.setSignature(signature);

            Function updated = functionRepository.save(function);
            logger.info("Функция с ID {} успешно обновлена", functionId);
            return updated;
        }

        logger.warn("Функция с ID {} не найдена для обновления", functionId);
        return null;
    }

    public boolean deleteFunction(Long functionId) {
        logger.info("Удаление функции с ID: {}", functionId);

        if (functionRepository.existsById(functionId)) {
            pointRepository.deleteByFunctionId(functionId);
            functionRepository.deleteById(functionId);
            logger.info("Функция с ID {} и все её точки удалены", functionId);
            return true;
        }

        logger.warn("Функция с ID {} не найдена для удаления", functionId);
        return false;
    }

    public FunctionStatistics getFunctionStatistics(Long functionId) {
        logger.debug("Получение статистики для функции с ID: {}", functionId);

        Optional<Function> function = functionRepository.findById(functionId);
        if (function.isPresent()) {
            List<Point> points = pointRepository.findByFunctionId(functionId);

            double minX = points.stream().mapToDouble(Point::getXValue).min().orElse(0);
            double maxX = points.stream().mapToDouble(Point::getXValue).max().orElse(0);
            double minY = points.stream().mapToDouble(Point::getYValue).min().orElse(0);
            double maxY = points.stream().mapToDouble(Point::getYValue).max().orElse(0);
            double avgY = points.stream().mapToDouble(Point::getYValue).average().orElse(0);

            FunctionStatistics stats = new FunctionStatistics(
                    functionId,
                    function.get().getName(),
                    points.size(),
                    minX,
                    maxX,
                    minY,
                    maxY,
                    avgY
            );

            logger.info("Статистика функции {}: {} точек, x=[{}, {}], y=[{}, {}]",
                    function.get().getName(), points.size(), minX, maxX, minY, maxY);

            return stats;
        }

        logger.warn("Функция с ID {} не найдена для статистики", functionId);
        return null;
    }

    public static class FunctionStatistics {
        private final Long functionId;
        private final String functionName;
        private final int pointCount;
        private final double minX;
        private final double maxX;
        private final double minY;
        private final double maxY;
        private final double averageY;

        public FunctionStatistics(Long functionId, String functionName, int pointCount,
                                  double minX, double maxX, double minY, double maxY, double averageY) {
            this.functionId = functionId;
            this.functionName = functionName;
            this.pointCount = pointCount;
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
            this.averageY = averageY;
        }

        public Long getFunctionId() { return functionId; }
        public String getFunctionName() { return functionName; }
        public int getPointCount() { return pointCount; }
        public double getMinX() { return minX; }
        public double getMaxX() { return maxX; }
        public double getMinY() { return minY; }
        public double getMaxY() { return maxY; }
        public double getAverageY() { return averageY; }

        @Override
        public String toString() {
            return String.format(
                    "FunctionStatistics{function='%s', points=%d, x=[%.2f, %.2f], y=[%.2f, %.2f], avgY=%.2f}",
                    functionName, pointCount, minX, maxX, minY, maxY, averageY
            );
        }
    }
}
