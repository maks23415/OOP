package com.example.lab5.manual.service;

import com.example.lab5.manual.dao.FunctionDAO;
import com.example.lab5.manual.dao.PointDAO;
import com.example.lab5.manual.dao.UserDAO;
import com.example.lab5.manual.dto.FunctionDTO;
import com.example.lab5.manual.dto.PointDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class FunctionService {
    private static final Logger logger = LoggerFactory.getLogger(FunctionService.class);
    private final FunctionDAO functionDAO;
    private final UserDAO userDAO;
    private final PointDAO pointDAO;

    public FunctionService() {
        this.functionDAO = new FunctionDAO();
        this.userDAO = new UserDAO();
        this.pointDAO = new PointDAO();
    }

    public FunctionService(FunctionDAO functionDAO, UserDAO userDAO, PointDAO pointDAO) {
        this.functionDAO = functionDAO;
        this.userDAO = userDAO;
        this.pointDAO = pointDAO;
    }

    public Long createFunction(Long userId, String name, String signature) {
        logger.info("Создание функции: user={}, name={}, signature={}", userId, name, signature);

        if (!userDAO.findById(userId).isPresent()) {
            logger.error("Пользователь с ID {} не существует", userId);
            throw new IllegalArgumentException("User with ID " + userId + " does not exist");
        }

        FunctionDTO function = new FunctionDTO(userId, name, signature);
        Long functionId = functionDAO.createFunction(function);
        logger.info("Создана функция с ID: {}", functionId);
        return functionId;
    }

    public Optional<FunctionDTO> getFunctionById(Long id) {
        logger.debug("Поиск функции по ID: {}", id);
        return functionDAO.findById(id);
    }

    public List<FunctionDTO> getFunctionsByUserId(Long userId) {
        logger.debug("Поиск функций пользователя с ID: {}", userId);
        return functionDAO.findByUserId(userId);
    }

    public List<FunctionDTO> getFunctionsByName(String name) {
        logger.debug("Поиск функций по имени: {}", name);
        return functionDAO.findByName(name);
    }

    public List<FunctionDTO> getAllFunctions() {
        logger.debug("Получение всех функций");
        return functionDAO.findAll();
    }

    public List<FunctionDTO> getFunctionsWithPointCount() {
        logger.debug("Получение функций со статистикой по точкам");
        List<FunctionDTO> functions = functionDAO.findAll();

        for (FunctionDTO function : functions) {
            List<PointDTO> points = pointDAO.findByFunctionId(function.getId());
            logger.debug("Функция {} имеет {} точек", function.getName(), points.size());
        }

        return functions;
    }

    public boolean updateFunction(Long functionId, Long userId, String name, String signature) {
        logger.info("Обновление функции с ID: {}", functionId);

        Optional<FunctionDTO> existingFunction = functionDAO.findById(functionId);
        if (existingFunction.isPresent()) {
            if (!userDAO.findById(userId).isPresent()) {
                logger.error("Пользователь с ID {} не существует", userId);
                return false;
            }

            FunctionDTO function = existingFunction.get();
            function.setUserId(userId);
            function.setName(name);
            function.setSignature(signature);

            boolean updated = functionDAO.updateFunction(function);
            if (updated) {
                logger.info("Функция с ID {} успешно обновлена", functionId);
            }
            return updated;
        }

        logger.warn("Функция с ID {} не найдена для обновления", functionId);
        return false;
    }

    public boolean updateFunctionSignature(Long functionId, String newSignature) {
        logger.info("Обновление сигнатуры функции с ID: {}", functionId);

        Optional<FunctionDTO> existingFunction = functionDAO.findById(functionId);
        if (existingFunction.isPresent()) {
            FunctionDTO function = existingFunction.get();
            function.setSignature(newSignature);

            boolean updated = functionDAO.updateFunction(function);
            if (updated) {
                logger.info("Сигнатура функции с ID {} обновлена", functionId);
            }
            return updated;
        }

        logger.warn("Функция с ID {} не найдена", functionId);
        return false;
    }

    public boolean deleteFunction(Long functionId) {
        logger.info("Удаление функции с ID: {}", functionId);

        int deletedPoints = pointDAO.deleteByFunctionId(functionId);
        logger.info("Удалено {} точек функции с ID: {}", deletedPoints, functionId);

        boolean deleted = functionDAO.deleteFunction(functionId);
        if (deleted) {
            logger.info("Функция с ID {} и все её точки удалены", functionId);
        } else {
            logger.warn("Функция с ID {} не найдена для удаления", functionId);
        }

        return deleted;
    }

    public int deleteFunctionsByUserId(Long userId) {
        logger.info("Удаление всех функций пользователя с ID: {}", userId);

        List<FunctionDTO> userFunctions = functionDAO.findByUserId(userId);
        int totalDeleted = 0;

        for (FunctionDTO function : userFunctions) {
            if (deleteFunction(function.getId())) {
                totalDeleted++;
            }
        }

        logger.info("Удалено {} функций пользователя с ID: {}", totalDeleted, userId);
        return totalDeleted;
    }

    public boolean validateFunctionName(Long userId, String functionName) {
        logger.debug("Проверка уникальности имени функции для пользователя: {}", userId);

        List<FunctionDTO> userFunctions = functionDAO.findByUserId(userId);
        return userFunctions.stream()
                .noneMatch(func -> func.getName().equalsIgnoreCase(functionName));
    }

    public FunctionStatistics getFunctionStatistics(Long functionId) {
        logger.debug("Получение статистики для функции с ID: {}", functionId);

        Optional<FunctionDTO> function = functionDAO.findById(functionId);
        if (function.isPresent()) {
            List<PointDTO> points = pointDAO.findByFunctionId(functionId);

            double minX = points.stream().mapToDouble(PointDTO::getXValue).min().orElse(0);
            double maxX = points.stream().mapToDouble(PointDTO::getXValue).max().orElse(0);
            double minY = points.stream().mapToDouble(PointDTO::getYValue).min().orElse(0);
            double maxY = points.stream().mapToDouble(PointDTO::getYValue).max().orElse(0);
            double avgY = points.stream().mapToDouble(PointDTO::getYValue).average().orElse(0);

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
