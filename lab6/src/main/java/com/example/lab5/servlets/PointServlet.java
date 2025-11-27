package com.example.lab5.servlets;

import com.example.lab5.manual.dto.FunctionDTO;
import com.example.lab5.manual.dto.PointDTO;
import com.example.lab5.manual.dto.UserDTO;
import com.example.lab5.manual.service.FunctionService;
import com.example.lab5.manual.service.PointService;
import com.example.lab5.manual.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@WebServlet("/points/*")
public class PointServlet extends BaseAuthServlet {
    private static final Logger logger = Logger.getLogger(PointServlet.class.getName());
    private PointService pointService;
    private FunctionService functionService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        this.pointService = new PointService();
        this.functionService = new FunctionService();
        this.objectMapper = new ObjectMapper();
        logger.info("PointServlet initialized with Basic Auth");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();

        logger.info("GET request for path: " + pathInfo);

        // Аутентификация
        Optional<UserDTO> currentUser = authenticate(req);
        if (currentUser.isEmpty()) {
            sendUnauthorized(resp, "Authentication required");
            return;
        }

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /points - получить все точки не поддерживается, используем /points/function/{id}
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(),
                        new ErrorResponse("Use /points/function/{functionId} to get points by function"));
                return;

            } else if (pathInfo.startsWith("/function/")) {
                // GET /points/function/{functionId} - получить точки по function ID
                Long functionId = Long.parseLong(pathInfo.substring(10));

                // Получаем функцию для проверки владельца
                Optional<FunctionDTO> function = functionService.getFunctionById(functionId);
                if (function.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function not found"));
                    return;
                }

                // Получаем владельца функции
                Optional<UserDTO> functionOwner = userService.getUserById(function.get().getUserId());
                if (functionOwner.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function owner not found"));
                    return;
                }

                // USER может видеть точки только своих функций, ADMIN - любых
                if (!checkPermission(currentUser.get(), "ADMIN", functionOwner.get().getLogin())) {
                    sendForbidden(resp, "Access denied to function points");
                    return;
                }

                List<PointDTO> points = pointService.getPointsByFunctionId(functionId);
                logger.info("User " + currentUser.get().getLogin() + " retrieved " + points.size() + " points for function: " + functionId);
                objectMapper.writeValue(resp.getWriter(), points);

            } else if (pathInfo.startsWith("/max/")) {
                // GET /points/max/{functionId} - получить точку с максимальным Y
                Long functionId = Long.parseLong(pathInfo.substring(5));

                // Получаем функцию для проверки владельца
                Optional<FunctionDTO> function = functionService.getFunctionById(functionId);
                if (function.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function not found"));
                    return;
                }

                // Получаем владельца функции
                Optional<UserDTO> functionOwner = userService.getUserById(function.get().getUserId());
                if (functionOwner.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function owner not found"));
                    return;
                }

                // USER может видеть точки только своих функций, ADMIN - любых
                if (!checkPermission(currentUser.get(), "ADMIN", functionOwner.get().getLogin())) {
                    sendForbidden(resp, "Access denied to function points");
                    return;
                }

                PointDTO maxPoint = pointService.findMaxYPoint(functionId);
                if (maxPoint != null) {
                    objectMapper.writeValue(resp.getWriter(), maxPoint);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("No points found for function: " + functionId));
                }

            } else if (pathInfo.startsWith("/min/")) {
                // GET /points/min/{functionId} - получить точку с минимальным Y
                Long functionId = Long.parseLong(pathInfo.substring(5));

                // Получаем функцию для проверки владельца
                Optional<FunctionDTO> function = functionService.getFunctionById(functionId);
                if (function.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function not found"));
                    return;
                }

                // Получаем владельца функции
                Optional<UserDTO> functionOwner = userService.getUserById(function.get().getUserId());
                if (functionOwner.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function owner not found"));
                    return;
                }

                // USER может видеть точки только своих функций, ADMIN - любых
                if (!checkPermission(currentUser.get(), "ADMIN", functionOwner.get().getLogin())) {
                    sendForbidden(resp, "Access denied to function points");
                    return;
                }

                PointDTO minPoint = pointService.findMinYPoint(functionId);
                if (minPoint != null) {
                    objectMapper.writeValue(resp.getWriter(), minPoint);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("No points found for function: " + functionId));
                }

            } else if (pathInfo.startsWith("/stats/")) {
                // GET /points/stats/{functionId} - получить статистику точек
                Long functionId = Long.parseLong(pathInfo.substring(7));

                // Получаем функцию для проверки владельца
                Optional<FunctionDTO> function = functionService.getFunctionById(functionId);
                if (function.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function not found"));
                    return;
                }

                // Получаем владельца функции
                Optional<UserDTO> functionOwner = userService.getUserById(function.get().getUserId());
                if (functionOwner.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function owner not found"));
                    return;
                }

                // USER может видеть статистику только своих функций, ADMIN - любых
                if (!checkPermission(currentUser.get(), "ADMIN", functionOwner.get().getLogin())) {
                    sendForbidden(resp, "Access denied to function statistics");
                    return;
                }

                PointService.PointStatistics stats = pointService.getPointStatistics(functionId);
                if (stats != null) {
                    objectMapper.writeValue(resp.getWriter(), stats);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("No points found for function: " + functionId));
                }

            } else {
                // GET /points/{id} - получить точку по ID
                Long id = Long.parseLong(pathInfo.substring(1));
                Optional<PointDTO> point = pointService.getPointById(id);

                if (point.isPresent()) {
                    // Получаем функцию для проверки владельца
                    Optional<FunctionDTO> function = functionService.getFunctionById(point.get().getFunctionId());
                    if (function.isEmpty()) {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function not found"));
                        return;
                    }

                    // Получаем владельца функции
                    Optional<UserDTO> functionOwner = userService.getUserById(function.get().getUserId());
                    if (functionOwner.isEmpty()) {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function owner not found"));
                        return;
                    }

                    // USER может видеть точки только своих функций, ADMIN - любых
                    if (!checkPermission(currentUser.get(), "ADMIN", functionOwner.get().getLogin())) {
                        sendForbidden(resp, "Access denied to point");
                        return;
                    }

                    logger.info("User " + currentUser.get().getLogin() + " accessed point ID: " + id);
                    objectMapper.writeValue(resp.getWriter(), point.get());
                } else {
                    logger.warning("Point not found with ID: " + id);
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Point not found with ID: " + id));
                }
            }
        } catch (NumberFormatException e) {
            logger.warning("Invalid ID format: " + pathInfo);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Invalid ID format"));
        } catch (Exception e) {
            logger.severe("Error processing GET request: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();

        // Аутентификация
        Optional<UserDTO> currentUser = authenticate(req);
        if (currentUser.isEmpty()) {
            sendUnauthorized(resp, "Authentication required");
            return;
        }

        try {
            if (pathInfo != null && pathInfo.startsWith("/generate/")) {
                // POST /points/generate/{functionId} - сгенерировать точки для функции
                Long functionId = Long.parseLong(pathInfo.substring(10));

                // Получаем функцию для проверки владельца
                Optional<FunctionDTO> function = functionService.getFunctionById(functionId);
                if (function.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function not found"));
                    return;
                }

                // Получаем владельца функции
                Optional<UserDTO> functionOwner = userService.getUserById(function.get().getUserId());
                if (functionOwner.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function owner not found"));
                    return;
                }

                // USER может генерировать точки только для своих функций, ADMIN - для любых
                if (!checkPermission(currentUser.get(), "ADMIN", functionOwner.get().getLogin())) {
                    sendForbidden(resp, "Access denied to generate points for this function");
                    return;
                }

                // Параметры генерации из тела запроса
                PointGenerationRequest generationRequest = objectMapper.readValue(
                        req.getInputStream(), PointGenerationRequest.class
                );

                int pointCount = pointService.generateFunctionPoints(
                        functionId,
                        generationRequest.functionType,
                        generationRequest.start,
                        generationRequest.end,
                        generationRequest.step
                );

                logger.info("User " + currentUser.get().getLogin() + " generated " + pointCount + " points for function: " + functionId);
                objectMapper.writeValue(resp.getWriter(),
                        new SuccessResponse("Generated " + pointCount + " points", (long) pointCount));

            } else {
                // POST /points - создать новую точку
                PointDTO point = objectMapper.readValue(req.getInputStream(), PointDTO.class);
                logger.info("User " + currentUser.get().getLogin() + " creating new point for function: " + point.getFunctionId());

                // Валидация
                if (point.getFunctionId() == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function ID is required"));
                    return;
                }

                if (point.getXValue() == null || point.getYValue() == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("X and Y values are required"));
                    return;
                }

                // Получаем функцию для проверки владельца
                Optional<FunctionDTO> function = functionService.getFunctionById(point.getFunctionId());
                if (function.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function not found"));
                    return;
                }

                // Получаем владельца функции
                Optional<UserDTO> functionOwner = userService.getUserById(function.get().getUserId());
                if (functionOwner.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function owner not found"));
                    return;
                }

                // USER может создавать точки только для своих функций, ADMIN - для любых
                if (!checkPermission(currentUser.get(), "ADMIN", functionOwner.get().getLogin())) {
                    sendForbidden(resp, "Access denied to create points for this function");
                    return;
                }

                Long pointId = pointService.createPoint(
                        point.getFunctionId(),
                        point.getXValue(),
                        point.getYValue()
                );

                logger.info("Point created successfully with ID: " + pointId + " by user: " + currentUser.get().getLogin());
                resp.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(resp.getWriter(), new SuccessResponse("Point created successfully", pointId));
            }

        } catch (Exception e) {
            logger.severe("Error creating point: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Invalid point data: " + e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();

        logger.info("PUT request for path: " + pathInfo);

        // Аутентификация
        Optional<UserDTO> currentUser = authenticate(req);
        if (currentUser.isEmpty()) {
            sendUnauthorized(resp, "Authentication required");
            return;
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Point ID is required"));
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            PointDTO pointUpdates = objectMapper.readValue(req.getInputStream(), PointDTO.class);

            // Получаем существующую точку
            Optional<PointDTO> existingPoint = pointService.getPointById(id);
            if (existingPoint.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Point not found with ID: " + id));
                return;
            }

            // Получаем функцию для проверки владельца
            Optional<FunctionDTO> function = functionService.getFunctionById(existingPoint.get().getFunctionId());
            if (function.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function not found"));
                return;
            }

            // Получаем владельца функции
            Optional<UserDTO> functionOwner = userService.getUserById(function.get().getUserId());
            if (functionOwner.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function owner not found"));
                return;
            }

            // USER может обновлять точки только своих функций, ADMIN - любых
            if (!checkPermission(currentUser.get(), "ADMIN", functionOwner.get().getLogin())) {
                sendForbidden(resp, "Access denied to update point");
                return;
            }

            logger.info("User " + currentUser.get().getLogin() + " updating point with ID: " + id);

            boolean updated = pointService.updatePoint(
                    id,
                    pointUpdates.getFunctionId(),
                    pointUpdates.getXValue(),
                    pointUpdates.getYValue()
            );

            if (updated) {
                logger.info("Point updated successfully: " + id);
                objectMapper.writeValue(resp.getWriter(), new SuccessResponse("Point updated successfully", id));
            } else {
                logger.warning("Point not found for update: " + id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Point not found with ID: " + id));
            }

        } catch (NumberFormatException e) {
            logger.warning("Invalid point ID format: " + pathInfo);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Invalid point ID format"));
        } catch (Exception e) {
            logger.severe("Error updating point: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();

        logger.info("DELETE request for path: " + pathInfo);

        // Аутентификация
        Optional<UserDTO> currentUser = authenticate(req);
        if (currentUser.isEmpty()) {
            sendUnauthorized(resp, "Authentication required");
            return;
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Point ID is required"));
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));

            // Получаем существующую точку
            Optional<PointDTO> existingPoint = pointService.getPointById(id);
            if (existingPoint.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Point not found with ID: " + id));
                return;
            }

            // Получаем функцию для проверки владельца
            Optional<FunctionDTO> function = functionService.getFunctionById(existingPoint.get().getFunctionId());
            if (function.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function not found"));
                return;
            }

            // Получаем владельца функции
            Optional<UserDTO> functionOwner = userService.getUserById(function.get().getUserId());
            if (functionOwner.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function owner not found"));
                return;
            }

            // USER может удалять точки только своих функций, ADMIN - любых
            if (!checkPermission(currentUser.get(), "ADMIN", functionOwner.get().getLogin())) {
                sendForbidden(resp, "Access denied to delete point");
                return;
            }

            logger.info("User " + currentUser.get().getLogin() + " deleting point with ID: " + id);

            boolean deleted = pointService.deletePoint(id);
            if (deleted) {
                logger.info("Point deleted successfully: " + id);
                objectMapper.writeValue(resp.getWriter(), new SuccessResponse("Point deleted successfully", id));
            } else {
                logger.warning("Point not found for deletion: " + id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Point not found with ID: " + id));
            }

        } catch (NumberFormatException e) {
            logger.warning("Invalid point ID format: " + pathInfo);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Invalid point ID format"));
        } catch (Exception e) {
            logger.severe("Error deleting point: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    // Вспомогательные классы для запросов и ответов
    private static class PointGenerationRequest {
        public String functionType;
        public double start;
        public double end;
        public double step;
    }
}