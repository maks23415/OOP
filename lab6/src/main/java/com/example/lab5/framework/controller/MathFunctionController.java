package com.example.lab5.framework.controller;

import com.example.lab5.framework.dto.*;
import com.example.lab5.framework.service.MathFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/math-functions")
public class MathFunctionController {

    @Autowired
    private MathFunctionService mathFunctionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<MathFunctionDTO> getAllMathFunctions() {
        return mathFunctionService.getAllMathFunctions();
    }

    @GetMapping("/map")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Map<String, MathFunctionDTO> getFunctionMap() {
        return mathFunctionService.getFunctionMap();
    }

    @PostMapping("/preview")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public PreviewResponse previewMathFunction(@RequestBody PreviewRequest request) {
        return mathFunctionService.previewMathFunction(
                request.getMathFunctionKey(),
                request.getPointsCount(),
                request.getLeftBound(),
                request.getRightBound()
        );
    }
}