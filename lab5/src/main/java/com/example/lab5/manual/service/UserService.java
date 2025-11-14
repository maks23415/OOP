package com.example.lab5.manual.service;

import com.example.lab5.manual.dao.UserDAO;
import com.example.lab5.manual.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Long createUser(String login, String role, String password) {
        logger.info("Создание пользователя: login={}, role={}", login, role);
        UserDTO user = new UserDTO(login, role, password);
        return userDAO.createUser(user);
    }

    public Optional<UserDTO> getUserById(Long id) {
        logger.debug("Поиск пользователя по ID: {}", id);
        return userDAO.findById(id);
    }

    public Optional<UserDTO> getUserByLogin(String login) {
        logger.debug("Поиск пользователя по логину: {}", login);
        return userDAO.findByLogin(login);
    }

    public List<UserDTO> getAllUsers() {
        logger.debug("Получение всех пользователей");
        return userDAO.findAll();
    }

    public List<UserDTO> getUsersByRole(String role) {
        logger.debug("Поиск пользователей по роли: {}", role);
        return userDAO.findByRole(role);
    }

    public boolean updateUser(Long id, String login, String role, String password) {
        logger.info("Обновление пользователя с ID: {}", id);
        Optional<UserDTO> existingUser = userDAO.findById(id);
        if (existingUser.isPresent()) {
            UserDTO user = existingUser.get();
            user.setLogin(login);
            user.setRole(role);
            user.setPassword(password);
            return userDAO.updateUser(user);
        }
        logger.warn("Пользователь с ID {} не найден для обновления", id);
        return false;
    }

    public boolean deleteUser(Long id) {
        logger.info("Удаление пользователя с ID: {}", id);
        return userDAO.deleteUser(id);
    }

    public boolean validateUserCredentials(String login, String password) {
        logger.debug("Проверка учетных данных для пользователя: {}", login);
        Optional<UserDTO> user = userDAO.findByLogin(login);
        return user.isPresent() && user.get().getPassword().equals(password);
    }
}
