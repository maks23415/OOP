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
public class HierarchySearch {

    private static final Logger logger = LoggerFactory.getLogger(HierarchySearch.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private PointRepository pointRepository;

    public Map<String, Object> searchFullHierarchy(String userLogin) {
        logger.info("Starting hierarchy search for user: {}", userLogin);

        Map<String, Object> hierarchyResult = new HashMap<>();

        Optional<User> userOpt = userRepository.findByLogin(userLogin);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            hierarchyResult.put("user", user);

            List<Function> functions = functionRepository.findByUser(user);
            hierarchyResult.put("functions", functions);

            List<Map<String, Object>> functionsWithPoints = new ArrayList<>();
            for (Function function : functions) {
                Map<String, Object> functionData = new HashMap<>();
                functionData.put("function", function);

                List<Point> points = pointRepository.findByFunction(function);
                functionData.put("points", points);

                functionsWithPoints.add(functionData);
            }
            hierarchyResult.put("functionsWithPoints", functionsWithPoints);
        }

        logger.info("Hierarchy search completed for user: {}", userLogin);
        return hierarchyResult;
    }

    public List<Point> findPointsByUserHierarchy(String userLogin) {
        logger.info("Starting points hierarchy search for user: {}", userLogin);

        List<Point> allPoints = new ArrayList<>();
        Optional<User> userOpt = userRepository.findByLogin(userLogin);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<Function> functions = functionRepository.findByUser(user);

            for (Function function : functions) {
                List<Point> points = pointRepository.findByFunction(function);
                allPoints.addAll(points);
                logger.debug("Found {} points for function: {}", points.size(), function.getName());
            }
        }

        logger.info("Points hierarchy search completed. Found {} points", allPoints.size());
        return allPoints;
    }

    public List<Map<String, Object>> searchMultipleUsersHierarchy(List<String> userLogins) {
        logger.info("Starting multiple users hierarchy search for: {}", userLogins);

        List<Map<String, Object>> results = new ArrayList<>();

        for (String login : userLogins) {
            Map<String, Object> userHierarchy = searchFullHierarchy(login);
            if (!userHierarchy.isEmpty()) {
                results.add(userHierarchy);
            }
        }

        logger.info("Multiple users hierarchy search completed. Found {} results", results.size());
        return results;
    }

    /**
     * Поиск точек с сортировкой - ИСПРАВЛЕННАЯ ВЕРСИЯ
     */
    public List<Point> findPointsWithHierarchySorting(String userLogin, String sortBy, String sortOrder) {
        logger.info("Starting hierarchical points search with sorting for user: {}, sort by: {} {}",
                userLogin, sortBy, sortOrder);

        List<Point> points = findPointsByUserHierarchy(userLogin);

        // Сортировка с обработкой null значений
        points.sort((p1, p2) -> {
            int result;
            switch (sortBy.toLowerCase()) {
                case "x":
                    result = Objects.compare(p1.getXValue(), p2.getXValue(),
                            Comparator.nullsFirst(Double::compareTo));
                    break;
                case "y":
                    result = Objects.compare(p1.getYValue(), p2.getYValue(),
                            Comparator.nullsFirst(Double::compareTo));
                    break;
                case "id":
                    result = Objects.compare(p1.getId(), p2.getId(),
                            Comparator.nullsFirst(Long::compareTo));
                    break;
                default:
                    result = Objects.compare(p1.getId(), p2.getId(),
                            Comparator.nullsFirst(Long::compareTo));
            }
            return "DESC".equalsIgnoreCase(sortOrder) ? -result : result;
        });

        logger.info("Hierarchical points search with sorting completed. Found {} points", points.size());
        return points;
    }

    /**
     * Множественная сортировка точек по нескольким полям
     */
    public List<Point> findPointsWithMultipleSorting(String userLogin, Map<String, String> sortCriteria) {
        logger.info("Starting hierarchical points search with multiple sorting for user: {}, criteria: {}",
                userLogin, sortCriteria);

        List<Point> points = findPointsByUserHierarchy(userLogin);

        points.sort((p1, p2) -> {
            for (Map.Entry<String, String> entry : sortCriteria.entrySet()) {
                String field = entry.getKey();
                String direction = entry.getValue();

                int result = comparePointsByField(p1, p2, field);
                if (result != 0) {
                    return "DESC".equalsIgnoreCase(direction) ? -result : result;
                }
            }
            return 0;
        });

        logger.info("Hierarchical points search with multiple sorting completed. Found {} points", points.size());
        return points;
    }

    private int comparePointsByField(Point p1, Point p2, String field) {
        switch (field.toLowerCase()) {
            case "x":
                return Objects.compare(p1.getXValue(), p2.getXValue(),
                        Comparator.nullsFirst(Double::compareTo));
            case "y":
                return Objects.compare(p1.getYValue(), p2.getYValue(),
                        Comparator.nullsFirst(Double::compareTo));
            case "id":
                return Objects.compare(p1.getId(), p2.getId(),
                        Comparator.nullsFirst(Long::compareTo));
            default:
                return Objects.compare(p1.getId(), p2.getId(),
                        Comparator.nullsFirst(Long::compareTo));
        }
    }

    public Optional<Map<String, Object>> findFunctionWithHierarchy(Long functionId) {
        logger.info("Starting single function hierarchy search for ID: {}", functionId);

        Optional<Function> functionOpt = functionRepository.findById(functionId);
        if (functionOpt.isPresent()) {
            Function function = functionOpt.get();
            Map<String, Object> result = new HashMap<>();
            result.put("function", function);
            result.put("user", function.getUser());
            result.put("points", pointRepository.findByFunction(function));

            logger.info("Single function hierarchy search completed");
            return Optional.of(result);
        }

        logger.warn("Function not found with ID: {}", functionId);
        return Optional.empty();
    }

    /**
     * Сортировка пользователей по иерархии
     */
    public List<User> findUsersWithHierarchySorting(String sortField, String sortDirection) {
        logger.info("Starting users hierarchy search with sorting by: {} {}",
                sortField, sortDirection);

        List<User> users = userRepository.findAll();

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

        logger.info("Users hierarchy search with sorting completed. Found {} users", users.size());
        return users;
    }
}