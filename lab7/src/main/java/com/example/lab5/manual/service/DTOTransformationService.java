package com.example.lab5.manual.service;

import com.example.lab5.manual.dto.FunctionDTO;
import com.example.lab5.manual.dto.PointDTO;
import com.example.lab5.manual.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DTOTransformationService {
    private static final Logger logger = LoggerFactory.getLogger(DTOTransformationService.class);

    public UserDTO transformToUserDTO(Map<String, Object> data) {
        logger.info("Transforming map data to UserDTO: {}", data);

        try {
            UserDTO userDTO = new UserDTO();

            if (data.containsKey("id")) {
                userDTO.setId(Long.valueOf(data.get("id").toString()));
            }
            if (data.containsKey("login")) {
                userDTO.setLogin(data.get("login").toString());
            }
            if (data.containsKey("role")) {
                userDTO.setRole(data.get("role").toString());
            }
            if (data.containsKey("password")) {
                userDTO.setPassword(data.get("password").toString());
            }

            logger.debug("Successfully transformed to UserDTO: {}", userDTO);
            return userDTO;

        } catch (Exception e) {
            logger.error("Error transforming data to UserDTO: {}", data, e);
            throw new IllegalArgumentException("Invalid user data format", e);
        }
    }

    public FunctionDTO transformToFunctionDTO(Map<String, Object> data) {
        logger.info("Transforming map data to FunctionDTO: {}", data);

        try {
            FunctionDTO functionDTO = new FunctionDTO();

            if (data.containsKey("id")) {
                functionDTO.setId(Long.valueOf(data.get("id").toString()));
            }
            if (data.containsKey("userId") || data.containsKey("u_id")) {
                Long userId = data.containsKey("userId") ?
                        Long.valueOf(data.get("userId").toString()) :
                        Long.valueOf(data.get("u_id").toString());
                functionDTO.setUserId(userId);
            }
            if (data.containsKey("name")) {
                functionDTO.setName(data.get("name").toString());
            }
            if (data.containsKey("signature")) {
                functionDTO.setSignature(data.get("signature").toString());
            }

            logger.debug("Successfully transformed to FunctionDTO: {}", functionDTO);
            return functionDTO;

        } catch (Exception e) {
            logger.error("Error transforming data to FunctionDTO: {}", data, e);
            throw new IllegalArgumentException("Invalid function data format", e);
        }
    }

    public PointDTO transformToPointDTO(Map<String, Object> data) {
        logger.info("Transforming map data to PointDTO: {}", data);

        try {
            PointDTO pointDTO = new PointDTO();

            if (data.containsKey("id")) {
                pointDTO.setId(Long.valueOf(data.get("id").toString()));
            }
            if (data.containsKey("functionId") || data.containsKey("f_id")) {
                Long functionId = data.containsKey("functionId") ?
                        Long.valueOf(data.get("functionId").toString()) :
                        Long.valueOf(data.get("f_id").toString());
                pointDTO.setFunctionId(functionId);
            }
            if (data.containsKey("xValue") || data.containsKey("x_value")) {
                Double xValue = data.containsKey("xValue") ?
                        Double.valueOf(data.get("xValue").toString()) :
                        Double.valueOf(data.get("x_value").toString());
                pointDTO.setXValue(xValue);
            }
            if (data.containsKey("yValue") || data.containsKey("y_value")) {
                Double yValue = data.containsKey("yValue") ?
                        Double.valueOf(data.get("yValue").toString()) :
                        Double.valueOf(data.get("y_value").toString());
                pointDTO.setYValue(yValue);
            }

            logger.debug("Successfully transformed to PointDTO: {}", pointDTO);
            return pointDTO;

        } catch (Exception e) {
            logger.error("Error transforming data to PointDTO: {}", data, e);
            throw new IllegalArgumentException("Invalid point data format", e);
        }
    }

    public Map<String, Object> transformToMap(UserDTO userDTO) {
        logger.debug("Transforming UserDTO to map: {}", userDTO);
        return Map.of(
                "id", userDTO.getId(),
                "login", userDTO.getLogin(),
                "role", userDTO.getRole(),
                "password", userDTO.getPassword()
        );
    }

    public Map<String, Object> transformToMap(FunctionDTO functionDTO) {
        logger.debug("Transforming FunctionDTO to map: {}", functionDTO);
        return Map.of(
                "id", functionDTO.getId(),
                "userId", functionDTO.getUserId(),
                "name", functionDTO.getName(),
                "signature", functionDTO.getSignature()
        );
    }

    public Map<String, Object> transformToMap(PointDTO pointDTO) {
        logger.debug("Transforming PointDTO to map: {}", pointDTO);
        return Map.of(
                "id", pointDTO.getId(),
                "functionId", pointDTO.getFunctionId(),
                "xValue", pointDTO.getXValue(),
                "yValue", pointDTO.getYValue()
        );
    }
}