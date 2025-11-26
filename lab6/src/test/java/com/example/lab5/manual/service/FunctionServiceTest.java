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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FunctionServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(FunctionServiceTest.class);
    private UserDAO userDAO;
    private FunctionDAO functionDAO;
    private PointDAO pointDAO;
    private FunctionService functionService;
    private Long testUserId;
    private String uniqueTestLogin;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        functionDAO = new FunctionDAO();
        pointDAO = new PointDAO();
        functionService = new FunctionService(functionDAO, userDAO, pointDAO);

        uniqueTestLogin = "func_service_test_" + UUID.randomUUID().toString().substring(0, 8);
        UserDTO user = new UserDTO(uniqueTestLogin, "USER", "test_pass");
        testUserId = userDAO.createUser(user);
        logger.info("Создан тестовый пользователь с ID: {} и логином: {}", testUserId, uniqueTestLogin);
    }

    @AfterEach
    void tearDown() {
        if (testUserId != null) {
            try {
                functionDAO.deleteFunctionsByUserId(testUserId);

                userDAO.deleteUser(testUserId);
                logger.info("Удален тестовый пользователь с ID: {}", testUserId);
            } catch (Exception e) {
                logger.warn("Не удалось удалить тестового пользователя: {}", e.getMessage());
            }
        }
    }

    @Test
    void testCreateFunction() {
        String functionName = "test_function_" + UUID.randomUUID().toString().substring(0, 8);
        String functionExpression = "f(x) = x^2";

        Long functionId = functionService.createFunction(testUserId, functionName, functionExpression);

        assertNotNull(functionId, "ID функции не должен быть null");
        assertTrue(functionId > 0, "ID функции должен быть положительным числом");

        Optional<FunctionDTO> createdFunction = functionService.getFunctionById(functionId);
        assertTrue(createdFunction.isPresent(), "Функция должна быть найдена в базе данных");
        assertEquals(functionName, createdFunction.get().getName(), "Имя функции должно совпадать");

    }

    @Test
    void testGetFunctionStatistics() {
        String functionName = "stats_test_" + UUID.randomUUID().toString().substring(0, 8);
        Long functionId = functionService.createFunction(testUserId, functionName, "f(x) = x");

        PointService pointService = new PointService(pointDAO, functionDAO);
        pointService.generateFunctionPoints(functionId, "linear", -5, 5, 1);

        FunctionService.FunctionStatistics stats = functionService.getFunctionStatistics(functionId);

        assertNotNull(stats, "Статистика не должна быть null");
        assertEquals(11, stats.getPointCount(), "Количество точек должно быть 11 (от -5 до 5 включительно)");
        assertEquals(-5.0, stats.getMinX(), 0.001, "Минимальное значение X должно быть -5");
        assertEquals(5.0, stats.getMaxX(), 0.001, "Максимальное значение X должно быть 5");
    }

    @Test
    void testCreateFunctionWithInvalidUser() {
        Long invalidUserId = -1L;

        assertThrows(Exception.class, () -> {
            functionService.createFunction(invalidUserId, "test_function", "f(x) = x^2");
        }, "Должно быть выброшено исключение при создании функции с несуществующим пользователем");
    }

    @Test
    void testGetFunctionStatisticsForNonExistentFunction() {
        Long nonExistentFunctionId = -1L;

        FunctionService.FunctionStatistics stats = functionService.getFunctionStatistics(nonExistentFunctionId);

        assertNull(stats, "Статистика должна быть null для несуществующей функции");
    }

    @Test
    void testGetFunctionByIdNotFound() {
        Long nonExistentFunctionId = -1L;

        Optional<FunctionDTO> result = functionService.getFunctionById(nonExistentFunctionId);
        assertFalse(result.isPresent(), "Для несуществующего ID функция не должна быть найдена");
    }
}