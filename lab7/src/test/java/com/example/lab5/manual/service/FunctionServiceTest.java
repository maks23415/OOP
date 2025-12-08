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

import java.util.List;
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
                // Используем существующий метод deleteByUserId вместо deleteFunctionsByUserId
                functionDAO.deleteByUserId(testUserId);

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
        assertEquals(functionExpression, createdFunction.get().getSignature(), "Сигнатура функции должна совпадать");
        assertEquals(testUserId, createdFunction.get().getUserId(), "ID пользователя должно совпадать");
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
        assertEquals(-5.0, stats.getMinY(), 0.001, "Минимальное значение Y должно быть -5");
        assertEquals(5.0, stats.getMaxY(), 0.001, "Максимальное значение Y должно быть 5");
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

    @Test
    void testGetFunctionsByUserId() {
        // Создаем несколько функций для пользователя
        String functionName1 = "func1_" + UUID.randomUUID().toString().substring(0, 8);
        String functionName2 = "func2_" + UUID.randomUUID().toString().substring(0, 8);

        functionService.createFunction(testUserId, functionName1, "f(x) = x^2");
        functionService.createFunction(testUserId, functionName2, "f(x) = x^3");

        List<FunctionDTO> functions = functionService.getFunctionsByUserId(testUserId);

        assertNotNull(functions, "Список функций не должен быть null");
        assertEquals(2, functions.size(), "Должно быть 2 функции для пользователя");
    }

    @Test
    void testUpdateFunction() {
        String functionName = "update_test_" + UUID.randomUUID().toString().substring(0, 8);
        Long functionId = functionService.createFunction(testUserId, functionName, "f(x) = x");

        boolean updated = functionService.updateFunction(functionId, testUserId, "updated_function", "f(x) = x^2");

        assertTrue(updated, "Функция должна быть успешно обновлена");

        Optional<FunctionDTO> updatedFunction = functionService.getFunctionById(functionId);
        assertTrue(updatedFunction.isPresent(), "Обновленная функция должна существовать");
        assertEquals("updated_function", updatedFunction.get().getName(), "Имя функции должно быть обновлено");
        assertEquals("f(x) = x^2", updatedFunction.get().getSignature(), "Сигнатура функции должна быть обновлена");
    }

    @Test
    void testDeleteFunction() {
        String functionName = "delete_test_" + UUID.randomUUID().toString().substring(0, 8);
        Long functionId = functionService.createFunction(testUserId, functionName, "f(x) = x");

        // Создаем точки для функции
        PointService pointService = new PointService(pointDAO, functionDAO);
        pointService.generateFunctionPoints(functionId, "linear", -2, 2, 1);

        boolean deleted = functionService.deleteFunction(functionId);

        assertTrue(deleted, "Функция должна быть успешно удалена");

        Optional<FunctionDTO> deletedFunction = functionService.getFunctionById(functionId);
        assertFalse(deletedFunction.isPresent(), "Удаленная функция не должна существовать");
    }

    @Test
    void testValidateFunctionName() {
        String functionName = "unique_func_" + UUID.randomUUID().toString().substring(0, 8);

        // Создаем функцию с уникальным именем
        functionService.createFunction(testUserId, functionName, "f(x) = x");

        // Проверяем валидацию имени
        boolean isValid = functionService.validateFunctionName(testUserId, functionName);
        assertFalse(isValid, "Имя функции не должно быть валидным (уже существует)");

        boolean isValidNew = functionService.validateFunctionName(testUserId, "completely_new_name");
        assertTrue(isValidNew, "Новое имя функции должно быть валидным");
    }
}