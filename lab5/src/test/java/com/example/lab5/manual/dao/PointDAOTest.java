package com.example.lab5.manual.dao;

import com.example.lab5.manual.dto.FunctionDTO;
import com.example.lab5.manual.dto.PointDTO;
import com.example.lab5.manual.dto.UserDTO;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PointDAOTest {
    private static final Logger logger = LoggerFactory.getLogger(PointDAOTest.class);
    private UserDAO userDAO;
    private FunctionDAO functionDAO;
    private PointDAO pointDAO;
    private Long testUserId;
    private Long testFunctionId;
    private static String testPrefix;

    @BeforeAll
    static void setUpAll() {
        // Более короткий префикс для ускорения поиска
        testPrefix = "p_" + UUID.randomUUID().toString().substring(0, 6) + "_";
        logger.info("Установлен префикс тестов: {}", testPrefix);
    }

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        functionDAO = new FunctionDAO();
        pointDAO = new PointDAO();

        String userLogin = uniqueLogin("user");
        String functionName = uniqueLogin("func");

        // Упрощенная логика создания тестовых данных
        Optional<UserDTO> existingUser = userDAO.findByLogin(userLogin);
        if (existingUser.isPresent()) {
            testUserId = existingUser.get().getId();
            logger.info("Используем существующего пользователя с ID: {}", testUserId);
        } else {
            UserDTO user = new UserDTO(userLogin, "USER", "test_pass");
            testUserId = userDAO.createUser(user);
            logger.info("Создан тестовый пользователь с ID: {}", testUserId);
        }

        // Создаем функцию для этого пользователя
        FunctionDTO function = new FunctionDTO(testUserId, functionName, "f(x) = x");
        testFunctionId = functionDAO.createFunction(function);
        logger.info("Создана тестовая функция с ID: {}", testFunctionId);
    }

    private String uniqueLogin(String baseName) {
        return testPrefix + baseName;
    }

    @Test
    @Order(1)
    void testCreatePoint() {
        PointDTO point = new PointDTO(testFunctionId, 1.5, 2.25);

        Long pointId = pointDAO.createPoint(point);

        assertNotNull(pointId);
        assertTrue(pointId > 0);

        Optional<PointDTO> foundPoint = pointDAO.findById(pointId);
        assertTrue(foundPoint.isPresent());
        assertEquals(testFunctionId, foundPoint.get().getFunctionId());
        assertEquals(1.5, foundPoint.get().getXValue());
        assertEquals(2.25, foundPoint.get().getYValue());
    }

    @Test
    @Order(2)
    void testCreatePointsBatch() {
        List<PointDTO> points = new ArrayList<>();
        for (int i = 0; i < 3; i++) { // Уменьшил количество для скорости
            points.add(new PointDTO(testFunctionId, (double) i, (double) i * i));
        }

        int createdCount = pointDAO.createPoints(points);
        assertEquals(3, createdCount);

        List<PointDTO> allPoints = pointDAO.findByFunctionId(testFunctionId);
        assertTrue(allPoints.size() >= 3);
    }

    @Test
    @Order(3)
    void testFindByFunctionId() {
        pointDAO.createPoint(new PointDTO(testFunctionId, 1.0, 1.0));
        pointDAO.createPoint(new PointDTO(testFunctionId, 2.0, 4.0));
        pointDAO.createPoint(new PointDTO(testFunctionId, 3.0, 9.0));

        List<PointDTO> points = pointDAO.findByFunctionId(testFunctionId);
        assertTrue(points.size() >= 3);
        points.forEach(point -> assertEquals(testFunctionId, point.getFunctionId()));
    }

    @Test
    @Order(4)
    void testFindByXRange() {
        // Создаем только нужные точки для теста
        pointDAO.createPoint(new PointDTO(testFunctionId, -2.0, 4.0));
        pointDAO.createPoint(new PointDTO(testFunctionId, 0.0, 0.0));
        pointDAO.createPoint(new PointDTO(testFunctionId, 2.0, 4.0));

        List<PointDTO> pointsInRange = pointDAO.findByXRange(testFunctionId, -1.0, 1.0);
        assertEquals(1, pointsInRange.size()); // Только x=0.0
    }

    @Test
    @Order(5)
    void testFindByYRange() {
        pointDAO.createPoint(new PointDTO(testFunctionId, 1.0, 10.0));
        pointDAO.createPoint(new PointDTO(testFunctionId, 2.0, 20.0));
        pointDAO.createPoint(new PointDTO(testFunctionId, 3.0, 30.0));

        List<PointDTO> pointsInRange = pointDAO.findByYRange(testFunctionId, 15.0, 25.0);
        assertEquals(1, pointsInRange.size()); // Только y=20.0
    }

    @Test
    @Order(6)
    void testFindAllPoints() {
        pointDAO.createPoint(new PointDTO(testFunctionId, 1.0, 1.0));
        pointDAO.createPoint(new PointDTO(testFunctionId, 2.0, 4.0));

        List<PointDTO> allPoints = pointDAO.findAll();
        assertFalse(allPoints.isEmpty());
        assertTrue(allPoints.size() >= 2);
    }

    @Test
    @Order(7)
    void testUpdatePoint() {
        Long pointId = pointDAO.createPoint(new PointDTO(testFunctionId, 1.0, 1.0));

        PointDTO pointToUpdate = new PointDTO(testFunctionId, 2.0, 4.0);
        pointToUpdate.setId(pointId);
        boolean updated = pointDAO.updatePoint(pointToUpdate);

        assertTrue(updated);

        Optional<PointDTO> updatedPoint = pointDAO.findById(pointId);
        assertTrue(updatedPoint.isPresent());
        assertEquals(2.0, updatedPoint.get().getXValue());
        assertEquals(4.0, updatedPoint.get().getYValue());
    }

    @Test
    @Order(8)
    void testDeletePoint() {
        Long pointId = pointDAO.createPoint(new PointDTO(testFunctionId, 1.0, 1.0));

        boolean deleted = pointDAO.deletePoint(pointId);
        assertTrue(deleted);

        Optional<PointDTO> deletedPoint = pointDAO.findById(pointId);
        assertFalse(deletedPoint.isPresent());
    }

    @Test
    @Order(9)
    void testDeleteByFunctionId() {
        pointDAO.createPoint(new PointDTO(testFunctionId, 1.0, 1.0));
        pointDAO.createPoint(new PointDTO(testFunctionId, 2.0, 4.0));

        int deletedCount = pointDAO.deleteByFunctionId(testFunctionId);
        assertEquals(2, deletedCount);

        List<PointDTO> remainingPoints = pointDAO.findByFunctionId(testFunctionId);
        assertTrue(remainingPoints.isEmpty());
    }

    @Test
    @Order(10)
    void testPointUniquenessConstraint() {
        pointDAO.createPoint(new PointDTO(testFunctionId, 1.0, 1.0));

        PointDTO duplicatePoint = new PointDTO(testFunctionId, 1.0, 2.0);

        assertThrows(RuntimeException.class, () -> {
            pointDAO.createPoint(duplicatePoint);
        });
    }


    @Test
    @Order(12)
    void testEmptyRanges() {
        List<PointDTO> emptyXRange = pointDAO.findByXRange(testFunctionId, 100.0, 200.0);
        List<PointDTO> emptyYRange = pointDAO.findByYRange(testFunctionId, 100.0, 200.0);

        assertTrue(emptyXRange.isEmpty());
        assertTrue(emptyYRange.isEmpty());
    }

    @Test
    @Order(13)
    void testPointPrecision() {
        double preciseX = 1.23456789;
        double preciseY = 9.87654321;

        Long pointId = pointDAO.createPoint(new PointDTO(testFunctionId, preciseX, preciseY));
        Optional<PointDTO> foundPoint = pointDAO.findById(pointId);

        assertTrue(foundPoint.isPresent());
        assertEquals(preciseX, foundPoint.get().getXValue(), 0.0000001);
        assertEquals(preciseY, foundPoint.get().getYValue(), 0.0000001);
    }

    @AfterEach
    void tearDown() {
        cleanTestData();
    }

    private void cleanTestData() {
        long startTime = System.currentTimeMillis();
        int totalDeleted = 0;

        try {
            List<UserDTO> testUsers = userDAO.findAll().stream()
                    .filter(user -> user.getLogin().startsWith(testPrefix))
                    .toList();

            for (UserDTO user : testUsers) {
                List<FunctionDTO> userFunctions = functionDAO.findByUserId(user.getId());

                for (FunctionDTO function : userFunctions) {
                    int deletedPoints = pointDAO.deleteByFunctionId(function.getId());
                    totalDeleted += deletedPoints;
                    functionDAO.deleteFunction(function.getId());
                }

                userDAO.deleteUser(user.getId());
            }

            long duration = System.currentTimeMillis() - startTime;
            if (totalDeleted > 0) {
                logger.info("Очищено {} тестовых данных за {} мс", totalDeleted, duration);
            }

        } catch (Exception e) {
            logger.warn("Ошибка при очистке: {}", e.getMessage());
        }
    }
}