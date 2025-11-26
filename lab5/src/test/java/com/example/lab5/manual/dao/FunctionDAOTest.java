package com.example.lab5.manual.dao;

import com.example.lab5.manual.dto.FunctionDTO;
import com.example.lab5.manual.dto.UserDTO;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FunctionDAOTest {
    private static final Logger logger = LoggerFactory.getLogger(FunctionDAOTest.class);
    private UserDAO userDAO;
    private FunctionDAO functionDAO;
    private Long testUserId;
    private static String testPrefix;

    @BeforeAll
    static void setUpAll() {
        testPrefix = "f_" + UUID.randomUUID().toString().substring(0, 6) + "_";
        logger.info("Установлен префикс тестов: {}", testPrefix);
    }

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        functionDAO = new FunctionDAO();

        String uniqueLogin = uniqueLogin("test_user");

        Optional<UserDTO> existingUser = userDAO.findByLogin(uniqueLogin);
        if (existingUser.isPresent()) {
            testUserId = existingUser.get().getId();
            logger.info("Используем существующего пользователя с ID: {}, логин: {}", testUserId, uniqueLogin);
        } else {
            try {
                UserDTO user = new UserDTO(uniqueLogin, "USER", "test_pass");
                testUserId = userDAO.createUser(user);
                logger.info("Создан тестовый пользователь с ID: {}, логин: {}", testUserId, uniqueLogin);
            } catch (Exception e) {
                logger.error("Не удалось создать пользователя: {}", e.getMessage());
                testUserId = null;
            }
        }
    }

    private String uniqueLogin(String baseName) {
        return testPrefix + baseName;
    }

    @Test
    @Order(1)
    void testCreateAndFindFunction() {
        if (testUserId == null) {
            logger.warn("Пропуск теста: нет тестового пользователя");
            return;
        }

        FunctionDTO function = new FunctionDTO(testUserId, "quadratic", "f(x) = x^2");

        Long functionId = functionDAO.createFunction(function);

        assertNotNull(functionId);
        assertTrue(functionId > 0);

        var foundFunction = functionDAO.findById(functionId);
        assertTrue(foundFunction.isPresent());
        assertEquals("quadratic", foundFunction.get().getName());
        assertEquals(testUserId, foundFunction.get().getUserId());
    }

    @Test
    @Order(2)
    void testFindByUserId() {
        if (testUserId == null) {
            logger.warn("Пропуск теста: нет тестового пользователя");
            return;
        }

        functionDAO.createFunction(new FunctionDTO(testUserId, "func1", "f(x) = x"));
        functionDAO.createFunction(new FunctionDTO(testUserId, "func2", "f(x) = x^2"));

        List<FunctionDTO> userFunctions = functionDAO.findByUserId(testUserId);

        assertTrue(userFunctions.size() >= 2);
        userFunctions.forEach(func -> assertEquals(testUserId, func.getUserId()));
    }

    @Test
    @Order(3)
    void testFindByName() {
        if (testUserId == null) {
            logger.warn("Пропуск теста: нет тестового пользователя");
            return;
        }

        functionDAO.createFunction(new FunctionDTO(testUserId, "linear_function", "f(x) = x"));
        functionDAO.createFunction(new FunctionDTO(testUserId, "exponential_func", "f(x) = e^x"));

        List<FunctionDTO> linearFunctions = functionDAO.findByName("linear");

        assertFalse(linearFunctions.isEmpty());
        linearFunctions.forEach(func -> assertTrue(func.getName().toLowerCase().contains("linear")));
    }

    @Test
    @Order(4)
    void testFunctionNotFound() {
        Optional<FunctionDTO> nonExistentFunction = functionDAO.findById(999999L);
        assertFalse(nonExistentFunction.isPresent());
    }

    @Test
    @Order(5)
    void testUpdateFunction() {
        if (testUserId == null) {
            logger.warn("Пропуск теста: нет тестового пользователя");
            return;
        }

        FunctionDTO function = new FunctionDTO(testUserId, "original_name", "f(x) = x");
        Long functionId = functionDAO.createFunction(function);

        FunctionDTO updatedFunction = new FunctionDTO(testUserId, "updated_name", "f(x) = x^2");
        updatedFunction.setId(functionId);
        boolean updated = functionDAO.updateFunction(updatedFunction);

        assertTrue(updated);

        Optional<FunctionDTO> foundFunction = functionDAO.findById(functionId);
        assertTrue(foundFunction.isPresent());
        assertEquals("updated_name", foundFunction.get().getName());
        assertEquals("f(x) = x^2", foundFunction.get().getSignature());
    }

    @Test
    @Order(6)
    void testDeleteFunction() {
        if (testUserId == null) {
            logger.warn("Пропуск теста: нет тестового пользователя");
            return;
        }

        FunctionDTO function = new FunctionDTO(testUserId, "to_delete_func", "f(x) = x");
        Long functionId = functionDAO.createFunction(function);

        boolean deleted = functionDAO.deleteFunction(functionId);
        assertTrue(deleted);

        Optional<FunctionDTO> deletedFunction = functionDAO.findById(functionId);
        assertFalse(deletedFunction.isPresent());
    }

    @Test
    @Order(7)
    void testFindAllFunctions() {
        if (testUserId == null) {
            logger.warn("Пропуск теста: нет тестового пользователя");
            return;
        }

        int initialCount = functionDAO.findAll().size();

        functionDAO.createFunction(new FunctionDTO(testUserId, "find_all_1", "f(x) = x"));
        functionDAO.createFunction(new FunctionDTO(testUserId, "find_all_2", "f(x) = x^2"));

        List<FunctionDTO> allFunctions = functionDAO.findAll();

        assertFalse(allFunctions.isEmpty());
        assertTrue(allFunctions.size() >= initialCount + 2);
        logger.info("Найдено {} функций", allFunctions.size());
    }

    @AfterEach
    void tearDown() {
        try {
            cleanTestData();
        } catch (Exception e) {
            logger.warn("Ошибка при очистке тестовых данных: {}", e.getMessage());
        }
    }

    private void cleanTestData() {
        long startTime = System.currentTimeMillis();
        int deletedFunctions = 0;
        int deletedUsers = 0;

        try {
            List<Long> testUserIds = userDAO.findAll().stream()
                    .filter(user -> user.getLogin().startsWith(testPrefix))
                    .map(UserDTO::getId)
                    .toList();

            for (Long userId : testUserIds) {
                List<FunctionDTO> userFunctions = functionDAO.findByUserId(userId);
                for (FunctionDTO function : userFunctions) {
                    functionDAO.deleteFunction(function.getId());
                    deletedFunctions++;
                }
            }

            for (Long userId : testUserIds) {
                userDAO.deleteUser(userId);
                deletedUsers++;
            }

        } catch (Exception e) {
            logger.error("Ошибка при оптимизированной очистке: {}", e.getMessage());
            fallbackCleanup();
            return;
        }

        long endTime = System.currentTimeMillis();
        if (deletedFunctions > 0 || deletedUsers > 0) {
            logger.info("Очищено {} функций и {} пользователей за {} мс",
                    deletedFunctions, deletedUsers, (endTime - startTime));
        }
    }

    private void fallbackCleanup() {
        try {
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

            if (!testFunctions.isEmpty() || !testUsers.isEmpty()) {
                logger.info("Fallback очистка: {} функций и {} пользователей",
                        testFunctions.size(), testUsers.size());
            }
        } catch (Exception e) {
            logger.error("Ошибка в fallback очистке: {}", e.getMessage());
        }
    }
}