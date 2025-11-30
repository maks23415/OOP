package com.example.lab5.framework.controller;

import com.example.lab5.framework.dto.FunctionDTO;
import com.example.lab5.framework.entity.Function;
import com.example.lab5.framework.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/functions")
public class FunctionController {

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

    @GetMapping
    public List<FunctionDTO> getAllFunctions() {
        return functionService.getAllFunctions().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FunctionDTO> getFunctionById(@PathVariable Long id) {
        return functionService.getFunctionById(id)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<FunctionDTO> getFunctionsByUser(@PathVariable Long userId) {
        return functionService.getFunctionsByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public FunctionDTO createFunction(@RequestBody FunctionDTO functionDTO) {
        Function created = functionService.createFunction(
                functionDTO.getUserId(),
                functionDTO.getName(),
                functionDTO.getSignature()
        );
        return toDTO(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FunctionDTO> updateFunction(@PathVariable Long id, @RequestBody FunctionDTO functionDTO) {
        Function updated = functionService.updateFunction(
                id,
                functionDTO.getUserId(),
                functionDTO.getName(),
                functionDTO.getSignature()
        );
        if (updated != null) {
            return ResponseEntity.ok(toDTO(updated));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        if (functionService.deleteFunction(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}