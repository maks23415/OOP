package com.example.lab5.framework.controller;

import com.example.lab5.framework.dto.PointDTO;
import com.example.lab5.framework.entity.Point;
import com.example.lab5.framework.service.PointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/points")
public class PointController {

    private static final Logger logger = LoggerFactory.getLogger(PointController.class);

    @Autowired
    private PointService pointService;

    private PointDTO toDTO(Point point) {
        PointDTO dto = new PointDTO();
        dto.setId(point.getId());
        dto.setXValue(point.getXValue());
        dto.setYValue(point.getYValue());
        dto.setFunctionId(point.getFunction().getId());
        return dto;
    }

    @GetMapping
    public List<PointDTO> getAllPoints() {
        logger.info("GET /api/v1/points - получение всех точек");
        List<PointDTO> result = pointService.getAllPoints().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        logger.info("Получено {} точек", result.size());
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PointDTO> getPointById(@PathVariable Long id) {
        logger.info("GET /api/v1/points/{} - получение точки по ID", id);
        return pointService.getPointById(id)
                .map(point -> {
                    logger.info("Точка с ID {} найдена", id);
                    return ResponseEntity.ok(toDTO(point));
                })
                .orElseGet(() -> {
                    logger.warn("Точка с ID {} не найдена", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/function/{functionId}")
    public List<PointDTO> getPointsByFunction(@PathVariable Long functionId) {
        logger.info("GET /api/v1/points/function/{} - получение точек функции", functionId);
        List<PointDTO> result = pointService.getPointsByFunctionId(functionId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        logger.info("Найдено {} точек для функции {}", result.size(), functionId);
        return result;
    }

    @PostMapping
    public PointDTO createPoint(@RequestBody PointDTO pointDTO) {
        logger.info("POST /api/v1/points - создание точки для функции {}", pointDTO.getFunctionId());
        Point created = pointService.createPoint(
                pointDTO.getFunctionId(),
                pointDTO.getXValue(),
                pointDTO.getYValue()
        );
        logger.info("Точка создана с ID: {}", created.getId());
        return toDTO(created);
    }

    @PostMapping("/generate/{functionId}")
    public ResponseEntity<String> generatePoints(
            @PathVariable Long functionId,
            @RequestParam String functionType,
            @RequestParam double start,
            @RequestParam double end,
            @RequestParam double step) {

        logger.info("POST /api/v1/points/generate/{} - генерация точек. Тип: {}, от {} до {} шаг {}",
                functionId, functionType, start, end, step);

        int count = pointService.generateFunctionPoints(functionId, functionType, start, end, step);

        logger.info("Сгенерировано {} точек для функции {}", count, functionId);
        return ResponseEntity.ok("Сгенерировано " + count + " точек");
    }
}