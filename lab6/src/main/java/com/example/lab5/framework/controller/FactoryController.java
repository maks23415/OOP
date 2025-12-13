package com.example.lab5.framework.controller;

import com.example.lab5.framework.dto.FactoryRequest;
import com.example.lab5.framework.dto.FactoryResponse;
import com.example.lab5.framework.service.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/factory")
public class FactoryController {

    @Autowired
    private FactoryService factoryService;

    @GetMapping("/current")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public FactoryResponse getCurrentFactory() {
        return factoryService.getCurrentFactory();
    }

    @PostMapping("/set")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public FactoryResponse setFactory(@RequestBody FactoryRequest request) {
        return factoryService.setFactory(request.getFactoryType());
    }

    @GetMapping("/info")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Map<String, Object> getFactoryInfo() {
        return factoryService.getFactoryInfo();
    }
}