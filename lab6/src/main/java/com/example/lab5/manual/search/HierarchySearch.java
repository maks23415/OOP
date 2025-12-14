package com.example.lab5.manual.search;

import com.example.lab5.manual.dao.FunctionDAO;
import com.example.lab5.manual.dao.PointDAO;
import com.example.lab5.manual.dao.UserDAO;
import com.example.lab5.manual.dto.FunctionDTO;
import com.example.lab5.manual.dto.PointDTO;
import com.example.lab5.manual.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HierarchySearch {
    private static final Logger logger = LoggerFactory.getLogger(HierarchySearch.class);

    private final UserDAO userDAO;
    private final FunctionDAO functionDAO;
    private final PointDAO pointDAO;

    public HierarchySearch(UserDAO userDAO, FunctionDAO functionDAO, PointDAO pointDAO) {
        this.userDAO = userDAO;
        this.functionDAO = functionDAO;
        this.pointDAO = pointDAO;
    }

    public Map<String, Object> searchUserHierarchy(Long userId) {
        logger.info("Starting hierarchy search for user ID: {}", userId);

        Map<String, Object> hierarchy = new HashMap<>();

        try {
            // Реальные вызовы к DAO
            Optional<UserDTO> user = userDAO.findById(userId);
            if (user.isPresent()) {
                hierarchy.put("user", user.get());

                // Получаем функции пользователя
                List<FunctionDTO> functions = functionDAO.findByUserId(userId);
                hierarchy.put("functions", functions);

                // Для каждой функции получаем точки
                List<Map<String, Object>> functionsWithPoints = new ArrayList<>();
                for (FunctionDTO function : functions) {
                    Map<String, Object> functionData = new HashMap<>();
                    functionData.put("function", function);

                    List<PointDTO> points = pointDAO.findByFunctionId(function.getId());
                    functionData.put("points", points);

                    functionsWithPoints.add(functionData);
                    logger.debug("Added {} points for function ID {}", points.size(), function.getId());
                }
                hierarchy.put("functionsWithPoints", functionsWithPoints);
            }

            logger.info("Hierarchy search completed for user ID: {}", userId);

        } catch (Exception e) {
            logger.error("Error during hierarchy search for user ID {}: {}", userId, e.getMessage(), e);
        }

        return hierarchy;
    }

    public List<Map<String, Object>> searchMultipleUsersHierarchy(List<Long> userIds) {
        logger.info("Starting multiple users hierarchy search for {} users", userIds.size());

        List<Map<String, Object>> result = new ArrayList<>();

        for (Long userId : userIds) {
            logger.debug("Processing hierarchy for user ID: {}", userId);
            Map<String, Object> userHierarchy = searchUserHierarchy(userId);
            result.add(userHierarchy);
        }

        logger.info("Multiple users hierarchy search completed, processed {} users", result.size());
        return result;
    }
}