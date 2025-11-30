package com.example.lab5.framework.controller;

import com.example.lab5.framework.dto.PointDTO;
import com.example.lab5.framework.entity.Point;
import com.example.lab5.framework.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/points")
public class PointController {

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
        return pointService.getAllPoints().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PointDTO> getPointById(@PathVariable Long id) {
        return pointService.getPointById(id)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/function/{functionId}")
    public List<PointDTO> getPointsByFunction(@PathVariable Long functionId) {
        return pointService.getPointsByFunctionId(functionId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public PointDTO createPoint(@RequestBody PointDTO pointDTO) {
        Point created = pointService.createPoint(
                pointDTO.getFunctionId(),
                pointDTO.getXValue(),
                pointDTO.getYValue()
        );
        return toDTO(created);
    }

    @PostMapping("/generate/{functionId}")
    public ResponseEntity<String> generatePoints(
            @PathVariable Long functionId,
            @RequestParam String functionType,
            @RequestParam double start,
            @RequestParam double end,
            @RequestParam double step) {

        int count = pointService.generateFunctionPoints(functionId, functionType, start, end, step);
        return ResponseEntity.ok("Сгенерировано " + count + " точек");
    }
}