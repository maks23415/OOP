package com.example.lab5.manual.service;

import com.example.lab5.manual.dao.FunctionDAO;
import com.example.lab5.manual.dao.PointDAO;
import com.example.lab5.manual.dto.PointDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PointService {
    private static final Logger logger = LoggerFactory.getLogger(PointService.class);
    private final PointDAO pointDAO;
    private final FunctionDAO functionDAO;

    public PointService() {
        this.pointDAO = new PointDAO();
        this.functionDAO = new FunctionDAO();
    }

    public PointService(PointDAO pointDAO, FunctionDAO functionDAO) {
        this.pointDAO = pointDAO;
        this.functionDAO = functionDAO;
    }

    public Long createPoint(Long functionId, Double xValue, Double yValue) {
        logger.info("Создание точки: function={}, x={}, y={}", functionId, xValue, yValue);

        if (!functionDAO.findById(functionId).isPresent()) {
            logger.error("Функция с ID {} не существует", functionId);
            throw new IllegalArgumentException("Function with ID " + functionId + " does not exist");
        }

        PointDTO point = new PointDTO(functionId, xValue, yValue);
        Long pointId = pointDAO.createPoint(point);
        logger.debug("Создана точка с ID: {}", pointId);
        return pointId;
    }

    public int createPointsBatch(Long functionId, List<Double> xValues, List<Double> yValues) {
        logger.info("Пакетное создание точек для функции {}: {} точек", functionId, xValues.size());

        if (xValues.size() != yValues.size()) {
            logger.error("Количество X и Y значений не совпадает: {} vs {}", xValues.size(), yValues.size());
            throw new IllegalArgumentException("X and Y values count must be equal");
        }

        if (!functionDAO.findById(functionId).isPresent()) {
            logger.error("Функция с ID {} не существует", functionId);
            throw new IllegalArgumentException("Function with ID " + functionId + " does not exist");
        }

        List<PointDTO> points = new ArrayList<>();
        for (int i = 0; i < xValues.size(); i++) {
            points.add(new PointDTO(functionId, xValues.get(i), yValues.get(i)));
        }

        int createdCount = pointDAO.createPoints(points);
        logger.info("Создано {} точек для функции {}", createdCount, functionId);
        return createdCount;
    }

    public int generateFunctionPoints(Long functionId, String functionType, double start, double end, double step) {
        logger.info("Генерация точек для функции {}: type={}, range=[{}, {}], step={}",
                functionId, functionType, start, end, step);

        List<PointDTO> points = new ArrayList<>();
        int pointCount = 0;

        for (double x = start; x <= end; x += step) {
            double y = calculateFunction(functionType, x);
            points.add(new PointDTO(functionId, x, y));
            pointCount++;
        }

        if (!points.isEmpty()) {
            pointDAO.createPoints(points);
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

    public Optional<PointDTO> getPointById(Long id) {
        logger.debug("Поиск точки по ID: {}", id);
        return pointDAO.findById(id);
    }

    public List<PointDTO> getPointsByFunctionId(Long functionId) {
        logger.debug("Поиск точек функции с ID: {}", functionId);
        return pointDAO.findByFunctionId(functionId);
    }

    public List<PointDTO> getPointsByXRange(Long functionId, Double minX, Double maxX) {
        logger.debug("Поиск точек функции {} в диапазоне x=[{}, {}]", functionId, minX, maxX);
        return pointDAO.findByXRange(functionId, minX, maxX);
    }

    public List<PointDTO> getPointsByYRange(Long functionId, Double minY, Double maxY) {
        logger.debug("Поиск точек функции {} в диапазоне y=[{}, {}]", functionId, minY, maxY);
        return pointDAO.findByYRange(functionId, minY, maxY);
    }

    public List<PointDTO> getAllPoints() {
        logger.debug("Получение всех точек");
        return pointDAO.findAll();
    }

    public PointDTO findMaxYPoint(Long functionId) {
        logger.debug("Поиск точки с максимальным Y для функции {}", functionId);
        List<PointDTO> points = pointDAO.findByFunctionId(functionId);

        return points.stream()
                .max((p1, p2) -> Double.compare(p1.getYValue(), p2.getYValue()))
                .orElse(null);
    }

    public PointDTO findMinYPoint(Long functionId) {
        logger.debug("Поиск точки с минимальным Y для функции {}", functionId);
        List<PointDTO> points = pointDAO.findByFunctionId(functionId);

        return points.stream()
                .min((p1, p2) -> Double.compare(p1.getYValue(), p2.getYValue()))
                .orElse(null);
    }

    public List<PointDTO> findRoots(Long functionId, double tolerance) {
        logger.debug("Поиск корней функции {} с точностью {}", functionId, tolerance);
        List<PointDTO> points = pointDAO.findByFunctionId(functionId);
        List<PointDTO> roots = new ArrayList<>();

        for (PointDTO point : points) {
            if (Math.abs(point.getYValue()) <= tolerance) {
                roots.add(point);
            }
        }

        logger.debug("Найдено {} корней функции {}", roots.size(), functionId);
        return roots;
    }

    public boolean updatePoint(Long pointId, Long functionId, Double xValue, Double yValue) {
        logger.info("Обновление точки с ID: {}", pointId);

        Optional<PointDTO> existingPoint = pointDAO.findById(pointId);
        if (existingPoint.isPresent()) {
            // Проверка существования функции
            if (!functionDAO.findById(functionId).isPresent()) {
                logger.error("Функция с ID {} не существует", functionId);
                return false;
            }

            PointDTO point = existingPoint.get();
            point.setFunctionId(functionId);
            point.setXValue(xValue);
            point.setYValue(yValue);

            boolean updated = pointDAO.updatePoint(point);
            if (updated) {
                logger.info("Точка с ID {} успешно обновлена", pointId);
            }
            return updated;
        }

        logger.warn("Точка с ID {} не найдена для обновления", pointId);
        return false;
    }

    public int recalculatePoints(Long functionId, String functionType) {
        logger.info("Пересчет точек для функции {} с типом {}", functionId, functionType);

        List<PointDTO> points = pointDAO.findByFunctionId(functionId);
        int updatedCount = 0;

        for (PointDTO point : points) {
            double newY = calculateFunction(functionType, point.getXValue());
            if (!point.getYValue().equals(newY)) {
                point.setYValue(newY);
                if (pointDAO.updatePoint(point)) {
                    updatedCount++;
                }
            }
        }

        logger.info("Пересчитано {} точек функции {}", updatedCount, functionId);
        return updatedCount;
    }

    public boolean deletePoint(Long pointId) {
        logger.info("Удаление точки с ID: {}", pointId);
        boolean deleted = pointDAO.deletePoint(pointId);

        if (deleted) {
            logger.info("Точка с ID {} удалена", pointId);
        } else {
            logger.warn("Точка с ID {} не найдена для удаления", pointId);
        }

        return deleted;
    }

    public int deletePointsByFunctionId(Long functionId) {
        logger.info("Удаление всех точек функции с ID: {}", functionId);
        int deletedCount = pointDAO.deleteByFunctionId(functionId);
        logger.info("Удалено {} точек функции {}", deletedCount, functionId);
        return deletedCount;
    }

    public int deletePointsByXRange(Long functionId, Double minX, Double maxX) {
        logger.info("Удаление точек функции {} в диапазоне x=[{}, {}]", functionId, minX, maxX);

        List<PointDTO> pointsToDelete = pointDAO.findByXRange(functionId, minX, maxX);
        int deletedCount = 0;

        for (PointDTO point : pointsToDelete) {
            if (pointDAO.deletePoint(point.getId())) {
                deletedCount++;
            }
        }

        logger.info("Удалено {} точек в указанном диапазоне", deletedCount);
        return deletedCount;
    }

    public boolean isXValueUnique(Long functionId, Double xValue) {
        logger.debug("Проверка уникальности X={} для функции {}", xValue, functionId);
        List<PointDTO> points = pointDAO.findByFunctionId(functionId);
        return points.stream().noneMatch(p -> p.getXValue().equals(xValue));
    }

    public PointStatistics getPointStatistics(Long functionId) {
        logger.debug("Получение статистики точек для функции {}", functionId);

        List<PointDTO> points = pointDAO.findByFunctionId(functionId);
        if (points.isEmpty()) {
            logger.warn("Нет точек для функции {}", functionId);
            return null;
        }

        double minX = points.stream().mapToDouble(PointDTO::getXValue).min().orElse(0);
        double maxX = points.stream().mapToDouble(PointDTO::getXValue).max().orElse(0);
        double minY = points.stream().mapToDouble(PointDTO::getYValue).min().orElse(0);
        double maxY = points.stream().mapToDouble(PointDTO::getYValue).max().orElse(0);
        double avgX = points.stream().mapToDouble(PointDTO::getXValue).average().orElse(0);
        double avgY = points.stream().mapToDouble(PointDTO::getYValue).average().orElse(0);

        PointStatistics stats = new PointStatistics(
                functionId,
                points.size(),
                minX,
                maxX,
                minY,
                maxY,
                avgX,
                avgY
        );

        logger.info("Статистика точек функции {}: {} точек, x_avg={}, y_avg={}",
                functionId, points.size(), avgX, avgY);

        return stats;
    }

    public static class PointStatistics {
        private final Long functionId;
        private final int pointCount;
        private final double minX;
        private final double maxX;
        private final double minY;
        private final double maxY;
        private final double averageX;
        private final double averageY;

        public PointStatistics(Long functionId, int pointCount,
                               double minX, double maxX, double minY, double maxY,
                               double averageX, double averageY) {
            this.functionId = functionId;
            this.pointCount = pointCount;
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
            this.averageX = averageX;
            this.averageY = averageY;
        }


        public Long getFunctionId() { return functionId; }
        public int getPointCount() { return pointCount; }
        public double getMinX() { return minX; }
        public double getMaxX() { return maxX; }
        public double getMinY() { return minY; }
        public double getMaxY() { return maxY; }
        public double getAverageX() { return averageX; }
        public double getAverageY() { return averageY; }

        @Override
        public String toString() {
            return String.format(
                    "PointStatistics{function=%d, points=%d, x=[%.2f, %.2f], y=[%.2f, %.2f], avgX=%.2f, avgY=%.2f}",
                    functionId, pointCount, minX, maxX, minY, maxY, averageX, averageY
            );
        }
    }
}
