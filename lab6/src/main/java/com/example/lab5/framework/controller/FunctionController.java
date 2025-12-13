package com.example.lab5.framework.controller;

import com.example.lab5.framework.dto.*;
import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.service.FunctionService;
import com.example.lab5.framework.service.MathFunctionService;
import com.example.lab5.framework.service.FactoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class FunctionController {

    private static final Logger logger = LoggerFactory.getLogger(FunctionController.class);

    @Autowired
    private FunctionService functionService;

    @Autowired
    private MathFunctionService mathFunctionService;

    @Autowired
    private FactoryService factoryService;

    private FunctionDTO toDTO(Function function) {
        FunctionDTO dto = new FunctionDTO();
        dto.setId(function.getId());
        dto.setName(function.getName());
        dto.setSignature(function.getSignature());
        dto.setUserId(function.getUser().getId());

        // Добавляем поля для Lab 7
        dto.setFactoryType(function.getFactoryType());
        dto.setMathFunctionKey(function.getMathFunctionKey());
        dto.setCreationMethod(function.getCreationMethod());
        dto.setLeftBound(function.getLeftBound());
        dto.setRightBound(function.getRightBound());
        dto.setPointsCount(function.getPointsCount());

        return dto;
    }

    // Существующие методы...
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

    // НОВЫЕ МЕТОДЫ ДЛЯ LAB 7
    @PostMapping("/functions/create-from-arrays")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public FunctionDTO createFromArrays(@RequestBody CreateFromArraysRequest request) {
        logger.info("POST /api/v1/functions/create-from-arrays - создание из массивов: name={}, points={}, factory={}",
                request.getName(), request.getPoints().size(), request.getFactoryType());

        Function created = functionService.createFromArrays(
                request.getUserId(),
                request.getName(),
                request.getPoints(),
                request.getFactoryType()
        );

        logger.info("Функция создана из массивов с ID: {}", created.getId());
        return toDTO(created);
    }

    @PostMapping("/functions/create-from-math")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public FunctionDTO createFromMathFunction(@RequestBody CreateFromMathRequest request) {
        logger.info("POST /api/v1/functions/create-from-math - создание из MathFunction: name={}, functionKey={}, factory={}",
                request.getName(), request.getMathFunctionKey(), request.getFactoryType());

        Function created = functionService.createFromMathFunction(
                request.getUserId(),
                request.getName(),
                request.getMathFunctionKey(),
                request.getPointsCount(),
                request.getLeftBound(),
                request.getRightBound(),
                request.getFactoryType()
        );

        logger.info("Функция создана из MathFunction с ID: {}", created.getId());
        return toDTO(created);
    }

    @PostMapping("/functions/{functionId}/evaluate")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<EvaluateResponse> evaluateFunction(@PathVariable Long functionId,
                                                             @RequestBody EvaluateRequest request) {
        logger.info("POST /api/v1/functions/{}/evaluate - вычисление значения в точке x={}",
                functionId, request.getX());

        EvaluateResponse response = functionService.evaluateFunction(functionId, request.getX());

        if (response != null) {
            logger.info("Результат вычисления: {} для x={}", response.getY(), request.getX());
            return ResponseEntity.ok(response);
        } else {
            logger.warn("Не удалось вычислить значение функции {} в точке {}", functionId, request.getX());
            return ResponseEntity.badRequest().build();
        }
    }

    // MathFunction API
    @GetMapping("/math-functions")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<MathFunctionDTO> getAllMathFunctions() {
        logger.info("GET /api/v1/math-functions - получение всех математических функций");
        return mathFunctionService.getAllMathFunctions();
    }

    @GetMapping("/math-functions/map")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Map<String, MathFunctionDTO> getFunctionMap() {
        logger.info("GET /api/v1/math-functions/map - получение Map функций");
        return mathFunctionService.getFunctionMap();
    }

    @PostMapping("/math-functions/preview")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public PreviewResponse previewMathFunction(@RequestBody PreviewRequest request) {
        logger.info("POST /api/v1/math-functions/preview - предпросмотр функции: key={}, points={}",
                request.getMathFunctionKey(), request.getPointsCount());

        return mathFunctionService.previewMathFunction(
                request.getMathFunctionKey(),
                request.getPointsCount(),
                request.getLeftBound(),
                request.getRightBound()
        );
    }

    // Factory API
    @GetMapping("/factory/current")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public FactoryResponse getCurrentFactory() {
        logger.info("GET /api/v1/factory/current - получение текущей фабрики");
        return factoryService.getCurrentFactory();
    }

    @PostMapping("/factory/set")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public FactoryResponse setFactory(@RequestBody FactoryRequest request) {
        logger.info("POST /api/v1/factory/set - установка фабрики: {}", request.getFactoryType());
        return factoryService.setFactory(request.getFactoryType());
    }

    @GetMapping("/factory/info")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Map<String, Object> getFactoryInfo() {
        logger.info("GET /api/v1/factory/info - получение информации о фабриках");
        return factoryService.getFactoryInfo();
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