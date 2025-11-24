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
        // Короткий префикс для быстрого поиска
        testPrefix = "t_" + UUID.randomUUID().toString().substring(0, 4) + "_";
        logger.info("Установлен префикс тестов: {}", testPrefix);
    }

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    private String uniqueLogin(String baseName) {
        return testPrefix + baseName;
    }

    @Test
    @Order(1)
    void testCreateUser() {
        String uniqueLogin = uniqueLogin("user1");
        UserDTO user = new UserDTO(uniqueLogin, "USER", "test_password");

        Long userId = userDAO.createUser(user);

        assertNotNull(userId);
        assertTrue(userId > 0);

        Optional<UserDTO> foundUser = userDAO.findById(userId);
        assertTrue(foundUser.isPresent());
        assertEquals(uniqueLogin, foundUser.get().getLogin());
        assertEquals("USER", foundUser.get().getRole());
    }

    @Test
    @Order(2)
    void testCreateUserWithDuplicateLogin() {
        String duplicateLogin = uniqueLogin("dup_login");
        UserDTO user1 = new UserDTO(duplicateLogin, "USER", "pass1");
        userDAO.createUser(user1);

        UserDTO user2 = new UserDTO(duplicateLogin, "ADMIN", "pass2");

        assertThrows(RuntimeException.class, () -> {
            userDAO.createUser(user2);
        });
    }

    @Test
    @Order(3)
    void testFindByLogin() {
        String uniqueLogin = uniqueLogin("find_login");
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
        String nonExistentLogin = uniqueLogin("nonexistent");
        Optional<UserDTO> foundUser = userDAO.findByLogin(nonExistentLogin);
        assertFalse(foundUser.isPresent());
    }

    @Test
    @Order(5)
    void testFindById() {
        String uniqueLogin = uniqueLogin("find_id");
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

        userDAO.createUser(new UserDTO(uniqueLogin("all1"), "USER", "pass1"));
        userDAO.createUser(new UserDTO(uniqueLogin("all2"), "ADMIN", "pass2"));

        List<UserDTO> users = userDAO.findAll();

        assertFalse(users.isEmpty());
        assertTrue(users.size() >= initialCount + 2);
    }

    @Test
    @Order(8)
    void testUpdateUser() {
        String oldLogin = uniqueLogin("old_login");
        Long userId = userDAO.createUser(new UserDTO(oldLogin, "USER", "old_pass"));

        String newLogin = uniqueLogin("new_login");
        UserDTO userToUpdate = new UserDTO(newLogin, "ADMIN", "new_pass");
        userToUpdate.setId(userId);
        boolean updated = userDAO.updateUser(userToUpdate);

        assertTrue(updated);

        Optional<UserDTO> updatedUser = userDAO.findById(userId);
        assertTrue(updatedUser.isPresent());
        assertEquals(newLogin, updatedUser.get().getLogin());
        assertEquals("ADMIN", updatedUser.get().getRole());
    }

    @Test
    @Order(9)
    void testUpdateUser_NotFound() {
        UserDTO nonExistentUser = new UserDTO(uniqueLogin("nonexist"), "ROLE", "pass");
        nonExistentUser.setId(999999L);

        boolean updated = userDAO.updateUser(nonExistentUser);
        assertFalse(updated);
    }

    @Test
    @Order(10)
    void testDeleteUser() {
        String uniqueLogin = uniqueLogin("delete_me");
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
        userDAO.createUser(new UserDTO(uniqueLogin("admin1"), "ADMIN", "pass1"));
        userDAO.createUser(new UserDTO(uniqueLogin("admin2"), "ADMIN", "pass2"));

        List<UserDTO> admins = userDAO.findByRole("ADMIN");

        assertFalse(admins.isEmpty());
        admins.forEach(admin -> assertEquals("ADMIN", admin.getRole()));
    }

    @Test
    @Order(13)
    void testFindByRole_EmptyResult() {
        List<UserDTO> superAdmins = userDAO.findByRole("SUPER_ADMIN");
        assertTrue(superAdmins.isEmpty());
    }

    @Test
    @Order(14)
    void testMultipleUserOperations() {
        String login1 = uniqueLogin("multi1");
        String login2 = uniqueLogin("multi2");

        Long id1 = userDAO.createUser(new UserDTO(login1, "USER", "pass1"));
        Long id2 = userDAO.createUser(new UserDTO(login2, "ADMIN", "pass2"));

        assertNotNull(id1);
        assertNotNull(id2);

        UserDTO updatedUser = new UserDTO(uniqueLogin("updated"), "MODERATOR", "new_pass");
        updatedUser.setId(id1);
        boolean updateResult = userDAO.updateUser(updatedUser);
        assertTrue(updateResult);

        boolean deleteResult = userDAO.deleteUser(id2);
        assertTrue(deleteResult);
    }

    @AfterEach
    void tearDown() {
        cleanTestData();
    }

    private void cleanTestData() {
        long startTime = System.currentTimeMillis();

        try {
            // Прямой поиск тестовых пользователей по префиксу
            List<UserDTO> testUsers = userDAO.findAll().stream()
                    .filter(user -> user.getLogin().startsWith(testPrefix))
                    .toList();

            // Пакетное удаление
            for (UserDTO user : testUsers) {
                userDAO.deleteUser(user.getId());
            }

            long duration = System.currentTimeMillis() - startTime;
            if (!testUsers.isEmpty()) {
                logger.info("Очищено {} пользователей за {} мс", testUsers.size(), duration);
            }

        } catch (Exception e) {
            logger.warn("Ошибка при очистке: {}", e.getMessage());
        }
    }
}