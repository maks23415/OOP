package com.example.lab5.framework.service;

import com.example.lab5.framework.entity.User;
import com.example.lab5.framework.repository.FunctionRepository;
import com.example.lab5.framework.repository.PointRepository;
import com.example.lab5.framework.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private PointRepository pointRepository;

    public User createUser(String login, String role, String password) {
        logger.info("Создание пользователя: login={}, role={}", login, role);
        User user = new User(login, role, password);
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        logger.debug("Поиск пользователя по ID: {}", id);
        return userRepository.findById(id);
    }

    public Optional<User> getUserByLogin(String login) {
        logger.debug("Поиск пользователя по логину: {}", login);
        return userRepository.findByLogin(login);
    }

    public List<User> getAllUsers() {
        logger.debug("Получение всех пользователей");
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(String role) {
        logger.debug("Поиск пользователей по роли: {}", role);
        return userRepository.findByRole(role);
    }

    public User updateUser(Long id, String login, String role, String password) {
        logger.info("Обновление пользователя с ID: {}", id);
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setLogin(login);
            user.setRole(role);
            user.setPassword(password);
            return userRepository.save(user);
        }
        logger.warn("Пользователь с ID {} не найден для обновления", id);
        return null;
    }

    @Transactional
    public boolean deleteUser(Long id) {
        logger.info("Удаление пользователя с ID: {}", id);
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String userLogin = user.getLogin();

                // 1. Сначала удаляем все точки пользователя
                pointRepository.deleteByUserLogin(userLogin);

                // 2. Затем удаляем все функции пользователя
                functionRepository.deleteByUserId(id);

                // 3. Наконец удаляем самого пользователя
                userRepository.delete(user);

                logger.info("Пользователь с ID {} и все связанные данные удалены", id);
                return true;
            }
            logger.warn("Пользователь с ID {} не найден для удаления", id);
            return false;
        } catch (Exception e) {
            logger.error("Ошибка при удалении пользователя с ID {}: {}", id, e.getMessage());
            return false;
        }
    }

    public boolean validateUserCredentials(String login, String password) {
        logger.debug("Проверка учетных данных для пользователя: {}", login);
        Optional<User> user = userRepository.findByLogin(login);
        return user.isPresent() && user.get().getPassword().equals(password);
    }
}
