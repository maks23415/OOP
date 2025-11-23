package com.example.lab5;

import com.example.lab5.framework.search.BreadthFirstSearch;
import com.example.lab5.framework.search.DepthFirstSearch;
import com.example.lab5.framework.search.HierarchySearch;
import com.example.lab5.framework.entity.User;
import com.example.lab5.framework.repository.UserRepository;
import com.example.lab5.manual.search.SearchService;
import com.example.lab5.manual.dao.UserDAO;
import com.example.lab5.manual.dao.FunctionDAO;
import com.example.lab5.manual.dao.PointDAO;
import com.example.lab5.manual.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
public class SortingPerformanceComparisonTest {

    @Autowired
    private BreadthFirstSearch bfs;

    @Autowired
    private DepthFirstSearch dfs;

    @Autowired
    private HierarchySearch hierarchySearch;

    @Autowired
    private UserRepository userRepository;

    private static final int DATASET_SIZE = 10000;
    private final List<PerformanceResult> results = new ArrayList<>();

    @Test
    void compareAllSortingPerformance() throws IOException {
        // Создаем тестовые данные
        createTestData();

        // Тестируем Framework реализацию
        testFrameworkPerformance();

        // Тестируем Manual реализацию
        testManualPerformance();

        // Генерируем общую таблицу результатов
        generateResultsTable();
    }

    private void testFrameworkPerformance() {
        // BFS сортировка
        long bfsLoginTime = measurePerformance(() ->
                        bfs.findUsersWithSortingBFS("", "login", "ASC"),
                "Framework", "BFS", "login_ASC"
        );

        long bfsRoleTime = measurePerformance(() ->
                        bfs.findUsersWithSortingBFS("", "role", "ASC"),
                "Framework", "BFS", "role_ASC"
        );

        // DFS сортировка
        long dfsLoginTime = measurePerformance(() ->
                        dfs.findUsersWithSortingDFS("", "login", "ASC"),
                "Framework", "DFS", "login_ASC"
        );

        long dfsRoleTime = measurePerformance(() ->
                        dfs.findUsersWithSortingDFS("", "role", "ASC"),
                "Framework", "DFS", "role_ASC"
        );

        // Hierarchy сортировка
        long hierarchyTime = measurePerformance(() ->
                        hierarchySearch.findUsersWithHierarchySorting("login", "ASC"),
                "Framework", "Hierarchy", "login_ASC"
        );
    }

    private void testManualPerformance() {
        SearchService searchService = new SearchService(new UserDAO(), new FunctionDAO(), new PointDAO());
        List<UserDTO> testData = createManualTestData();

        // Manual сортировка по логину
        long manualLoginTime = measurePerformance(() ->
                        searchService.sortUsers(new ArrayList<>(testData), "login", "asc"),
                "Manual", "JDBC", "login_ASC"
        );

        // Manual сортировка по роли
        long manualRoleTime = measurePerformance(() ->
                        searchService.sortUsers(new ArrayList<>(testData), "role", "asc"),
                "Manual", "JDBC", "role_ASC"
        );

        // Manual множественная сортировка
        Map<String, String> multiSort = new LinkedHashMap<>();
        multiSort.put("role", "asc");
        multiSort.put("login", "asc");

        long manualMultiTime = measurePerformance(() ->
                        searchService.sortUsersByMultipleFields(new ArrayList<>(testData), multiSort),
                "Manual", "JDBC", "multi_sort"
        );
    }

    private long measurePerformance(Runnable operation, String implementation, String algorithm, String sortType) {
        // Прогрев
        for (int i = 0; i < 3; i++) {
            operation.run();
        }

        // Измерение времени
        long startTime = System.nanoTime();
        operation.run();
        long endTime = System.nanoTime();

        long duration = endTime - startTime;
        results.add(new PerformanceResult(implementation, algorithm, sortType, duration));

        return duration;
    }

    private void generateResultsTable() throws IOException {
        Files.createDirectories(Paths.get("docs"));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("docs/sorting_performance.md"))) {
            writer.write("# Сравнение производительности сортировок\n\n");

            writer.write("| Реализация | Алгоритм | Тип сортировки | Время (нс) | Время (мс) |\n");
            writer.write("|------------|----------|----------------|------------|------------|\n");

            for (PerformanceResult result : results) {
                writer.write(String.format("| %s | %s | %s | %d | %.3f |\n",
                        result.implementation,
                        result.algorithm,
                        result.sortType,
                        result.duration,
                        result.duration / 1_000_000.0
                ));
            }

            writer.write("\n## Сводная таблица (время в миллисекундах)\n\n");
            writer.write("| Реализация | Алгоритм | login_ASC | role_ASC | multi_sort |\n");
            writer.write("|------------|----------|-----------|----------|------------|\n");

            // Группируем результаты для сводной таблицы
            Map<String, Map<String, Double>> summary = new LinkedHashMap<>();

            for (PerformanceResult result : results) {
                summary.computeIfAbsent(result.implementation + "|" + result.algorithm,
                        k -> new HashMap<>()).put(result.sortType, result.duration / 1_000_000.0);
            }

            for (Map.Entry<String, Map<String, Double>> entry : summary.entrySet()) {
                String[] parts = entry.getKey().split("\\|");
                String implementation = parts[0];
                String algorithm = parts[1];
                Map<String, Double> times = entry.getValue();

                writer.write(String.format("| %s | %s | %.3f | %.3f | %.3f |\n",
                        implementation,
                        algorithm,
                        times.getOrDefault("login_ASC", 0.0),
                        times.getOrDefault("role_ASC", 0.0),
                        times.getOrDefault("multi_sort", 0.0)
                ));
            }
        }
    }

    private void createTestData() {
        if (userRepository.count() < DATASET_SIZE) {
            List<User> users = new ArrayList<>();
            Random random = new Random();
            String[] roles = {"USER", "ADMIN", "MODERATOR"};

            for (int i = 0; i < DATASET_SIZE; i++) {
                User user = new User();
                user.setLogin("user_" + i + "_" + random.nextInt(10000));
                user.setRole(roles[random.nextInt(roles.length)]);
                user.setPassword("pass_" + i);
                users.add(user);
            }

            userRepository.saveAll(users);
        }
    }

    private List<UserDTO> createManualTestData() {
        List<UserDTO> users = new ArrayList<>();
        Random random = new Random();
        String[] roles = {"USER", "ADMIN", "MODERATOR"};

        for (int i = 0; i < DATASET_SIZE; i++) {
            String login = "user_" + i + "_" + random.nextInt(10000);
            String role = roles[random.nextInt(roles.length)];
            UserDTO user = new UserDTO(login, role, "password" + i);
            user.setId((long) i);
            users.add(user);
        }

        return users;
    }

    private record PerformanceResult(String implementation, String algorithm, String sortType, long duration) {}
}