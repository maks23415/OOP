package com.example.lab5.framework.service;

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
