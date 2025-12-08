package com.example.lab5.manual.service;

import com.example.lab5.manual.dto.FunctionDTO;
import com.example.lab5.manual.dto.PointDTO;
import com.example.lab5.manual.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DTOTransformationServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(DTOTransformationServiceTest.class);
    private DTOTransformationService transformationService;

    @BeforeEach
    void setUp() {
        transformationService = new DTOTransformationService();
    }

    @Test
    void testTransformToUserDTO() {
        Map<String, Object> userData = Map.of(
                "id", "1",
                "login", "testuser",
                "role", "ADMIN",
                "password", "secret123"
        );

        UserDTO userDTO = transformationService.transformToUserDTO(userData);

        assertEquals(1L, userDTO.getId());
        assertEquals("testuser", userDTO.getLogin());
        assertEquals("ADMIN", userDTO.getRole());
        assertEquals("secret123", userDTO.getPassword());
        logger.info("UserDTO transformation test passed");
    }

    @Test
    void testTransformToFunctionDTO() {
        Map<String, Object> functionData = Map.of(
                "id", "10",
                "u_id", "1",
                "name", "quadratic",
                "signature", "f(x) = x^2"
        );

        FunctionDTO functionDTO = transformationService.transformToFunctionDTO(functionData);

        assertEquals(10L, functionDTO.getId());
        assertEquals(1L, functionDTO.getUserId());
        assertEquals("quadratic", functionDTO.getName());
        assertEquals("f(x) = x^2", functionDTO.getSignature());
        logger.info("FunctionDTO transformation test passed");
    }

    @Test
    void testTransformToPointDTO() {
        Map<String, Object> pointData = Map.of(
                "id", "100",
                "f_id", "10",
                "x_value", "2.5",
                "y_value", "6.25"
        );

        PointDTO pointDTO = transformationService.transformToPointDTO(pointData);

        assertEquals(100L, pointDTO.getId());
        assertEquals(10L, pointDTO.getFunctionId());
        assertEquals(2.5, pointDTO.getXValue());
        assertEquals(6.25, pointDTO.getYValue());
        logger.info("PointDTO transformation test passed");
    }

    @Test
    void testTransformUserDTOToMap() {
        UserDTO userDTO = new UserDTO("testuser", "USER", "password");
        userDTO.setId(1L);

        Map<String, Object> result = transformationService.transformToMap(userDTO);

        assertEquals(1L, result.get("id"));
        assertEquals("testuser", result.get("login"));
        assertEquals("USER", result.get("role"));
        assertEquals("password", result.get("password"));
        logger.info("UserDTO to map transformation test passed");
    }

    @Test
    void testTransformFunctionDTOToMap() {
        FunctionDTO functionDTO = new FunctionDTO(1L, "linear", "f(x) = x");
        functionDTO.setId(10L);

        Map<String, Object> result = transformationService.transformToMap(functionDTO);

        assertEquals(10L, result.get("id"));
        assertEquals(1L, result.get("userId"));
        assertEquals("linear", result.get("name"));
        assertEquals("f(x) = x", result.get("signature"));
        logger.info("FunctionDTO to map transformation test passed");
    }

    @Test
    void testTransformPointDTOToMap() {
        PointDTO pointDTO = new PointDTO(10L, 3.0, 9.0);
        pointDTO.setId(100L);

        Map<String, Object> result = transformationService.transformToMap(pointDTO);

        assertEquals(100L, result.get("id"));
        assertEquals(10L, result.get("functionId"));
        assertEquals(3.0, result.get("xValue"));
        assertEquals(9.0, result.get("yValue"));
        logger.info("PointDTO to map transformation test passed");
    }

    @Test
    void testTransformWithMissingFields() {
        Map<String, Object> incompleteData = Map.of(
                "login", "testuser",
                "role", "USER"
        );

        UserDTO userDTO = transformationService.transformToUserDTO(incompleteData);

        assertNull(userDTO.getId());
        assertEquals("testuser", userDTO.getLogin());
        assertEquals("USER", userDTO.getRole());
        assertNull(userDTO.getPassword());
        logger.info("Transformation with missing fields test passed");
    }

    @Test
    void testTransformWithInvalidData() {
        Map<String, Object> invalidData = Map.of(
                "id", "not_a_number",
                "login", "testuser"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            transformationService.transformToUserDTO(invalidData);
        });
        logger.info("Invalid data transformation error handling test passed");
    }
}