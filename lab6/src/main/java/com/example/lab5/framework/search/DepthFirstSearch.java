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
public class DepthFirstSearch {

    private static final Logger logger = LoggerFactory.getLogger(DepthFirstSearch.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private PointRepository pointRepository;

    public List<User> dfsUsers(String searchCriteria) {
        logger.info("Starting DFS search for users with criteria: {}", searchCriteria);

        List<User> allUsers = userRepository.findAll();
        List<User> result = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        for (User user : allUsers) {
            if (!visited.contains(user.getId())) {
                dfsUserTraversal(user, visited, result, searchCriteria);
            }
        }

        logger.info("DFS search completed. Found {} users", result.size());
        return result;
    }

    private void dfsUserTraversal(User user, Set<Long> visited, List<User> result, String criteria) {
        visited.add(user.getId());

        if (matchesUserCriteria(user, criteria)) {
            result.add(user);
        }

        List<Function> userFunctions = functionRepository.findByUser(user);
        for (Function function : userFunctions) {
            if (!visited.contains(function.getId())) {
                dfsFunctionTraversal(function, visited, criteria);
            }
        }

        logger.debug("Processed user in DFS: {}", user.getLogin());
    }

    private void dfsFunctionTraversal(Function function, Set<Long> visited, String criteria) {
        visited.add(function.getId());

        logger.debug("Processing function in DFS: {}", function.getName());

        List<Point> functionPoints = pointRepository.findByFunction(function);
        for (Point point : functionPoints) {
            if (!visited.contains(point.getId())) {
                visited.add(point.getId());
                logger.debug("Processing point in DFS: ({}, {})", point.getXValue(), point.getYValue());
            }
        }
    }

    public Optional<User> findUserByIdWithDFS(Long userId) {
        logger.info("Starting single user DFS search for ID: {}", userId);
        Optional<User> user = userRepository.findById(userId);
        user.ifPresent(u -> {
            List<Function> functions = functionRepository.findByUser(u);
            functions.forEach(f -> pointRepository.findByFunction(f));
        });
        logger.info("Single user DFS search completed");
        return user;
    }

    /**
     * Поиск с сортировкой по полям - ИСПРАВЛЕННАЯ ВЕРСИЯ
     */
    public List<User> findUsersWithSortingDFS(String criteria, String sortField, String sortDirection) {
        logger.info("Starting DFS search with criteria: {} and sorting by {} {}",
                criteria, sortField, sortDirection);

        List<User> users = dfsUsers(criteria);

        // Сортировка с обработкой null значений
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

        logger.info("DFS search with sorting completed. Found {} users", users.size());
        return users;
    }

    /**
     * Множественная сортировка по нескольким полям
     */
    public List<User> findUsersWithMultipleSortingDFS(String criteria, Map<String, String> sortCriteria) {
        logger.info("Starting DFS search with criteria: {} and multiple sorting: {}",
                criteria, sortCriteria);

        List<User> users = dfsUsers(criteria);

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

        logger.info("DFS search with multiple sorting completed. Found {} users", users.size());
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
}