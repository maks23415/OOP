package com.example.lab5.manual.service;

import com.example.lab5.manual.dao.FunctionDAO;
import com.example.lab5.manual.dao.PointDAO;
import com.example.lab5.manual.dao.UserDAO;
import com.example.lab5.manual.dto.FunctionDTO;
import com.example.lab5.manual.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PointServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(PointServiceTest.class);
    private UserDAO userDAO;
    private FunctionDAO functionDAO;
    private PointDAO pointDAO;
    private PointService pointService;
    private Long testUserId;
    private Long testFunctionId;
    private String uniqueTestLogin;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        functionDAO = new FunctionDAO();
        pointDAO = new PointDAO();
        pointService = new PointService(pointDAO, functionDAO);

        uniqueTestLogin = "point_service_test_" + UUID.randomUUID().toString().substring(0, 8);
        UserDTO user = new UserDTO(uniqueTestLogin, "USER", "test_pass");
        testUserId = userDAO.createUser(user);

        String functionName = "test_func_" + UUID.randomUUID().toString().substring(0, 8);
        FunctionDTO function = new FunctionDTO(testUserId, functionName, "f(x) = x^2");
        testFunctionId = functionDAO.createFunction(function);

        logger.info("Созданы тестовые пользователь и функция: user={}, function={}", testUserId, testFunctionId);
    }

    @AfterEach
    void tearDown() {
        if (testUserId != null) {
            try {
                String deletePointsSql = "DELETE FROM points WHERE f_id IN (SELECT id FROM functions WHERE u_id = ?)";
                try (var conn = com.example.lab5.manual.config.DatabaseConfig.getConnection();
                     var pstmt = conn.prepareStatement(deletePointsSql)) {
                    pstmt.setLong(1, testUserId);
                    pstmt.executeUpdate();
                }

                String deleteFunctionsSql = "DELETE FROM functions WHERE u_id = ?";
                try (var conn = com.example.lab5.manual.config.DatabaseConfig.getConnection();
                     var pstmt = conn.prepareStatement(deleteFunctionsSql)) {
                    pstmt.setLong(1, testUserId);
                    pstmt.executeUpdate();
                }

                userDAO.deleteUser(testUserId);
                logger.info("Удален тестовый пользователь с ID: {}", testUserId);
            } catch (Exception e) {
                logger.warn("Не удалось удалить тестового пользователя: {}", e.getMessage());
            }
        }
    }

    @Test
    void testGenerateFunctionPoints() {
        int pointCount = pointService.generateFunctionPoints(testFunctionId, "quadratic", -2, 2, 0.5);

        assertEquals(9, pointCount); // -2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2

        var points = pointService.getPointsByFunctionId(testFunctionId);
        assertEquals(9, points.size());

        boolean foundZeroPoint = points.stream()
                .anyMatch(p -> p.getXValue() == 0.0 && p.getYValue() == 0.0);
        assertTrue(foundZeroPoint);
    }

    @Test
    void testFindExtremums() {
        pointService.generateFunctionPoints(testFunctionId, "quadratic", -2, 2, 1);

        var maxPoint = pointService.findMaxYPoint(testFunctionId);
        var minPoint = pointService.findMinYPoint(testFunctionId);

        assertNotNull(maxPoint);
        assertNotNull(minPoint);
        assertEquals(4.0, maxPoint.getYValue()); // (-2)^2 = 4 или (2)^2 = 4
        assertEquals(0.0, minPoint.getYValue()); // 0^2 = 0
    }
}