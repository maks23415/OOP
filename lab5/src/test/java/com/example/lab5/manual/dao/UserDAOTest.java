package com.example.lab5.manual.dao;

import com.example.lab5.manual.dto.UserDTO;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDAOTest {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOTest.class);
    private UserDAO userDAO;
    private static String testPrefix;

    @BeforeAll
    static void setUpAll() {
        testPrefix = "test_" + UUID.randomUUID().toString().substring(0, 8) + "_";
        logger.info("Установлен префикс тестов: {}", testPrefix);
    }

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        logger.info("Настройка тестовой среды для UserDAO");
    }

    private String uniqueLogin(String baseName) {
        return testPrefix + baseName;
    }

    @Test
    @Order(1)
    void testCreateUser() {
        String uniqueLogin = uniqueLogin("test_user_1");
        UserDTO user = new UserDTO(uniqueLogin, "USER", "test_password");

        Long userId = userDAO.createUser(user);

        assertNotNull(userId);
        assertTrue(userId > 0);
        logger.info("Создан пользователь с ID: {}, логин: {}", userId, uniqueLogin);

        Optional<UserDTO> foundUser = userDAO.findById(userId);
        assertTrue(foundUser.isPresent());
        assertEquals(uniqueLogin, foundUser.get().getLogin());
        assertEquals("USER", foundUser.get().getRole());
    }

    @Test
    @Order(2)
    void testCreateUserWithDuplicateLogin() {
        String duplicateLogin = uniqueLogin("duplicate_login");
        UserDTO user1 = new UserDTO(duplicateLogin, "USER", "pass1");
        userDAO.createUser(user1);

        UserDTO user2 = new UserDTO(duplicateLogin, "ADMIN", "pass2");

        assertThrows(RuntimeException.class, () -> {
            userDAO.createUser(user2);
        });
        logger.info("Проверка уникальности логина выполнена успешно для: {}", duplicateLogin);
    }

    @Test
    @Order(3)
    void testFindByLogin() {
        String uniqueLogin = uniqueLogin("unique_login_find");
        UserDTO user = new UserDTO(uniqueLogin, "ADMIN", "admin_pass");
        Long userId = userDAO.createUser(user);

        Optional<UserDTO> foundUser = userDAO.findByLogin(uniqueLogin);

        assertTrue(foundUser.isPresent());
        assertEquals(userId, foundUser.get().getId());
        assertEquals("ADMIN", foundUser.get().getRole());
    }

    @Test
    @Order(4)
    void testFindByLogin_NotFound() {
        String nonExistentLogin = uniqueLogin("non_existent_login");
        Optional<UserDTO> foundUser = userDAO.findByLogin(nonExistentLogin);

        assertFalse(foundUser.isPresent());
        logger.info("Поиск несуществующего пользователя выполнен корректно: {}", nonExistentLogin);
    }

    @Test
    @Order(5)
    void testFindById() {
        String uniqueLogin = uniqueLogin("find_by_id_user");
        UserDTO user = new UserDTO(uniqueLogin, "MODERATOR", "password");
        Long userId = userDAO.createUser(user);

        Optional<UserDTO> foundUser = userDAO.findById(userId);

        assertTrue(foundUser.isPresent());
        assertEquals(userId, foundUser.get().getId());
        assertEquals(uniqueLogin, foundUser.get().getLogin());
        assertEquals("MODERATOR", foundUser.get().getRole());
    }

    @Test
    @Order(6)
    void testFindById_NotFound() {
        Optional<UserDTO> foundUser = userDAO.findById(999999L);
        assertFalse(foundUser.isPresent());
    }

    @Test
    @Order(7)
    void testFindAllUsers() {
        int initialCount = userDAO.findAll().size();

        userDAO.createUser(new UserDTO(uniqueLogin("find_all_1"), "USER", "pass1"));
        userDAO.createUser(new UserDTO(uniqueLogin("find_all_2"), "ADMIN", "pass2"));
        userDAO.createUser(new UserDTO(uniqueLogin("find_all_3"), "MODERATOR", "pass3"));

        List<UserDTO> users = userDAO.findAll();

        assertFalse(users.isEmpty());
        assertTrue(users.size() >= initialCount + 3);
        logger.info("Найдено {} пользователей", users.size());

        users.forEach(u -> {
            assertNotNull(u.getId());
            assertNotNull(u.getLogin());
            assertNotNull(u.getRole());
            assertNotNull(u.getPassword());
            assertNotNull(u.getCreatedAt());
            assertNotNull(u.getUpdatedAt());
        });
    }

    @Test
    @Order(8)
    void testUpdateUser() {
        String oldLogin = uniqueLogin("old_login_update");
        Long userId = userDAO.createUser(new UserDTO(oldLogin, "USER", "old_pass"));

        String newLogin = uniqueLogin("new_login_update");
        UserDTO userToUpdate = new UserDTO(newLogin, "ADMIN", "new_pass");
        userToUpdate.setId(userId);
        boolean updated = userDAO.updateUser(userToUpdate);

        assertTrue(updated);

        Optional<UserDTO> updatedUser = userDAO.findById(userId);
        assertTrue(updatedUser.isPresent());
        assertEquals(newLogin, updatedUser.get().getLogin());
        assertEquals("ADMIN", updatedUser.get().getRole());
        assertEquals("new_pass", updatedUser.get().getPassword());
    }

    @Test
    @Order(9)
    void testUpdateUser_NotFound() {
        UserDTO nonExistentUser = new UserDTO(uniqueLogin("non_existent"), "ROLE", "pass");
        nonExistentUser.setId(999999L);

        boolean updated = userDAO.updateUser(nonExistentUser);

        assertFalse(updated);
        logger.info("Обновление несуществующего пользователя обработано корректно");
    }

    @Test
    @Order(10)
    void testDeleteUser() {
        String uniqueLogin = uniqueLogin("to_delete_user");
        Long userId = userDAO.createUser(new UserDTO(uniqueLogin, "USER", "pass"));

        boolean deleted = userDAO.deleteUser(userId);

        assertTrue(deleted);

        Optional<UserDTO> deletedUser = userDAO.findById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    @Order(11)
    void testDeleteUser_NotFound() {
        boolean deleted = userDAO.deleteUser(999999L);
        assertFalse(deleted);
    }

    @Test
    @Order(12)
    void testFindByRole() {
        userDAO.createUser(new UserDTO(uniqueLogin("admin_role_1"), "ADMIN", "pass1"));
        userDAO.createUser(new UserDTO(uniqueLogin("admin_role_2"), "ADMIN", "pass2"));
        userDAO.createUser(new UserDTO(uniqueLogin("user_role_1"), "USER", "pass3"));
        userDAO.createUser(new UserDTO(uniqueLogin("admin_role_3"), "ADMIN", "pass4"));

        List<UserDTO> admins = userDAO.findByRole("ADMIN");

        assertFalse(admins.isEmpty());

        assertTrue(admins.size() >= 3);
        admins.forEach(admin -> {
            assertEquals("ADMIN", admin.getRole());
            assertNotNull(admin.getLogin());
            assertNotNull(admin.getPassword());
        });
        logger.info("Найдено {} администраторов", admins.size());
    }

    @Test
    @Order(13)
    void testFindByRole_EmptyResult() {
        List<UserDTO> superAdmins = userDAO.findByRole("SUPER_ADMIN");
        assertTrue(superAdmins.isEmpty());
    }

    @Test
    @Order(14)
    void testUserTimestamps() {
        String uniqueLogin = uniqueLogin("timestamp_test");
        UserDTO user = new UserDTO(uniqueLogin, "USER", "password");

        Long userId = userDAO.createUser(user);
        Optional<UserDTO> createdUser = userDAO.findById(userId);

        assertTrue(createdUser.isPresent());
        assertNotNull(createdUser.get().getCreatedAt());
        assertNotNull(createdUser.get().getUpdatedAt());
        assertEquals(createdUser.get().getCreatedAt(), createdUser.get().getUpdatedAt());
        logger.info("Временные метки созданы корректно: created={}, updated={}",
                createdUser.get().getCreatedAt(), createdUser.get().getUpdatedAt());
    }

    @Test
    @Order(15)
    void testMultipleUserOperations() {
        String login1 = uniqueLogin("multi_op_1");
        String login2 = uniqueLogin("multi_op_2");

        UserDTO user1 = new UserDTO(login1, "USER", "pass1");
        UserDTO user2 = new UserDTO(login2, "ADMIN", "pass2");

        Long id1 = userDAO.createUser(user1);
        Long id2 = userDAO.createUser(user2);

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);

        String updatedLogin = uniqueLogin("updated_multi_1");
        UserDTO updatedUser1 = new UserDTO(updatedLogin, "MODERATOR", "new_pass1");
        updatedUser1.setId(id1);
        boolean updateResult = userDAO.updateUser(updatedUser1);

        assertTrue(updateResult);
        Optional<UserDTO> foundUpdated = userDAO.findById(id1);
        assertTrue(foundUpdated.isPresent());
        assertEquals(updatedLogin, foundUpdated.get().getLogin());

        boolean deleteResult = userDAO.deleteUser(id2);

        assertTrue(deleteResult);
        Optional<UserDTO> deletedUser = userDAO.findById(id2);
        assertFalse(deletedUser.isPresent());

        logger.info("Множественные операции выполнены успешно");
    }

    @AfterEach
    void tearDown() {
        try {
            List<UserDTO> testUsers = userDAO.findAll().stream()
                    .filter(user -> user.getLogin().startsWith(testPrefix))
                    .toList();

            for (UserDTO user : testUsers) {
                userDAO.deleteUser(user.getId());
            }

            if (!testUsers.isEmpty()) {
                logger.info("Очищено {} тестовых пользователей", testUsers.size());
            }
        } catch (Exception e) {
            logger.warn("Ошибка при очистке тестовых данных: {}", e.getMessage());
        }
    }
}