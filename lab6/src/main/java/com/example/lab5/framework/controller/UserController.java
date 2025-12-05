package com.example.lab5.framework.controller;

import com.example.lab5.framework.dto.UserDTO;
import com.example.lab5.framework.entity.User;
import com.example.lab5.framework.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // Маппинг Entity -> DTO
    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setLogin(user.getLogin());
        dto.setRole(user.getRole());
        // Пароль не возвращаем в ответах
        return dto;
    }

    // Маппинг DTO -> Entity
    private User toEntity(UserDTO userDTO) {
        return new User(userDTO.getLogin(), userDTO.getRole(), userDTO.getPassword());
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        logger.info("GET /api/v1/users - получение всех пользователей");
        List<UserDTO> result = userService.getAllUsers().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        logger.info("Получено {} пользователей", result.size());
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        logger.info("GET /api/v1/users/{} - получение пользователя по ID", id);
        return userService.getUserById(id)
                .map(user -> {
                    logger.info("Пользователь с ID {} найден", id);
                    return ResponseEntity.ok(toDTO(user));
                })
                .orElseGet(() -> {
                    logger.warn("Пользователь с ID {} не найден", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        logger.info("POST /api/v1/users - создание пользователя: login={}", userDTO.getLogin());
        User user = toEntity(userDTO);
        User created = userService.createUser(user.getLogin(), user.getRole(), user.getPassword());
        logger.info("Пользователь создан с ID: {}", created.getId());
        return toDTO(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        logger.info("PUT /api/v1/users/{} - обновление пользователя", id);
        User updated = userService.updateUser(id, userDTO.getLogin(), userDTO.getRole(), userDTO.getPassword());
        if (updated != null) {
            logger.info("Пользователь с ID {} обновлен", id);
            return ResponseEntity.ok(toDTO(updated));
        }
        logger.warn("Пользователь с ID {} не найден для обновления", id);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("DELETE /api/v1/users/{} - удаление пользователя", id);
        if (userService.deleteUser(id)) {
            logger.info("Пользователь с ID {} удален", id);
            return ResponseEntity.noContent().build();
        }
        logger.warn("Пользователь с ID {} не найден для удаления", id);
        return ResponseEntity.notFound().build();
    }
}