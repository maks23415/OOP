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
        testPrefix = "point_test_" + UUID.randomUUID().toString().substring(0, 8) + "_";
        logger.info("Установлен префикс тестов: {}", testPrefix);
    }

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        functionDAO = new FunctionDAO();
        pointDAO = new PointDAO();

        String userLogin = uniqueLogin("point_test_user");
        String functionName = uniqueLogin("test_function");

        try {
            UserDTO user = new UserDTO(userLogin, "USER", "test_pass");
            testUserId = userDAO.createUser(user);
            logger.info("Создан тестовый пользователь с ID: {}, логин: {}", testUserId, userLogin);

            FunctionDTO function = new FunctionDTO(testUserId, functionName, "f(x) = x");
            testFunctionId = functionDAO.createFunction(function);
            logger.info("Создана тестовая функция с ID: {}, название: {}", testFunctionId, functionName);

        } catch (Exception e) {
            logger.warn("Не удалось создать тестовые данные, используем существующие");
            Optional<UserDTO> existingUser = userDAO.findByLogin(userLogin);
            if (existingUser.isPresent()) {
                testUserId = existingUser.get().getId();
                logger.info("Используем существующего пользователя с ID: {}", testUserId);

                List<FunctionDTO> userFunctions = functionDAO.findByUserId(testUserId);
                if (!userFunctions.isEmpty()) {
                    testFunctionId = userFunctions.get(0).getId();
                    logger.info("Используем существующую функцию с ID: {}", testFunctionId);
                } else {
                    FunctionDTO function = new FunctionDTO(testUserId, functionName, "f(x) = x");
                    testFunctionId = functionDAO.createFunction(function);
                    logger.info("Создана новая функция с ID: {}", testFunctionId);
                }
            } else {
                String fallbackLogin = uniqueLogin("fallback_point_user");
                UserDTO fallbackUser = new UserDTO(fallbackLogin, "USER", "test_pass");
                testUserId = userDAO.createUser(fallbackUser);

                FunctionDTO function = new FunctionDTO(testUserId, functionName, "f(x) = x");
                testFunctionId = functionDAO.createFunction(function);
                logger.info("Созданы резервные тестовые данные: user={}, function={}", testUserId, testFunctionId);
            }
        }
    }

    private String uniqueLogin(String baseName) {
        return testPrefix + baseName;
    }

    @Test
    @Order(1)
    void testCreatePoint() {
        if (testFunctionId == null) {
            logger.warn("Пропуск теста: нет тестовой функции");
            return;
        }

        PointDTO point = new PointDTO(testFunctionId, 1.5, 2.25);

        Long pointId = pointDAO.createPoint(point);

        assertNotNull(pointId);
        assertTrue(pointId > 0);

        Optional<PointDTO> foundPoint = pointDAO.findById(pointId);
        assertTrue(foundPoint.isPresent());
        assertEquals(testFunctionId, foundPoint.get().getFunctionId());
        assertEquals(1.5, foundPoint.get().getXValue());
        assertEquals(2.25, foundPoint.get().getYValue());
        assertNotNull(foundPoint.get().getCreatedAt());
    }

    @Test
    @Order(2)
    void testCreatePointsBatch() {
        if (testFunctionId == null) {
            logger.warn("Пропуск теста: нет тестовой функции");
            return;
        }

        List<PointDTO> points = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            points.add(new PointDTO(testFunctionId, (double) i, (double) i * i));
        }

        int createdCount = pointDAO.createPoints(points);

        assertEquals(5, createdCount);

        List<PointDTO> allPoints = pointDAO.findByFunctionId(testFunctionId);
        assertTrue(allPoints.size() >= 5);
        logger.info("Создано {} точек в пакетном режиме", createdCount);
    }

    @Test
    @Order(3)
    void testFindByFunctionId() {
        if (testFunctionId == null) {
            logger.warn("Пропуск теста: нет тестовой функции");
            return;
        }

        pointDAO.createPoint(new PointDTO(testFunctionId, 1.0, 1.0));
        pointDAO.createPoint(new PointDTO(testFunctionId, 2.0, 4.0));
        pointDAO.createPoint(new PointDTO(testFunctionId, 3.0, 9.0));

        String anotherFunctionName = uniqueLogin("another_func");
        FunctionDTO anotherFunction = new FunctionDTO(testUserId, anotherFunctionName, "f(x) = sin(x)");
        Long anotherFunctionId = functionDAO.createFunction(anotherFunction);
        pointDAO.createPoint(new PointDTO(anotherFunctionId, 0.0, 0.0));

        List<PointDTO> points = pointDAO.findByFunctionId(testFunctionId);

        assertTrue(points.size() >= 3);
        points.forEach(point -> assertEquals(testFunctionId, point.getFunctionId()));

        for (int i = 0; i < points.size() - 1; i++) {
            assertTrue(points.get(i).getXValue() <= points.get(i + 1).getXValue());
        }
    }

    @Test
    @Order(4)
    void testFindByXRange() {
        if (testFunctionId == null) {
            logger.warn("Пропуск теста: нет тестовой функции");
            return;
        }

        for (double x = -5.0; x <= 5.0; x += 1.0) {
            pointDAO.createPoint(new PointDTO(testFunctionId, x, x * x));
        }

        List<PointDTO> pointsInRange = pointDAO.findByXRange(testFunctionId, -2.0, 2.0);

        assertEquals(5, pointsInRange.size()); // -2, -1, 0, 1, 2
        pointsInRange.forEach(point -> {
            assertTrue(point.getXValue() >= -2.0);
            assertTrue(point.getXValue() <= 2.0);
        });
        logger.info("Найдено {} точек в диапазоне x=[-2, 2]", pointsInRange.size());
    }

    @Test
    @Order(5)
    void testFindByYRange() {
        if (testFunctionId == null) {
            logger.warn("Пропуск теста: нет тестовой функции");
            return;
        }

        pointDAO.createPoint(new PointDTO(testFunctionId, 1.0, 10.0));
        pointDAO.createPoint(new PointDTO(testFunctionId, 2.0, 20.0));
        pointDAO.createPoint(new PointDTO(testFunctionId, 3.0, 30.0));
        pointDAO.createPoint(new PointDTO(testFunctionId, 4.0, 40.0));

        List<PointDTO> pointsInRange = pointDAO.findByYRange(testFunctionId, 15.0, 35.0);

        assertEquals(2, pointsInRange.size()); // 20 и 30
        pointsInRange.forEach(point -> {
            assertTrue(point.getYValue() >= 15.0);
            assertTrue(point.getYValue() <= 35.0);
        });
    }

    @Test
    @Order(6)
    void testFindAllPoints() {
        if (testFunctionId == null) {
            logger.warn("Пропуск теста: нет тестовой функции");
            return;
        }

        pointDAO.createPoint(new PointDTO(testFunctionId, 1.0, 1.0));
        pointDAO.createPoint(new PointDTO(testFunctionId, 2.0, 4.0));

        String anotherFunctionName = uniqueLogin("func2");
        FunctionDTO anotherFunction = new FunctionDTO(testUserId, anotherFunctionName, "f(x) = x");
        Long func2Id = functionDAO.createFunction(anotherFunction);
        pointDAO.createPoint(new PointDTO(func2Id, 3.0, 3.0));

        List<PointDTO> allPoints = pointDAO.findAll();

        assertFalse(allPoints.isEmpty());
        assertTrue(allPoints.size() >= 3);
        logger.info("Найдено {} точек всего", allPoints.size());
    }

    @Test
    @Order(7)
    void testUpdatePoint() {
        if (testFunctionId == null) {
            logger.warn("Пропуск теста: нет тестовой функции");
            return;
        }

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
        if (testFunctionId == null) {
            logger.warn("Пропуск теста: нет тестовой функции");
            return;
        }

        Long pointId = pointDAO.createPoint(new PointDTO(testFunctionId, 1.0, 1.0));

        boolean deleted = pointDAO.deletePoint(pointId);

        assertTrue(deleted);

        Optional<PointDTO> deletedPoint = pointDAO.findById(pointId);
        assertFalse(deletedPoint.isPresent());
    }

    @Test
    @Order(9)
    void testDeleteByFunctionId() {
        if (testFunctionId == null) {
            logger.warn("Пропуск теста: нет тестовой функции");
            return;
        }

        pointDAO.createPoint(new PointDTO(testFunctionId, 1.0, 1.0));
        pointDAO.createPoint(new PointDTO(testFunctionId, 2.0, 4.0));
        pointDAO.createPoint(new PointDTO(testFunctionId, 3.0, 9.0));

        String survivingFuncName = uniqueLogin("surviving_func");
        FunctionDTO anotherFunction = new FunctionDTO(testUserId, survivingFuncName, "f(x) = x");
        Long survivingFuncId = functionDAO.createFunction(anotherFunction);
        pointDAO.createPoint(new PointDTO(survivingFuncId, 4.0, 4.0));

        int deletedCount = pointDAO.deleteByFunctionId(testFunctionId);

        assertEquals(3, deletedCount);

        List<PointDTO> remainingPoints = pointDAO.findByFunctionId(testFunctionId);
        assertTrue(remainingPoints.isEmpty());

        List<PointDTO> survivingPoints = pointDAO.findByFunctionId(survivingFuncId);
        assertEquals(1, survivingPoints.size());
        logger.info("Удалено {} точек, сохранилось {} точек другой функции",
                deletedCount, survivingPoints.size());
    }

    @Test
    @Order(10)
    void testPointUniquenessConstraint() {
        if (testFunctionId == null) {
            logger.warn("Пропуск теста: нет тестовой функции");
            return;
        }


        pointDAO.createPoint(new PointDTO(testFunctionId, 1.0, 1.0));

        PointDTO duplicatePoint = new PointDTO(testFunctionId, 1.0, 2.0);

        assertThrows(RuntimeException.class, () -> {
            pointDAO.createPoint(duplicatePoint);
        });
        logger.info("Проверка ограничения уникальности выполнена");
    }

    @Test
    @Order(11)
    void testFindPointById_NotFound() {
        Optional<PointDTO> point = pointDAO.findById(999999L);

        assertFalse(point.isPresent());
    }

    @Test
    @Order(12)
    void testEmptyRanges() {
        if (testFunctionId == null) {
            logger.warn("Пропуск теста: нет тестовой функции");
            return;
        }

        List<PointDTO> emptyXRange = pointDAO.findByXRange(testFunctionId, 100.0, 200.0);
        List<PointDTO> emptyYRange = pointDAO.findByYRange(testFunctionId, 100.0, 200.0);

        assertTrue(emptyXRange.isEmpty());
        assertTrue(emptyYRange.isEmpty());
    }

    @Test
    @Order(13)
    void testPointPrecision() {
        if (testFunctionId == null) {
            logger.warn("Пропуск теста: нет тестовой функции");
            return;
        }

        double preciseX = 1.23456789;
        double preciseY = 9.87654321;

        Long pointId = pointDAO.createPoint(new PointDTO(testFunctionId, preciseX, preciseY));
        Optional<PointDTO> foundPoint = pointDAO.findById(pointId);

        assertTrue(foundPoint.isPresent());
        assertEquals(preciseX, foundPoint.get().getXValue(), 0.0000001);
        assertEquals(preciseY, foundPoint.get().getYValue(), 0.0000001);
        logger.info("Точность чисел с плавающей точкой сохранена: x={}, y={}",
                foundPoint.get().getXValue(), foundPoint.get().getYValue());
    }

    @AfterEach
    void tearDown() {
        try {
            List<PointDTO> testPoints = pointDAO.findAll().stream()
                    .filter(point -> {
                        // Находим функцию точки
                        Optional<FunctionDTO> function = functionDAO.findById(point.getFunctionId());
                        if (function.isPresent()) {
                            // Находим пользователя функции
                            Optional<UserDTO> user = userDAO.findById(function.get().getUserId());
                            return user.isPresent() && user.get().getLogin().startsWith(testPrefix);
                        }
                        return false;
                    })
                    .toList();

            for (PointDTO point : testPoints) {
                pointDAO.deletePoint(point.getId());
            }

            List<FunctionDTO> testFunctions = functionDAO.findAll().stream()
                    .filter(func -> {
                        Optional<UserDTO> user = userDAO.findById(func.getUserId());
                        return user.isPresent() && user.get().getLogin().startsWith(testPrefix);
                    })
                    .toList();

            for (FunctionDTO function : testFunctions) {
                functionDAO.deleteFunction(function.getId());
            }

            List<UserDTO> testUsers = userDAO.findAll().stream()
                    .filter(user -> user.getLogin().startsWith(testPrefix))
                    .toList();

            for (UserDTO user : testUsers) {
                userDAO.deleteUser(user.getId());
            }

            if (!testPoints.isEmpty() || !testFunctions.isEmpty() || !testUsers.isEmpty()) {
                logger.info("Очищено {} точек, {} функций и {} пользователей",
                        testPoints.size(), testFunctions.size(), testUsers.size());
            }
        } catch (Exception e) {
            logger.warn("Ошибка при очистке тестовых данных: {}", e.getMessage());
        }
    }
}