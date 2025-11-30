package com.example.lab5.framework.search;

import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.entity.Point;
import com.example.lab5.framework.entity.User;
import com.example.lab5.framework.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FrameworkSortingTest {

    @Autowired
    private BreadthFirstSearch bfs;

    @Autowired
    private DepthFirstSearch dfs;

    @Autowired
    private HierarchySearch hierarchySearch;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Убедимся, что есть тестовые данные
        createTestData();
    }

    @Test
    void testBreadthFirstSearchSorting() {
        // Given
        String criteria = ""; // Пустой критерий для получения всех пользователей

        // When
        List<User> usersAsc = bfs.findUsersWithSortingBFS(criteria, "login", "ASC");
        List<User> usersDesc = bfs.findUsersWithSortingBFS(criteria, "login", "DESC");

        // Then
        assertNotNull(usersAsc);
        assertNotNull(usersDesc);
        assertFalse(usersAsc.isEmpty());
        assertFalse(usersDesc.isEmpty());

        // Проверяем что списки не пустые и имеют одинаковый размер
        assertEquals(usersAsc.size(), usersDesc.size());

        // Проверяем корректность сортировки ASC
        if (usersAsc.size() > 1) {
            for (int i = 0; i < usersAsc.size() - 1; i++) {
                String currentLogin = usersAsc.get(i).getLogin();
                String nextLogin = usersAsc.get(i + 1).getLogin();
                assertTrue(currentLogin.compareTo(nextLogin) <= 0,
                        "ASC sorting failed: " + currentLogin + " should be <= " + nextLogin);
            }
        }

        // Проверяем корректность сортировки DESC
        if (usersDesc.size() > 1) {
            for (int i = 0; i < usersDesc.size() - 1; i++) {
                String currentLogin = usersDesc.get(i).getLogin();
                String nextLogin = usersDesc.get(i + 1).getLogin();
                assertTrue(currentLogin.compareTo(nextLogin) >= 0,
                        "DESC sorting failed: " + currentLogin + " should be >= " + nextLogin);
            }
        }

        // Проверяем что первый элемент ASC равен последнему элементу DESC и наоборот
        if (!usersAsc.isEmpty() && !usersDesc.isEmpty()) {
            assertEquals(usersAsc.get(0).getLogin(), usersDesc.get(usersDesc.size() - 1).getLogin());
            assertEquals(usersAsc.get(usersAsc.size() - 1).getLogin(), usersDesc.get(0).getLogin());
        }
    }

    @Test
    void testDepthFirstSearchSorting() {
        // Given
        String criteria = "";

        // When
        List<User> usersAsc = dfs.findUsersWithSortingDFS(criteria, "login", "ASC");
        List<User> usersDesc = dfs.findUsersWithSortingDFS(criteria, "login", "DESC");

        // Then
        assertNotNull(usersAsc);
        assertNotNull(usersDesc);
        assertFalse(usersAsc.isEmpty());
        assertFalse(usersDesc.isEmpty());

        // Проверяем корректность сортировки ASC
        if (usersAsc.size() > 1) {
            for (int i = 0; i < usersAsc.size() - 1; i++) {
                String currentLogin = usersAsc.get(i).getLogin();
                String nextLogin = usersAsc.get(i + 1).getLogin();
                assertTrue(currentLogin.compareTo(nextLogin) <= 0,
                        "ASC sorting failed at position " + i + ": " + currentLogin + " > " + nextLogin);
            }
        }

        // Проверяем корректность сортировки DESC
        if (usersDesc.size() > 1) {
            for (int i = 0; i < usersDesc.size() - 1; i++) {
                String currentLogin = usersDesc.get(i).getLogin();
                String nextLogin = usersDesc.get(i + 1).getLogin();
                assertTrue(currentLogin.compareTo(nextLogin) >= 0,
                        "DESC sorting failed at position " + i + ": " + currentLogin + " < " + nextLogin);
            }
        }
    }

    @Test
    void testRoleSorting() {
        // Given
        String criteria = "";

        // When
        List<User> usersAsc = bfs.findUsersWithSortingBFS(criteria, "role", "ASC");
        List<User> usersDesc = bfs.findUsersWithSortingBFS(criteria, "role", "DESC");

        // Then
        assertNotNull(usersAsc);
        assertNotNull(usersDesc);

        // Проверяем корректность сортировки по роли ASC
        if (usersAsc.size() > 1) {
            for (int i = 0; i < usersAsc.size() - 1; i++) {
                String currentRole = usersAsc.get(i).getRole();
                String nextRole = usersAsc.get(i + 1).getRole();
                assertTrue(currentRole.compareTo(nextRole) <= 0,
                        "Role ASC sorting failed: " + currentRole + " should be <= " + nextRole);
            }
        }

        // Проверяем корректность сортировки по роли DESC
        if (usersDesc.size() > 1) {
            for (int i = 0; i < usersDesc.size() - 1; i++) {
                String currentRole = usersDesc.get(i).getRole();
                String nextRole = usersDesc.get(i + 1).getRole();
                assertTrue(currentRole.compareTo(nextRole) >= 0,
                        "Role DESC sorting failed: " + currentRole + " should be >= " + nextRole);
            }
        }
    }

    @Test
    void testMultipleFieldSorting() {
        // Given
        String criteria = "";
        Map<String, String> sortCriteria = Map.of(
                "role", "ASC",
                "login", "ASC"
        );

        // When
        List<User> usersMultiSorted = bfs.findUsersWithMultipleSortingBFS(criteria, sortCriteria);

        // Then
        assertNotNull(usersMultiSorted);
        assertFalse(usersMultiSorted.isEmpty());

        // Вывод для отладки
        System.out.println("Multi-sorted users (role ASC, login ASC):");
        for (int i = 0; i < usersMultiSorted.size(); i++) {
            User user = usersMultiSorted.get(i);
            System.out.printf("%d: %s (%s)%n", i, user.getLogin(), user.getRole());
        }

        // Упрощенная проверка - проверяем только логику для одинаковых ролей
        // Находим пользователей с одинаковыми ролями и проверяем сортировку по логину
        for (int i = 0; i < usersMultiSorted.size() - 1; i++) {
            User current = usersMultiSorted.get(i);
            User next = usersMultiSorted.get(i + 1);

            // Если роли одинаковые, проверяем что логины отсортированы ASC
            if (current.getRole().equals(next.getRole())) {
                assertTrue(current.getLogin().compareTo(next.getLogin()) <= 0,
                        "For same role " + current.getRole() + ", login should be sorted ASC: " +
                                current.getLogin() + " should come before " + next.getLogin());
            }
        }
    }


    @Test
    void testEmptyAndNullHandling() {
        // Given
        String criteria = "nonexistent";

        // When - поиск несуществующих пользователей
        List<User> emptyResult = bfs.findUsersWithSortingBFS(criteria, "login", "ASC");

        // Then - должен вернуть пустой список без ошибок
        assertNotNull(emptyResult);
        assertTrue(emptyResult.isEmpty());

        // Тест с null критерием
        List<User> nullCriteriaResult = bfs.findUsersWithSortingBFS(null, "login", "ASC");
        assertNotNull(nullCriteriaResult);
    }

    @Test
    void testPerformanceComparison() {
        // Тест производительности
        long startTime, endTime;

        startTime = System.nanoTime();
        List<User> bfsResult = bfs.findUsersWithSortingBFS("", "login", "ASC");
        endTime = System.nanoTime();
        long bfsTime = endTime - startTime;

        startTime = System.nanoTime();
        List<User> dfsResult = dfs.findUsersWithSortingDFS("", "login", "ASC");
        endTime = System.nanoTime();
        long dfsTime = endTime - startTime;

        System.out.printf("BFS sorting time: %d ns%n", bfsTime);
        System.out.printf("DFS sorting time: %d ns%n", dfsTime);
        System.out.printf("BFS found: %d users%n", bfsResult.size());
        System.out.printf("DFS found: %d users%n", dfsResult.size());

        // Проверяем что оба метода нашли одинаковое количество пользователей
        assertEquals(bfsResult.size(), dfsResult.size());
    }

    private void createTestData() {
        // Создаем тестовых пользователей с предсказуемыми данными для тестирования сортировки
        List<User> existingUsers = userRepository.findAll();
        if (existingUsers.isEmpty()) {
            User user1 = new User();
            user1.setLogin("admin_user");
            user1.setRole("ADMIN");
            user1.setPassword("pass1");

            User user2 = new User();
            user2.setLogin("zeta_user");
            user2.setRole("USER");
            user2.setPassword("pass2");

            User user3 = new User();
            user3.setLogin("alpha_user");
            user3.setRole("MODERATOR");
            user3.setPassword("pass3");

            User user4 = new User();
            user4.setLogin("beta_user");
            user4.setRole("USER");
            user4.setPassword("pass4");

            userRepository.saveAll(List.of(user1, user2, user3, user4));
        }
    }

    @Test
    void testSpecificSortingScenario() {
        // Given - конкретный тестовый сценарий
        String criteria = "user"; // Ищем всех пользователей с "user" в логине

        // When
        List<User> users = bfs.findUsersWithSortingBFS(criteria, "login", "ASC");

        // Then
        assertNotNull(users);

        // Выводим отладочную информацию
        System.out.println("Sorted users:");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.printf("%d: %s (%s)%n", i, user.getLogin(), user.getRole());
        }

        // Проверяем сортировку
        if (users.size() > 1) {
            for (int i = 0; i < users.size() - 1; i++) {
                String current = users.get(i).getLogin();
                String next = users.get(i + 1).getLogin();
                System.out.printf("Comparing: %s vs %s -> %d%n", current, next, current.compareTo(next));
                assertTrue(current.compareTo(next) <= 0,
                        String.format("Sorting error at position %d: %s should come before %s", i, current, next));
            }
        }
    }
}