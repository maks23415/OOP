package com.example.lab5.framework.controller;

import com.example.lab5.framework.dto.FunctionDTO;
import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.service.FunctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class FunctionController {

    private static final Logger logger = LoggerFactory.getLogger(FunctionController.class);

    @Autowired
    private FunctionService functionService;

    private FunctionDTO toDTO(Function function) {
        FunctionDTO dto = new FunctionDTO();
        dto.setId(function.getId());
        dto.setName(function.getName());
        dto.setSignature(function.getSignature());
        dto.setUserId(function.getUser().getId());
        return dto;
    }

    @GetMapping("/functions")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<FunctionDTO> getAllFunctions() {
        logger.info("GET /api/v1/functions - получение всех функций");
        List<FunctionDTO> result = functionService.getAllFunctions().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        logger.info("Получено {} функций", result.size());
        return result;
    }

    @GetMapping("/functions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<FunctionDTO> getFunctionById(@PathVariable Long id) {
        logger.info("GET /api/v1/functions/{} - получение функции по ID", id);
        return functionService.getFunctionById(id)
                .map(function -> {
                    logger.info("Функция с ID {} найдена: {}", id, function.getName());
                    return ResponseEntity.ok(toDTO(function));
                })
                .orElseGet(() -> {
                    logger.warn("Функция с ID {} не найдена", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/users/{userId}/functions")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<FunctionDTO> getFunctionsByUser(@PathVariable Long userId) {
        logger.info("GET /api/v1/users/{}/functions - получение функций пользователя", userId);
        List<FunctionDTO> result = functionService.getFunctionsByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        logger.info("Найдено {} функций для пользователя {}", result.size(), userId);
        return result;
    }

    @PostMapping("/functions")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public FunctionDTO createFunction(@RequestBody FunctionDTO functionDTO) {
        logger.info("POST /api/v1/functions - создание функции: name={}, userId={}",
                functionDTO.getName(), functionDTO.getUserId());
        Function created = functionService.createFunction(
                functionDTO.getUserId(),
                functionDTO.getName(),
                functionDTO.getSignature()
        );
        logger.info("Функция создана с ID: {}", created.getId());
        return toDTO(created);
    }

    @PutMapping("/functions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<FunctionDTO> updateFunction(@PathVariable Long id, @RequestBody FunctionDTO functionDTO) {
        logger.info("PUT /api/v1/functions/{} - обновление функции", id);
        Function updated = functionService.updateFunction(
                id,
                functionDTO.getUserId(),
                functionDTO.getName(),
                functionDTO.getSignature()
        );
        if (updated != null) {
            logger.info("Функция с ID {} обновлена", id);
            return ResponseEntity.ok(toDTO(updated));
        }
        logger.warn("Функция с ID {} не найдена для обновления", id);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/functions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        logger.info("DELETE /api/v1/functions/{} - удаление функции", id);
        if (functionService.deleteFunction(id)) {
            logger.info("Функция с ID {} удалена", id);
            return ResponseEntity.noContent().build();
        }
        logger.warn("Функция с ID {} не найдена для удаления", id);
        return ResponseEntity.notFound().build();
    }
}