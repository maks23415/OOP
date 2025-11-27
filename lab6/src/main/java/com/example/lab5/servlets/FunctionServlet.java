package com.example.lab5.servlets;

import com.example.lab5.manual.dto.FunctionDTO;
import com.example.lab5.manual.service.FunctionService;
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

@WebServlet("/functions/*")
public class FunctionServlet extends HttpServlet {
    private FunctionService functionService;
    private ObjectMapper objectMapper;
    private static final Logger logger = Logger.getLogger(FunctionServlet.class.getName());

    @Override
    public void init() throws ServletException {
        this.functionService = new FunctionService();
        this.objectMapper = new ObjectMapper();
        logger.info("FunctionServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();

        logger.info("GET request for path: " + pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /functions - получить все функции
                List<FunctionDTO> functions = functionService.getAllFunctions();
                logger.info("Retrieved " + functions.size() + " functions");
                objectMapper.writeValue(resp.getWriter(), functions);

            } else if (pathInfo.startsWith("/user/")) {
                // GET /functions/user/{userId} - получить функции по user ID
                Long userId = Long.parseLong(pathInfo.substring(6));
                List<FunctionDTO> functions = functionService.getFunctionsByUserId(userId);
                logger.info("Found " + functions.size() + " functions for user: " + userId);
                objectMapper.writeValue(resp.getWriter(), functions);

            } else if (pathInfo.startsWith("/name/")) {
                // GET /functions/name/{name} - получить функции по имени
                String name = pathInfo.substring(6);
                List<FunctionDTO> functions = functionService.getFunctionsByName(name);
                logger.info("Found " + functions.size() + " functions with name: " + name);
                objectMapper.writeValue(resp.getWriter(), functions);

            } else if (pathInfo.startsWith("/stats/")) {
                // GET /functions/stats/{functionId} - получить статистику функции
                Long functionId = Long.parseLong(pathInfo.substring(7));
                FunctionService.FunctionStatistics stats = functionService.getFunctionStatistics(functionId);

                if (stats != null) {
                    objectMapper.writeValue(resp.getWriter(), stats);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(),
                            new ErrorResponse("Function not found with ID: " + functionId));
                }

            } else {
                // GET /functions/{id} - получить функцию по ID
                Long id = Long.parseLong(pathInfo.substring(1));
                Optional<FunctionDTO> function = functionService.getFunctionById(id);

                if (function.isPresent()) {
                    logger.info("Found function: " + function.get().getName());
                    objectMapper.writeValue(resp.getWriter(), function.get());
                } else {
                    logger.warning("Function not found with ID: " + id);
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(),
                            new ErrorResponse("Function not found with ID: " + id));
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

        try {
            FunctionDTO function = objectMapper.readValue(req.getInputStream(), FunctionDTO.class);
            logger.info("Creating new function: " + function.getName());

            // Валидация
            if (function.getUserId() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(),
                        new ErrorResponse("User ID is required"));
                return;
            }

            if (function.getName() == null || function.getName().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(),
                        new ErrorResponse("Function name is required"));
                return;
            }

            Long functionId = functionService.createFunction(
                    function.getUserId(),
                    function.getName(),
                    function.getSignature()
            );

            logger.info("Function created successfully with ID: " + functionId);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(),
                    new SuccessResponse("Function created successfully", functionId));

        } catch (Exception e) {
            logger.severe("Error creating function: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("Invalid function data: " + e.getMessage()));
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
                    new ErrorResponse("Function ID is required"));
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            FunctionDTO function = objectMapper.readValue(req.getInputStream(), FunctionDTO.class);

            logger.info("Updating function with ID: " + id);

            boolean updated = functionService.updateFunction(
                    id,
                    function.getUserId(),
                    function.getName(),
                    function.getSignature()
            );

            if (updated) {
                logger.info("Function updated successfully: " + id);
                objectMapper.writeValue(resp.getWriter(),
                        new SuccessResponse("Function updated successfully", id));
            } else {
                logger.warning("Function not found for update: " + id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(),
                        new ErrorResponse("Function not found with ID: " + id));
            }

        } catch (NumberFormatException e) {
            logger.warning("Invalid function ID format: " + pathInfo);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("Invalid function ID format"));
        } catch (Exception e) {
            logger.severe("Error updating function: " + e.getMessage());
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
                    new ErrorResponse("Function ID is required"));
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            logger.info("Deleting function with ID: " + id);

            boolean deleted = functionService.deleteFunction(id);
            if (deleted) {
                logger.info("Function deleted successfully: " + id);
                objectMapper.writeValue(resp.getWriter(),
                        new SuccessResponse("Function deleted successfully", id));
            } else {
                logger.warning("Function not found for deletion: " + id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(),
                        new ErrorResponse("Function not found with ID: " + id));
            }

        } catch (NumberFormatException e) {
            logger.warning("Invalid function ID format: " + pathInfo);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("Invalid function ID format"));
        } catch (Exception e) {
            logger.severe("Error deleting function: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("Internal server error"));
        }
    }

    // Вспомогательные классы для стандартизированных ответов
    private static class SuccessResponse {
        private String message;
        private Long id;
        private boolean success = true;

        public SuccessResponse(String message, Long id) {
            this.message = message;
            this.id = id;
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
}
