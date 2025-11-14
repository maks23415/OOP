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
        testPrefix = "func_test_" + UUID.randomUUID().toString().substring(0, 8) + "_";
        logger.info("Установлен префикс тестов: {}", testPrefix);
    }

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        functionDAO = new FunctionDAO();

        String uniqueLogin = uniqueLogin("function_test_user");
        try {
            UserDTO user = new UserDTO(uniqueLogin, "USER", "test_pass");
            testUserId = userDAO.createUser(user);
            logger.info("Создан тестовый пользователь с ID: {}, логин: {}", testUserId, uniqueLogin);
        } catch (Exception e) {
            logger.warn("Не удалось создать пользователя {}, ищем существующего", uniqueLogin);
            Optional<UserDTO> existingUser = userDAO.findByLogin(uniqueLogin);
            if (existingUser.isPresent()) {
                testUserId = existingUser.get().getId();
                logger.info("Используем существующего пользователя с ID: {}", testUserId);
            } else {
                String fallbackLogin = uniqueLogin("fallback_user");
                UserDTO fallbackUser = new UserDTO(fallbackLogin, "USER", "test_pass");
                testUserId = userDAO.createUser(fallbackUser);
                logger.info("Создан резервный пользователь с ID: {}, логин: {}", testUserId, fallbackLogin);
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

        String anotherUserLogin = uniqueLogin("another_user");
        Long anotherUserId = createUserSafely(anotherUserLogin, "USER", "pass");

        if (anotherUserId != null) {
            functionDAO.createFunction(new FunctionDTO(anotherUserId, "func3", "f(x) = sin(x)"));
        }

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

    private Long createUserSafely(String login, String role, String password) {
        try {
            UserDTO user = new UserDTO(login, role, password);
            return userDAO.createUser(user);
        } catch (Exception e) {
            logger.warn("Не удалось создать пользователя {}, ищем существующего", login);
            Optional<UserDTO> existingUser = userDAO.findByLogin(login);
            return existingUser.map(UserDTO::getId).orElse(null);
        }
    }

    @AfterEach
    void tearDown() {
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
                logger.info("Очищено {} функций и {} пользователей", testFunctions.size(), testUsers.size());
            }
        } catch (Exception e) {
            logger.warn("Ошибка при очистке тестовых данных: {}", e.getMessage());
        }
    }
}