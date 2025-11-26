package com.example.lab5.manual.search;

import com.example.lab5.manual.dao.FunctionDAO;
import com.example.lab5.manual.dao.PointDAO;
import com.example.lab5.manual.dto.FunctionDTO;
import com.example.lab5.manual.dto.PointDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class BreadthFirstSearch {
    private static final Logger logger = LoggerFactory.getLogger(BreadthFirstSearch.class);

    private final FunctionDAO functionDAO;
    private final PointDAO pointDAO;

    public BreadthFirstSearch(FunctionDAO functionDAO, PointDAO pointDAO) {
        this.functionDAO = functionDAO;
        this.pointDAO = pointDAO;
    }

    public List<FunctionDTO> searchFunctionsByUserBFS(Long startUserId) {
        logger.info("Starting BFS search for functions from user ID: {}", startUserId);

        List<FunctionDTO> result = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        Queue<Long> queue = new LinkedList<>();

        queue.offer(startUserId);
        visited.add(startUserId);

        while (!queue.isEmpty()) {
            Long currentUserId = queue.poll();
            logger.debug("Processing user ID: {}", currentUserId);

            try {
                // Реальный вызов к FunctionDAO
                List<FunctionDTO> userFunctions = functionDAO.findByUserId(currentUserId);
                result.addAll(userFunctions);

                logger.debug("Found {} functions for user ID {}", userFunctions.size(), currentUserId);

                // В нашей модели данных нет связей между пользователями,
                // но можно добавить логику для связанных данных если нужно

            } catch (Exception e) {
                logger.error("Error during BFS search for user ID {}: {}", currentUserId, e.getMessage(), e);
            }
        }

        logger.info("BFS search completed, found {} functions", result.size());
        return result;
    }

    public List<PointDTO> searchPointsInRangeBFS(Long startFunctionId, double minX, double maxX, double minY, double maxY) {
        logger.info("Starting BFS search for points in range: x[{}, {}], y[{}, {}] for function ID: {}",
                minX, maxX, minY, maxY, startFunctionId);

        List<PointDTO> result = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        Queue<Long> queue = new LinkedList<>();

        queue.offer(startFunctionId);
        visited.add(startFunctionId);

        while (!queue.isEmpty()) {
            Long currentFunctionId = queue.poll();
            logger.debug("Processing function ID: {}", currentFunctionId);

            try {
                // Реальный вызов к PointDAO с фильтрацией по диапазону
                List<PointDTO> allPoints = pointDAO.findByFunctionId(currentFunctionId);
                List<PointDTO> pointsInRange = allPoints.stream()
                        .filter(p -> p.getXValue() >= minX && p.getXValue() <= maxX &&
                                p.getYValue() >= minY && p.getYValue() <= maxY)
                        .collect(Collectors.toList());

                result.addAll(pointsInRange);
                logger.debug("Found {} points in range for function ID {}", pointsInRange.size(), currentFunctionId);

            } catch (Exception e) {
                logger.error("Error during BFS range search for function ID {}: {}", currentFunctionId, e.getMessage(), e);
            }
        }

        logger.info("BFS range search completed, found {} points", result.size());
        return result;
    }
}
