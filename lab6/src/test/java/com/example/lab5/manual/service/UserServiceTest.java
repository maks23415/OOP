package com.example.lab5.manual.service;

import com.example.lab5.manual.dao.UserDAO;
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

class UserServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
    private UserDAO userDAO;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        userService = new UserService(userDAO);
        logger.info("Настройка тестовой среды для UserService");
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void testCreateUser() {
        String uniqueLogin = "service_test_user_" + UUID.randomUUID().toString().substring(0, 8);
        Long userId = userService.createUser(uniqueLogin, "USER", "service_pass");

        assertNotNull(userId);
        assertTrue(userId > 0);

        Optional<UserDTO> createdUser = userService.getUserById(userId);
        assertTrue(createdUser.isPresent());
        assertEquals(uniqueLogin, createdUser.get().getLogin());
        assertEquals("USER", createdUser.get().getRole());
        assertEquals("service_pass", createdUser.get().getPassword());
    }

    @Test
    void testGetUserById() {
        String uniqueLogin = "get_by_id_user_" + UUID.randomUUID().toString().substring(0, 8);
        Long userId = userService.createUser(uniqueLogin, "ADMIN", "password123");

        Optional<UserDTO> foundUser = userService.getUserById(userId);

        assertTrue(foundUser.isPresent());
        assertEquals(userId, foundUser.get().getId());
        assertEquals(uniqueLogin, foundUser.get().getLogin());
        assertEquals("ADMIN", foundUser.get().getRole());
    }

    @Test
    void testGetUserById_NotFound() {
        Optional<UserDTO> foundUser = userService.getUserById(999999L);

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testGetUserByLogin() {
        String uniqueLogin = "login_search_user_" + UUID.randomUUID().toString().substring(0, 8);
        userService.createUser(uniqueLogin, "MODERATOR", "mod_pass");

        Optional<UserDTO> foundUser = userService.getUserByLogin(uniqueLogin);

        assertTrue(foundUser.isPresent());
        assertEquals(uniqueLogin, foundUser.get().getLogin());
        assertEquals("MODERATOR", foundUser.get().getRole());
    }

    @Test
    void testGetUserByLogin_NotFound() {
        Optional<UserDTO> foundUser = userService.getUserByLogin("non_existent_login_" + UUID.randomUUID());

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testGetAllUsers() {
        int initialCount = userService.getAllUsers().size();

        String uniqueLogin1 = "all_users_1_" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueLogin2 = "all_users_2_" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueLogin3 = "all_users_3_" + UUID.randomUUID().toString().substring(0, 8);

        userService.createUser(uniqueLogin1, "USER", "pass1");
        userService.createUser(uniqueLogin2, "ADMIN", "pass2");
        userService.createUser(uniqueLogin3, "MODERATOR", "pass3");

        List<UserDTO> allUsers = userService.getAllUsers();

        assertFalse(allUsers.isEmpty());
        assertTrue(allUsers.size() >= initialCount + 3);
        logger.info("Сервис вернул {} пользователей", allUsers.size());

        List<UserDTO> daoUsers = userDAO.findAll();
        assertEquals(daoUsers.size(), allUsers.size());
    }

    @Test
    void testGetUsersByRole() {
        String uniqueLogin1 = "role_user_1_" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueLogin2 = "role_user_2_" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueLogin3 = "role_user_3_" + UUID.randomUUID().toString().substring(0, 8);

        userService.createUser(uniqueLogin1, "ADMIN", "pass1");
        userService.createUser(uniqueLogin2, "ADMIN", "pass2");
        userService.createUser(uniqueLogin3, "USER", "pass3");

        List<UserDTO> admins = userService.getUsersByRole("ADMIN");

        assertFalse(admins.isEmpty());
        assertTrue(admins.size() >= 2);
        admins.forEach(admin -> assertEquals("ADMIN", admin.getRole()));

        List<UserDTO> users = userService.getUsersByRole("USER");
        assertFalse(users.isEmpty());
        users.forEach(user -> assertEquals("USER", user.getRole()));
    }

    @Test
    void testGetUsersByRole_Empty() {
        List<UserDTO> superAdmins = userService.getUsersByRole("SUPER_ADMIN");

        assertTrue(superAdmins.isEmpty());
    }

    @Test
    void testUpdateUser() {
        // Given
        String originalLogin = "to_update_user_" + UUID.randomUUID().toString().substring(0, 8);
        Long userId = userService.createUser(originalLogin, "USER", "old_password");

        String updatedLogin = "updated_user_" + UUID.randomUUID().toString().substring(0, 8);
        boolean updated = userService.updateUser(userId, updatedLogin, "ADMIN", "new_password");

        assertTrue(updated);

        Optional<UserDTO> updatedUser = userService.getUserById(userId);
        assertTrue(updatedUser.isPresent());
        assertEquals(updatedLogin, updatedUser.get().getLogin());
        assertEquals("ADMIN", updatedUser.get().getRole());
        assertEquals("new_password", updatedUser.get().getPassword());
    }

    @Test
    void testUpdateUser_NotFound() {
        boolean updated = userService.updateUser(999999L, "new_login", "ADMIN", "new_pass");

        assertFalse(updated);
    }

    @Test
    void testDeleteUser() {
        String uniqueLogin = "to_delete_service_" + UUID.randomUUID().toString().substring(0, 8);
        Long userId = userService.createUser(uniqueLogin, "USER", "password");

        boolean deleted = userService.deleteUser(userId);

        assertTrue(deleted);

        Optional<UserDTO> deletedUser = userService.getUserById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testDeleteUser_NotFound() {
        boolean deleted = userService.deleteUser(999999L);

        assertFalse(deleted);
    }

    @Test
    void testValidateUserCredentials_Valid() {
        String uniqueLogin = "valid_user_" + UUID.randomUUID().toString().substring(0, 8);
        userService.createUser(uniqueLogin, "USER", "correct_password");

        boolean isValid = userService.validateUserCredentials(uniqueLogin, "correct_password");

        assertTrue(isValid);
    }

    @Test
    void testValidateUserCredentials_InvalidPassword() {
        String uniqueLogin = "invalid_pass_user_" + UUID.randomUUID().toString().substring(0, 8);
        userService.createUser(uniqueLogin, "USER", "correct_password");

        boolean isValid = userService.validateUserCredentials(uniqueLogin, "wrong_password");

        assertFalse(isValid);
    }

    @Test
    void testValidateUserCredentials_UserNotFound() {
        boolean isValid = userService.validateUserCredentials("non_existent_user_" + UUID.randomUUID(), "any_password");

        assertFalse(isValid);
    }

    @Test
    void testUserServiceWithMockDAO() {
        String testLogin = "mock_test_user_" + UUID.randomUUID().toString().substring(0, 8);
        String testRole = "ADMIN";
        String testPassword = "mock_pass";

        Long userId = userService.createUser(testLogin, testRole, testPassword);

        Optional<UserDTO> user = userService.getUserById(userId);
        assertTrue(user.isPresent());

        boolean credentialsValid = userService.validateUserCredentials(testLogin, testPassword);
        assertTrue(credentialsValid);

        boolean credentialsInvalid = userService.validateUserCredentials(testLogin, "wrong_pass");
        assertFalse(credentialsInvalid);

        logger.info("Тестирование сервиса с реальным DAO завершено успешно");
    }

    @Test
    void testMultipleServiceOperations() {
        logger.info("Запуск комплексного теста сервиса");

        String login1 = "workflow_1_" + UUID.randomUUID().toString().substring(0, 8);
        String login2 = "workflow_2_" + UUID.randomUUID().toString().substring(0, 8);

        Long user1 = userService.createUser(login1, "USER", "pass1");
        Long user2 = userService.createUser(login2, "ADMIN", "pass2");

        assertNotNull(user1);
        assertNotNull(user2);

        Optional<UserDTO> foundUser1 = userService.getUserById(user1);
        Optional<UserDTO> foundUser2 = userService.getUserById(user2);

        assertTrue(foundUser1.isPresent());
        assertTrue(foundUser2.isPresent());

        String updatedLogin = "updated_workflow_1_" + UUID.randomUUID().toString().substring(0, 8);
        boolean updated = userService.updateUser(user1, updatedLogin, "MODERATOR", "new_pass1");
        assertTrue(updated);

        Optional<UserDTO> updatedUser = userService.getUserById(user1);
        assertTrue(updatedUser.isPresent());
        assertEquals(updatedLogin, updatedUser.get().getLogin());

        boolean validOld = userService.validateUserCredentials(login1, "pass1");
        boolean validNew = userService.validateUserCredentials(updatedLogin, "new_pass1");

        assertFalse(validOld);
        assertTrue(validNew);

        List<UserDTO> moderators = userService.getUsersByRole("MODERATOR");
        assertTrue(moderators.stream().anyMatch(u -> u.getId().equals(user1)));

        boolean deleted = userService.deleteUser(user2);
        assertTrue(deleted);

        Optional<UserDTO> deletedUser = userService.getUserById(user2);
        assertFalse(deletedUser.isPresent());

        logger.info("Комплексный тест сервиса завершен успешно");
    }

    @Test
    void testServiceErrorHandling() {
        assertThrows(Exception.class, () -> {
            userService.createUser("", "USER", "pass");
        });

        logger.info("Обработка ошибок в сервисе проверена");
    }
}
