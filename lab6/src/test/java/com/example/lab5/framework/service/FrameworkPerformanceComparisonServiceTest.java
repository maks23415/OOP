package com.example.lab5.framework.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class FrameworkPerformanceComparisonServiceTest {

    @Autowired
    private FrameworkPerformanceComparisonService performanceService;

    @BeforeEach
    void setUp() {
        // Очистка базы перед тестом для чистоты
        performanceService.cleanupTestData();
    }

    @Test
    void performanceComparisonTest() {
        System.out.println("=== НАЧАЛО ТЕСТА SPRING DATA JPA ===");

        // Генерация тестовых данных (100 пользователей × 10 функций × 10 точек = 10,000 точек)
        long startTotalTime = System.nanoTime();
        performanceService.generateTestData(100, 10, 10);
        long generationTime = System.nanoTime() - startTotalTime;

        System.out.println("=== БАЗА ДАННЫХ ЗАПОЛНЕНА 10,000+ ЗАПИСЯМИ ===");

        // Даем время базе "устаканиться"
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Замер производительности НА ЗАПОЛНЕННОЙ БАЗЕ
        FrameworkPerformanceComparisonService.PerformanceResults results = performanceService.comparePerformance();

        // Проверка, что результаты валидны
        assertTrue(results.getUserCreateTime() > 0, "User creation time should be positive");
        assertTrue(results.getUserReadTime() > 0, "User read time should be positive");
        assertTrue(results.getFunctionCreateTime() > 0, "Function creation time should be positive");
        assertTrue(results.getPointCreateTime() > 0, "Point creation time should be positive");

        // Вывод результатов
        System.out.println("=== SPRING DATA JPA PERFORMANCE RESULTS (НА БАЗЕ С 10,000+ ЗАПИСЯМИ) ===");
        System.out.println(results.toMarkdownTable());
        System.out.printf("Время генерации 10,000+ записей: %.3f мс%n", generationTime / 1_000_000.0);
        System.out.println("\n=== CSV FORMAT ===");
        System.out.println(results.toCSV());

        // Сохранение результатов в файл
        saveResultsToFile(results, generationTime);
    }

    @AfterEach
    void tearDown() {
        // Очистка базы после теста
        performanceService.cleanupTestData();
        System.out.println("=== БАЗА ОЧИЩЕНА ===");
    }

    private void saveResultsToFile(FrameworkPerformanceComparisonService.PerformanceResults results, long generationTime) {
        try {
            // Сохранение в Markdown с временем генерации
            String markdownContent = results.toMarkdownTable();

            java.nio.file.Files.write(
                    java.nio.file.Paths.get("framework_performance_10k_results.md"),
                    markdownContent.getBytes()
            );

            // Сохранение в CSV с временем генерации
            String csvContent = results.toCSV() +
                    String.format("Время генерации 10,000+ записей,%.3f", generationTime / 1_000_000.0);

            java.nio.file.Files.write(
                    java.nio.file.Paths.get("framework_performance_10k_results.csv"),
                    csvContent.getBytes()
            );

            System.out.println("Результаты сохранены в framework_performance_10k_results.md и framework_performance_10k_results.csv");
        } catch (Exception e) {
            System.err.println("Ошибка при сохранении результатов: " + e.getMessage());
        }
    }
}