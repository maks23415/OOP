package com.example.lab5.framework.service;

import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FrameworkPerformanceComparisonService {
    private static final Logger logger = LoggerFactory.getLogger(FrameworkPerformanceComparisonService.class);

    @Autowired
    public UserService userService;

    @Autowired
    public FunctionService functionService;

    @Autowired
    public PointService pointService;

    // Храним ссылки на тестовые данные для очистки
    private List<User> testUsers = new ArrayList<>();
    private List<Function> testFunctions = new ArrayList<>();

    public void generateTestData(int userCount, int functionsPerUser, int pointsPerFunction) {
        logger.info("Генерация тестовых данных для Framework: {} пользователей, {} функций на пользователя, {} точек на функцию",
                userCount, functionsPerUser, pointsPerFunction);

        testUsers.clear();
        testFunctions.clear();
        int totalPoints = 0;

        for (int i = 0; i < userCount; i++) {
            String login = "framework_perf_user_" + System.currentTimeMillis() + "_" + i;
            var user = userService.createUser(login, "USER", "password123");
            testUsers.add(user);

            for (int j = 0; j < functionsPerUser; j++) {
                String functionName = "framework_func_" + i + "_" + j;
                String expression = "f(x) = x^" + j;
                var function = functionService.createFunction(user.getId(), functionName, expression);
                testFunctions.add(function);

                int pointsGenerated = pointService.generateFunctionPoints(function.getId(), "polynomial", -10, 10, 0.1);
                totalPoints += pointsGenerated;
            }
        }

        logger.info("Framework: Сгенерировано: {} пользователей, {} функций, {} точек",
                testUsers.size(), testFunctions.size(), totalPoints);
    }

    public PerformanceResults comparePerformance() {
        // Проверяем что база не пустая
        long userCount = userService.getAllUsers().size();
        long functionCount = functionService.getAllFunctions().size();

        System.out.printf("=== ПРОВЕРКА БАЗЫ: %d пользователей, %d функций ===%n", userCount, functionCount);

        if (userCount < 50) {
            System.out.println("ПРЕДУПРЕЖДЕНИЕ: База данных может быть недостаточно заполнена для реалистичного тестирования!");
        }

        PerformanceResults results = new PerformanceResults();

        testUserOperations(results);
        testFunctionOperations(results);
        testPointOperations(results);
        testComplexQueries(results);

        return results;
    }

    private void testUserOperations(PerformanceResults results) {
        long startTime, endTime;

        // Create User - время создания ОДНОГО пользователя НА ЗАГРУЖЕННОЙ БАЗЕ
        startTime = System.nanoTime();
        String login = "framework_perf_test_" + System.currentTimeMillis();
        User user = userService.createUser(login, "ADMIN", "testpass");
        endTime = System.nanoTime();
        results.setUserCreateTime((endTime - startTime) / 1_000_000.0);

        // Read User - время чтения СУЩЕСТВУЮЩЕГО пользователя ИЗ ЗАГРУЖЕННОЙ БАЗЫ
        User existingUser = testUsers.get(0);
        startTime = System.nanoTime();
        userService.getUserById(existingUser.getId());
        endTime = System.nanoTime();
        results.setUserReadTime((endTime - startTime) / 1_000_000.0);

        // Update User - время обновления СУЩЕСТВУЮЩЕГО пользователя В ЗАГРУЖЕННОЙ БАЗЕ
        startTime = System.nanoTime();
        userService.updateUser(existingUser.getId(), existingUser.getLogin() + "_updated", "USER", "newpass");
        endTime = System.nanoTime();
        results.setUserUpdateTime((endTime - startTime) / 1_000_000.0);

        // Delete User - время удаления НОВОГО пользователя ИЗ ЗАГРУЖЕННОЙ БАЗЫ
        startTime = System.nanoTime();
        userService.deleteUser(user.getId());
        endTime = System.nanoTime();
        results.setUserDeleteTime((endTime - startTime) / 1_000_000.0);
    }

    private void testFunctionOperations(PerformanceResults results) {
        long startTime, endTime;

        // Используем существующего пользователя из тестовых данных
        User existingUser = testUsers.get(0);

        // Create Function - время создания ОДНОЙ функции НА ЗАГРУЖЕННОЙ БАЗЕ
        startTime = System.nanoTime();
        Function function = functionService.createFunction(existingUser.getId(), "test_function_perf", "f(x) = x^2");
        endTime = System.nanoTime();
        results.setFunctionCreateTime((endTime - startTime) / 1_000_000.0);

        // Read Function - время чтения ОДНОЙ функции ИЗ ЗАГРУЖЕННОЙ БАЗЫ
        startTime = System.nanoTime();
        functionService.getFunctionById(function.getId());
        endTime = System.nanoTime();
        results.setFunctionReadTime((endTime - startTime) / 1_000_000.0);

        // Удаляем тестовую функцию
        functionService.deleteFunction(function.getId());
    }

    private void testPointOperations(PerformanceResults results) {
        long startTime, endTime;

        // Используем существующую функцию из тестовых данных
        Function existingFunction = testFunctions.get(0);

        // Create Points - время создания точек ДЛЯ СУЩЕСТВУЮЩЕЙ ФУНКЦИИ
        startTime = System.nanoTime();
        int pointCount = pointService.generateFunctionPoints(existingFunction.getId(), "linear", -5, 5, 1);
        endTime = System.nanoTime();
        results.setPointCreateTime((endTime - startTime) / 1_000_000.0);

        // Read Points - время чтения точек ИЗ ЗАГРУЖЕННОЙ БАЗЫ
        startTime = System.nanoTime();
        pointService.getPointsByFunctionId(existingFunction.getId());
        endTime = System.nanoTime();
        results.setPointReadTime((endTime - startTime) / 1_000_000.0);
    }

    private void testComplexQueries(PerformanceResults results) {
        long startTime, endTime;

        // Complex Query - время выполнения сложных запросов НА РЕАЛЬНЫХ ДАННЫХ
        startTime = System.nanoTime();

        // Запрос 1: Получить всех пользователей
        var allUsers = userService.getAllUsers();

        // Запрос 2: Для каждого пользователя получить все функции
        for (var user : allUsers) {
            var userFunctions = functionService.getFunctionsByUserId(user.getId());

            // Запрос 3: Для каждой функции получить точки (только для первых 3 функций)
            for (int i = 0; i < Math.min(userFunctions.size(), 3); i++) {
                pointService.getPointsByFunctionId(userFunctions.get(i).getId());
            }
        }

        endTime = System.nanoTime();
        results.setComplexQueryTime((endTime - startTime) / 1_000_000.0);
    }

    // Метод для очистки тестовых данных
    public void cleanupTestData() {
        logger.info("Очистка тестовых данных Framework: {} пользователей", testUsers.size());
        for (User user : testUsers) {
            userService.deleteUser(user.getId());
        }
        testUsers.clear();
        testFunctions.clear();
    }

    // Метод для получения всех функций (для проверки)
    public List<Function> getAllFunctions() {
        return functionService.getAllFunctions();
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

        // Getters and Setters
        public double getUserCreateTime() {
            return userCreateTime;
        }

        public void setUserCreateTime(double userCreateTime) {
            this.userCreateTime = userCreateTime;
        }

        public double getUserReadTime() {
            return userReadTime;
        }

        public void setUserReadTime(double userReadTime) {
            this.userReadTime = userReadTime;
        }

        public double getUserUpdateTime() {
            return userUpdateTime;
        }

        public void setUserUpdateTime(double userUpdateTime) {
            this.userUpdateTime = userUpdateTime;
        }

        public double getUserDeleteTime() {
            return userDeleteTime;
        }

        public void setUserDeleteTime(double userDeleteTime) {
            this.userDeleteTime = userDeleteTime;
        }

        public double getFunctionCreateTime() {
            return functionCreateTime;
        }

        public void setFunctionCreateTime(double functionCreateTime) {
            this.functionCreateTime = functionCreateTime;
        }

        public double getFunctionReadTime() {
            return functionReadTime;
        }

        public void setFunctionReadTime(double functionReadTime) {
            this.functionReadTime = functionReadTime;
        }

        public double getPointCreateTime() {
            return pointCreateTime;
        }

        public void setPointCreateTime(double pointCreateTime) {
            this.pointCreateTime = pointCreateTime;
        }

        public double getPointReadTime() {
            return pointReadTime;
        }

        public void setPointReadTime(double pointReadTime) {
            this.pointReadTime = pointReadTime;
        }

        public double getComplexQueryTime() {
            return complexQueryTime;
        }

        public void setComplexQueryTime(double complexQueryTime) {
            this.complexQueryTime = complexQueryTime;
        }

        public String toMarkdownTable() {
            return String.format(
                    "| Операция | Spring Data JPA (мс) |\n" +
                            "|----------|---------------------|\n" +
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