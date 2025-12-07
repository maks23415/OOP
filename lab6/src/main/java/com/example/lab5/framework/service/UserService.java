package com.example.lab5.framework.service;

import com.example.lab5.framework.entity.User;
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

    public User createUser(String login, String role, String password) {
        logger.info("Создание пользователя: {}, роль: {}", login, role);

        if (userRepository.findByLogin(login).isPresent()) {
            logger.warn("Пользователь с логином {} уже существует", login);
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        if (!isValidRole(role)) {
            logger.warn("Недопустимая роль: {}", role);
            throw new IllegalArgumentException("Недопустимая роль. Разрешены: ADMIN, USER");
        }

        // ВАЖНО: Сохраняем пароль как есть, БЕЗ {noop} префикса!
        // Spring Security сам добавит его при проверке
        User user = new User(login, role, password);
        User saved = userRepository.save(user);

        logger.info("Пользователь создан с ID: {} (пароль сохранен без префикса: {})",
                saved.getId(), password);
        return saved;
    }

    public List<User> getAllUsers() {
        logger.info("Получение всех пользователей");
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        logger.info("Поиск пользователя по ID: {}", id);
        return userRepository.findById(id);
    }

    public Optional<User> getUserByLogin(String login) {
        logger.info("Поиск пользователя по логину: {}", login);
        return userRepository.findByLogin(login);
    }

    public User updateUser(Long id, String login, String role, String password) {
        logger.info("Обновление пользователя с ID: {}", id);

        return userRepository.findById(id)
                .map(user -> {
                    user.setLogin(login);
                    user.setRole(role);

                    if (password != null && !password.isEmpty()) {
                        // Обновляем пароль как есть, БЕЗ {noop} префикса
                        user.setPassword(password);
                        logger.debug("Пароль обновлен для пользователя ID: {}", id);
                    }

                    if (!isValidRole(role)) {
                        logger.warn("Недопустимая роль: {}", role);
                        throw new IllegalArgumentException("Недопустимая роль. Разрешены: ADMIN, USER");
                    }

                    User updated = userRepository.save(user);
                    logger.info("Пользователь с ID {} обновлен", id);
                    return updated;
                })
                .orElse(null);
    }

    public boolean deleteUser(Long id) {
        logger.info("Удаление пользователя с ID: {}", id);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            logger.info("Пользователь с ID {} удален", id);
            return true;
        }
        logger.warn("Пользователь с ID {} не найден для удаления", id);
        return false;
    }

    private boolean isValidRole(String role) {
        return "ADMIN".equals(role) || "USER".equals(role);
    }
}