package com.example.lab5.manual.service;

import com.example.lab5.manual.dao.FunctionDAO;
import com.example.lab5.manual.dao.PointDAO;
import com.example.lab5.manual.dao.UserDAO;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class PerformanceComparisonTest {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceComparisonTest.class);

    private UserService userService = new UserService(new UserDAO());
    private FunctionService functionService = new FunctionService(new FunctionDAO(), new UserDAO(), new PointDAO());
    private PointService pointService = new PointService(new PointDAO(), new FunctionDAO());
    private PerformanceComparisonService performanceService =
            new PerformanceComparisonService(userService, functionService, pointService);

    @Test
    void testPerformanceComparison() {
        logger.info("=== НАЧАЛО ТЕСТА ПРОИЗВОДИТЕЛЬНОСТИ ===");

        performanceService.generateTestData(100, 10, 10); // 100 пользователей × 10 функций × 10 точек = 10k точек

        PerformanceComparisonService.PerformanceResults results = performanceService.comparePerformance();

        logger.info("Результаты сравнения производительности:");
        logger.info("\n" + results.toMarkdownTable());

        saveResultsToFile(results);

        logger.info("=== ЗАВЕРШЕНИЕ ТЕСТА ПРОИЗВОДИТЕЛЬНОСТИ ===");
    }

    private void saveResultsToFile(PerformanceComparisonService.PerformanceResults results) {
        try {
            String filename = "performance_results.md";
            java.nio.file.Files.write(
                    java.nio.file.Paths.get(filename),
                    results.toMarkdownTable().getBytes()
            );
            logger.info("Результаты сохранены в файл: {}", filename);
        } catch (Exception e) {
            logger.error("Ошибка при сохранении результатов: {}", e.getMessage());
        }
    }
}