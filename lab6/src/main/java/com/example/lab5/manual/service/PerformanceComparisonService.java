package com.example.lab5.manual.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PerformanceComparisonService {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceComparisonService.class);

    private final UserService userService;
    private final FunctionService functionService;
    private final PointService pointService;

    public PerformanceComparisonService(UserService userService,
                                        FunctionService functionService,
                                        PointService pointService) {
        this.userService = userService;
        this.functionService = functionService;
        this.pointService = pointService;
    }

    public void generateTestData(int userCount, int functionsPerUser, int pointsPerFunction) {
        logger.info("Генерация тестовых данных: {} пользователей, {} функций на пользователя, {} точек на функцию",
                userCount, functionsPerUser, pointsPerFunction);

        List<Long> userIds = new ArrayList<>();
        List<Long> functionIds = new ArrayList<>();

        for (int i = 0; i < userCount; i++) {
            String login = "perf_test_user_" + System.currentTimeMillis() + "_" + i;
            Long userId = userService.createUser(login, "USER", "password123");
            userIds.add(userId);

            for (int j = 0; j < functionsPerUser; j++) {
                String functionName = "func_" + i + "_" + j;
                String expression = "f(x) = x^" + j;
                Long functionId = functionService.createFunction(userId, functionName, expression);
                functionIds.add(functionId);

                pointService.generateFunctionPoints(functionId, "polynomial", -10, 10, 0.1);
            }
        }

        logger.info("Сгенерировано: {} пользователей, {} функций, {} точек",
                userIds.size(), functionIds.size(), pointsPerFunction * functionIds.size());
    }

    public PerformanceResults comparePerformance() {
        PerformanceResults results = new PerformanceResults();

        testUserOperations(results);

        testFunctionOperations(results);

        testPointOperations(results);

        testComplexQueries(results);

        return results;
    }

    private void testUserOperations(PerformanceResults results) {
        long startTime, endTime;

        startTime = System.nanoTime();
        String login = "perf_test_" + System.currentTimeMillis();
        Long userId = userService.createUser(login, "ADMIN", "testpass");
        endTime = System.nanoTime();
        results.setUserCreateTime((endTime - startTime) / 1_000_000.0);

        startTime = System.nanoTime();
        userService.getUserById(userId);
        endTime = System.nanoTime();
        results.setUserReadTime((endTime - startTime) / 1_000_000.0);

        startTime = System.nanoTime();
        userService.updateUser(userId, login + "_updated", "USER", "newpass");
        endTime = System.nanoTime();
        results.setUserUpdateTime((endTime - startTime) / 1_000_000.0);

        startTime = System.nanoTime();
        userService.deleteUser(userId);
        endTime = System.nanoTime();
        results.setUserDeleteTime((endTime - startTime) / 1_000_000.0);
    }

    private void testFunctionOperations(PerformanceResults results) {
        String login = "func_perf_test_" + System.currentTimeMillis();
        Long userId = userService.createUser(login, "USER", "password");

        long startTime, endTime;

        startTime = System.nanoTime();
        Long functionId = functionService.createFunction(userId, "test_function", "f(x) = x^2");
        endTime = System.nanoTime();
        results.setFunctionCreateTime((endTime - startTime) / 1_000_000.0);

        startTime = System.nanoTime();
        functionService.getFunctionById(functionId);
        endTime = System.nanoTime();
        results.setFunctionReadTime((endTime - startTime) / 1_000_000.0);

        userService.deleteUser(userId);
    }

    private void testPointOperations(PerformanceResults results) {
        String login = "point_perf_test_" + System.currentTimeMillis();
        Long userId = userService.createUser(login, "USER", "password");
        Long functionId = functionService.createFunction(userId, "point_test_function", "f(x) = x");

        long startTime, endTime;

        startTime = System.nanoTime();
        int pointCount = pointService.generateFunctionPoints(functionId, "linear", -5, 5, 1);
        endTime = System.nanoTime();
        results.setPointCreateTime((endTime - startTime) / 1_000_000.0);

        startTime = System.nanoTime();
        pointService.getPointsByFunctionId(functionId);
        endTime = System.nanoTime();
        results.setPointReadTime((endTime - startTime) / 1_000_000.0);

        userService.deleteUser(userId);
    }

    private void testComplexQueries(PerformanceResults results) {
        long startTime, endTime;

        startTime = System.nanoTime();
        userService.getAllUsers();
        endTime = System.nanoTime();
        results.setComplexQueryTime((endTime - startTime) / 1_000_000.0);
    }

    public static class PerformanceResults {
        private double userCreateTime;
        private double userReadTime;
        private double userUpdateTime;
        private double userDeleteTime;
        private double functionCreateTime;
        private double functionReadTime;
        private double pointCreateTime;
        private double pointReadTime;
        private double complexQueryTime;

        public double getUserCreateTime() { return userCreateTime; }
        public void setUserCreateTime(double userCreateTime) {
            this.userCreateTime = userCreateTime;
        }

        public double getUserReadTime() { return userReadTime; }
        public void setUserReadTime(double userReadTime) {
            this.userReadTime = userReadTime;
        }

        public double getUserUpdateTime() { return userUpdateTime; }
        public void setUserUpdateTime(double userUpdateTime) {
            this.userUpdateTime = userUpdateTime;
        }

        public double getUserDeleteTime() { return userDeleteTime; }
        public void setUserDeleteTime(double userDeleteTime) {
            this.userDeleteTime = userDeleteTime;
        }

        public double getFunctionCreateTime() { return functionCreateTime; }
        public void setFunctionCreateTime(double functionCreateTime) {
            this.functionCreateTime = functionCreateTime;
        }

        public double getFunctionReadTime() { return functionReadTime; }
        public void setFunctionReadTime(double functionReadTime) {
            this.functionReadTime = functionReadTime;
        }

        public double getPointCreateTime() { return pointCreateTime; }
        public void setPointCreateTime(double pointCreateTime) {
            this.pointCreateTime = pointCreateTime;
        }

        public double getPointReadTime() { return pointReadTime; }
        public void setPointReadTime(double pointReadTime) {
            this.pointReadTime = pointReadTime;
        }

        public double getComplexQueryTime() { return complexQueryTime; }
        public void setComplexQueryTime(double complexQueryTime) {
            this.complexQueryTime = complexQueryTime;
        }

        public String toMarkdownTable() {
            return String.format(
                    "| Операция | JDBC (мс) |\n" +
                            "|----------|-----------|\n" +
                            "| Создание пользователя | %.3f |\n" +
                            "| Чтение пользователя | %.3f |\n" +
                            "| Обновление пользователя | %.3f |\n" +
                            "| Удаление пользователя | %.3f |\n" +
                            "| Создание функции | %.3f |\n" +
                            "| Чтение функции | %.3f |\n" +
                            "| Создание точек | %.3f |\n" +
                            "| Чтение точек | %.3f |\n" +
                            "| Сложные запросы | %.3f |\n",
                    userCreateTime,
                    userReadTime,
                    userUpdateTime,
                    userDeleteTime,
                    functionCreateTime,
                    functionReadTime,
                    pointCreateTime,
                    pointReadTime,
                    complexQueryTime
            );
        }

        public String toCSV() {
            return String.format(
                    "Операция,Время (мс)\n" +
                            "Создание пользователя,%.3f\n" +
                            "Чтение пользователя,%.3f\n" +
                            "Обновление пользователя,%.3f\n" +
                            "Удаление пользователя,%.3f\n" +
                            "Создание функции,%.3f\n" +
                            "Чтение функции,%.3f\n" +
                            "Создание точек,%.3f\n" +
                            "Чтение точек,%.3f\n" +
                            "Сложные запросы,%.3f\n",
                    userCreateTime,
                    userReadTime,
                    userUpdateTime,
                    userDeleteTime,
                    functionCreateTime,
                    functionReadTime,
                    pointCreateTime,
                    pointReadTime,
                    complexQueryTime
            );
        }
    }
}