package com.example.lab5.manual.search;

import com.example.lab5.manual.dao.FunctionDAO;
import com.example.lab5.manual.dao.PointDAO;
import com.example.lab5.manual.dto.FunctionDTO;
import com.example.lab5.manual.dto.PointDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DepthFirstSearch {
    private static final Logger logger = LoggerFactory.getLogger(DepthFirstSearch.class);

    private final FunctionDAO functionDAO;
    private final PointDAO pointDAO;

    public DepthFirstSearch(FunctionDAO functionDAO, PointDAO pointDAO) {
        this.functionDAO = functionDAO;
        this.pointDAO = pointDAO;
    }

    public List<FunctionDTO> searchFunctionsByUserHierarchy(Long startUserId, Set<Long> visited) {
        logger.info("Starting DFS search for functions by user hierarchy from user ID: {}", startUserId);

        if (visited == null) {
            visited = new HashSet<>();
        }

        if (visited.contains(startUserId)) {
            logger.debug("User ID {} already visited, skipping", startUserId);
            return new ArrayList<>();
        }

        visited.add(startUserId);
        List<FunctionDTO> result = new ArrayList<>();

        try {
            // Реальный вызов к FunctionDAO
            List<FunctionDTO> userFunctions = functionDAO.findByUserId(startUserId);
            result.addAll(userFunctions);

            logger.debug("Found {} functions for user ID {}", userFunctions.size(), startUserId);

        } catch (Exception e) {
            logger.error("Error during DFS search for user ID {}: {}", startUserId, e.getMessage(), e);
        }

        logger.info("DFS search completed for user ID: {}, found {} functions", startUserId, result.size());
        return result;
    }

    public List<PointDTO> searchPointsByFunctionHierarchy(Long startFunctionId, Set<Long> visited) {
        logger.info("Starting DFS search for points by function hierarchy from function ID: {}", startFunctionId);

        if (visited == null) {
            visited = new HashSet<>();
        }

        if (visited.contains(startFunctionId)) {
            logger.debug("Function ID {} already visited, skipping", startFunctionId);
            return new ArrayList<>();
        }

        visited.add(startFunctionId);
        List<PointDTO> result = new ArrayList<>();

        try {
            // Реальный вызов к PointDAO
            List<PointDTO> functionPoints = pointDAO.findByFunctionId(startFunctionId);
            result.addAll(functionPoints);

            logger.debug("Found {} points for function ID {}", functionPoints.size(), startFunctionId);

        } catch (Exception e) {
            logger.error("Error during DFS search for function ID {}: {}", startFunctionId, e.getMessage(), e);
        }

        logger.info("DFS search completed for function ID: {}, found {} points", startFunctionId, result.size());
        return result;
    }
}