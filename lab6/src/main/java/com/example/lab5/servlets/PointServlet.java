package com.example.lab5.servlets;

import com.example.lab5.manual.dto.PointDTO;
import com.example.lab5.manual.service.PointService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@WebServlet("/points/*")
public class PointServlet extends HttpServlet {
    private PointService pointService;
    private ObjectMapper objectMapper;
    private static final Logger logger = Logger.getLogger(PointServlet.class.getName());

    @Override
    public void init() throws ServletException {
        this.pointService = new PointService();
        this.objectMapper = new ObjectMapper();
        logger.info("PointServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();

        logger.info("GET request for path: " + pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /points - получить все точки
                List<PointDTO> points = pointService.getAllPoints();
                logger.info("Retrieved " + points.size() + " points");
                objectMapper.writeValue(resp.getWriter(), points);

            } else if (pathInfo.startsWith("/function/")) {
                // GET /points/function/{functionId} - получить точки по function ID
                Long functionId = Long.parseLong(pathInfo.substring(10));
                List<PointDTO> points = pointService.getPointsByFunctionId(functionId);
                logger.info("Found " + points.size() + " points for function: " + functionId);
                objectMapper.writeValue(resp.getWriter(), points);

            } else if (pathInfo.startsWith("/max/")) {
                // GET /points/max/{functionId} - получить точку с максимальным Y
                Long functionId = Long.parseLong(pathInfo.substring(5));
                PointDTO maxPoint = pointService.findMaxYPoint(functionId);

                if (maxPoint != null) {
                    objectMapper.writeValue(resp.getWriter(), maxPoint);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(),
                            new ErrorResponse("No points found for function: " + functionId));
                }

            } else if (pathInfo.startsWith("/min/")) {
                // GET /points/min/{functionId} - получить точку с минимальным Y
                Long functionId = Long.parseLong(pathInfo.substring(5));
                PointDTO minPoint = pointService.findMinYPoint(functionId);

                if (minPoint != null) {
                    objectMapper.writeValue(resp.getWriter(), minPoint);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(),
                            new ErrorResponse("No points found for function: " + functionId));
                }

            } else if (pathInfo.startsWith("/stats/")) {
                // GET /points/stats/{functionId} - получить статистику точек
                Long functionId = Long.parseLong(pathInfo.substring(7));
                PointService.PointStatistics stats = pointService.getPointStatistics(functionId);

                if (stats != null) {
                    objectMapper.writeValue(resp.getWriter(), stats);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(),
                            new ErrorResponse("No points found for function: " + functionId));
                }

            } else {
                // GET /points/{id} - получить точку по ID
                Long id = Long.parseLong(pathInfo.substring(1));
                Optional<PointDTO> point = pointService.getPointById(id);

                if (point.isPresent()) {
                    logger.info("Found point: " + point.get().getId());
                    objectMapper.writeValue(resp.getWriter(), point.get());
                } else {
                    logger.warning("Point not found with ID: " + id);
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(),
                            new ErrorResponse("Point not found with ID: " + id));
                }
            }
        } catch (NumberFormatException e) {
            logger.warning("Invalid ID format: " + pathInfo);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("Invalid ID format"));
        } catch (Exception e) {
            logger.severe("Error processing GET request: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("Internal server error"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo != null && pathInfo.startsWith("/generate/")) {
                // POST /points/generate/{functionId} - сгенерировать точки для функции
                Long functionId = Long.parseLong(pathInfo.substring(10));

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

                logger.info("Generated " + pointCount + " points for function: " + functionId);
                objectMapper.writeValue(resp.getWriter(),
                        new SuccessResponse("Generated " + pointCount + " points", pointCount));

            } else {
                // POST /points - создать новую точку
                PointDTO point = objectMapper.readValue(req.getInputStream(), PointDTO.class);
                logger.info("Creating new point for function: " + point.getFunctionId());

                // Валидация
                if (point.getFunctionId() == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    objectMapper.writeValue(resp.getWriter(),
                            new ErrorResponse("Function ID is required"));
                    return;
                }

                if (point.getXValue() == null || point.getYValue() == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    objectMapper.writeValue(resp.getWriter(),
                            new ErrorResponse("X and Y values are required"));
                    return;
                }

                Long pointId = pointService.createPoint(
                        point.getFunctionId(),
                        point.getXValue(),
                        point.getYValue()
                );

                logger.info("Point created successfully with ID: " + pointId);
                resp.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(resp.getWriter(),
                        new SuccessResponse("Point created successfully", pointId));
            }

        } catch (Exception e) {
            logger.severe("Error creating point: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("Invalid point data: " + e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();

        logger.info("PUT request for path: " + pathInfo);

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("Point ID is required"));
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            PointDTO point = objectMapper.readValue(req.getInputStream(), PointDTO.class);

            logger.info("Updating point with ID: " + id);

            boolean updated = pointService.updatePoint(
                    id,
                    point.getFunctionId(),
                    point.getXValue(),
                    point.getYValue()
            );

            if (updated) {
                logger.info("Point updated successfully: " + id);
                objectMapper.writeValue(resp.getWriter(),
                        new SuccessResponse("Point updated successfully", id));
            } else {
                logger.warning("Point not found for update: " + id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(),
                        new ErrorResponse("Point not found with ID: " + id));
            }

        } catch (NumberFormatException e) {
            logger.warning("Invalid point ID format: " + pathInfo);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("Invalid point ID format"));
        } catch (Exception e) {
            logger.severe("Error updating point: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("Internal server error"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();

        logger.info("DELETE request for path: " + pathInfo);

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("Point ID is required"));
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            logger.info("Deleting point with ID: " + id);

            boolean deleted = pointService.deletePoint(id);
            if (deleted) {
                logger.info("Point deleted successfully: " + id);
                objectMapper.writeValue(resp.getWriter(),
                        new SuccessResponse("Point deleted successfully", id));
            } else {
                logger.warning("Point not found for deletion: " + id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(),
                        new ErrorResponse("Point not found with ID: " + id));
            }

        } catch (NumberFormatException e) {
            logger.warning("Invalid point ID format: " + pathInfo);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("Invalid point ID format"));
        } catch (Exception e) {
            logger.severe("Error deleting point: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("Internal server error"));
        }
    }

    // Вспомогательные классы для запросов и ответов
    private static class SuccessResponse {
        private String message;
        private Long id;
        private boolean success = true;

        public SuccessResponse(String message, Long id) {
            this.message = message;
            this.id = id;
        }

        public SuccessResponse(String message, int count) {
            this.message = message;
            this.id = (long) count;
        }

        public String getMessage() { return message; }
        public Long getId() { return id; }
        public boolean isSuccess() { return success; }
    }

    private static class ErrorResponse {
        private String error;
        private boolean success = false;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
        public boolean isSuccess() { return success; }
    }

    private static class PointGenerationRequest {
        public String functionType;
        public double start;
        public double end;
        public double step;
    }
}