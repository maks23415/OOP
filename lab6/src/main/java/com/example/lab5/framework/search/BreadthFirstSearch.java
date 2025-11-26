package com.example.lab5.framework.search;

import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.entity.Point;
import com.example.lab5.framework.entity.User;
import com.example.lab5.framework.repository.FunctionRepository;
import com.example.lab5.framework.repository.PointRepository;
import com.example.lab5.framework.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class BreadthFirstSearch {

    private static final Logger logger = LoggerFactory.getLogger(BreadthFirstSearch.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private PointRepository pointRepository;

    /**
     * Поиск в ширину по иерархии пользователей
     */
    public List<User> bfsUsers(String searchCriteria) {
        logger.info("Starting BFS search for users with criteria: {}", searchCriteria);

        List<User> allUsers = userRepository.findAll();
        List<User> result = new ArrayList<>();
        Queue<User> queue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();

        // Добавляем всех пользователей в очередь
        queue.addAll(allUsers);

        while (!queue.isEmpty()) {
            User currentUser = queue.poll();

            if (!visited.contains(currentUser.getId())) {
                visited.add(currentUser.getId());

                if (matchesUserCriteria(currentUser, searchCriteria)) {
                    result.add(currentUser);
                }

                // Обрабатываем функции пользователя на следующем уровне
                processUserFunctionsBFS(currentUser, visited);

                logger.debug("Processed user in BFS: {}", currentUser.getLogin());
            }
        }

        logger.info("BFS search completed. Found {} users", result.size());
        return result;
    }

    private void processUserFunctionsBFS(User user, Set<Long> visited) {
        List<Function> functions = functionRepository.findByUser(user);

        for (Function function : functions) {
            if (!visited.contains(function.getId())) {
                visited.add(function.getId());
                logger.debug("Processing function in BFS: {}", function.getName());

                // Обрабатываем точки функции
                processFunctionPointsBFS(function, visited);
            }
        }
    }

    private void processFunctionPointsBFS(Function function, Set<Long> visited) {
        List<Point> points = pointRepository.findByFunction(function);

        for (Point point : points) {
            if (!visited.contains(point.getId())) {
                visited.add(point.getId());
                logger.debug("Processing point in BFS: ({}, {})", point.getXValue(), point.getYValue());
            }
        }
    }

    /**
     * Множественный поиск с различными критериями
     */
    public List<User> findUsersWithMultipleCriteriaBFS(Map<String, String> criteria) {
        logger.info("Starting BFS search with multiple criteria: {}", criteria);

        List<User> allUsers = userRepository.findAll();
        List<User> result = new ArrayList<>();

        for (User user : allUsers) {
            if (matchesMultipleCriteria(user, criteria)) {
                result.add(user);
            }
        }

        logger.info("BFS search with multiple criteria completed. Found {} users", result.size());
        return result;
    }

    /**
     * Поиск с сортировкой по полям - ИСПРАВЛЕННАЯ ВЕРСИЯ
     */
    public List<User> findUsersWithSortingBFS(String criteria, String sortField, String sortDirection) {
        logger.info("Starting BFS search with criteria: {} and sorting by {} {}",
                criteria, sortField, sortDirection);

        List<User> users = bfsUsers(criteria);

        // Сортировка результатов с обработкой null значений
        users.sort((u1, u2) -> {
            int result;
            switch (sortField.toLowerCase()) {
                case "login":
                    result = Objects.compare(u1.getLogin(), u2.getLogin(),
                            Comparator.nullsFirst(String::compareTo));
                    break;
                case "role":
                    result = Objects.compare(u1.getRole(), u2.getRole(),
                            Comparator.nullsFirst(String::compareTo));
                    break;
                case "id":
                    result = Objects.compare(u1.getId(), u2.getId(),
                            Comparator.nullsFirst(Long::compareTo));
                    break;
                default:
                    result = Objects.compare(u1.getId(), u2.getId(),
                            Comparator.nullsFirst(Long::compareTo));
            }
            return "DESC".equalsIgnoreCase(sortDirection) ? -result : result;
        });

        logger.info("BFS search with sorting completed. Found {} users", users.size());
        return users;
    }

    /**
     * Множественная сортировка по нескольким полям
     */
    public List<User> findUsersWithMultipleSortingBFS(String criteria, Map<String, String> sortCriteria) {
        logger.info("Starting BFS search with criteria: {} and multiple sorting: {}",
                criteria, sortCriteria);

        List<User> users = bfsUsers(criteria);

        users.sort((u1, u2) -> {
            for (Map.Entry<String, String> entry : sortCriteria.entrySet()) {
                String field = entry.getKey();
                String direction = entry.getValue();

                int result = compareUsersByField(u1, u2, field);
                if (result != 0) {
                    return "DESC".equalsIgnoreCase(direction) ? -result : result;
                }
            }
            return 0;
        });

        logger.info("BFS search with multiple sorting completed. Found {} users", users.size());
        return users;
    }

    private int compareUsersByField(User u1, User u2, String field) {
        switch (field.toLowerCase()) {
            case "login":
                return Objects.compare(u1.getLogin(), u2.getLogin(),
                        Comparator.nullsFirst(String::compareTo));
            case "role":
                return Objects.compare(u1.getRole(), u2.getRole(),
                        Comparator.nullsFirst(String::compareTo));
            case "id":
                return Objects.compare(u1.getId(), u2.getId(),
                        Comparator.nullsFirst(Long::compareTo));
            default:
                return Objects.compare(u1.getId(), u2.getId(),
                        Comparator.nullsFirst(Long::compareTo));
        }
    }

    private boolean matchesUserCriteria(User user, String criteria) {
        if (criteria == null || criteria.isEmpty()) {
            return true;
        }
        return user.getLogin().toLowerCase().contains(criteria.toLowerCase()) ||
                user.getRole().toLowerCase().contains(criteria.toLowerCase());
    }

    private boolean matchesMultipleCriteria(User user, Map<String, String> criteria) {
        for (Map.Entry<String, String> entry : criteria.entrySet()) {
            switch (entry.getKey()) {
                case "login":
                    if (!user.getLogin().contains(entry.getValue())) {
                        return false;
                    }
                    break;
                case "role":
                    if (!user.getRole().equals(entry.getValue())) {
                        return false;
                    }
                    break;
            }
        }
        return true;
    }
}