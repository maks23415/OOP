package com.example.lab5.servlets;

import com.example.lab5.manual.dto.FunctionDTO;
import com.example.lab5.manual.dto.UserDTO;
import com.example.lab5.manual.service.FunctionService;
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

@WebServlet("/functions/*")
public class FunctionServlet extends BaseAuthServlet {
    private static final Logger logger = Logger.getLogger(FunctionServlet.class.getName());
    private FunctionService functionService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        this.functionService = new FunctionService();
        this.objectMapper = new ObjectMapper();
        logger.info("FunctionServlet initialized with Basic Auth");
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
                // GET /functions - получить все функции (только ADMIN)
                if (!checkPermission(currentUser.get(), "ADMIN", null)) {
                    sendForbidden(resp, "Admin access required");
                    return;
                }

                List<FunctionDTO> functions = functionService.getAllFunctions();
                logger.info("Admin " + currentUser.get().getLogin() + " retrieved " + functions.size() + " functions");
                objectMapper.writeValue(resp.getWriter(), functions);

            } else if (pathInfo.startsWith("/user/")) {
                // GET /functions/user/{userId} - получить функции по user ID
                Long userId = Long.parseLong(pathInfo.substring(6));

                // Получаем логин владельца для проверки прав
                Optional<UserDTO> targetUser = userService.getUserById(userId);
                if (targetUser.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("User not found"));
                    return;
                }

                // USER может видеть только свои функции, ADMIN - любые
                if (!checkPermission(currentUser.get(), "ADMIN", targetUser.get().getLogin())) {
                    sendForbidden(resp, "Access denied to user functions");
                    return;
                }

                List<FunctionDTO> functions = functionService.getFunctionsByUserId(userId);
                logger.info("User " + currentUser.get().getLogin() + " retrieved " + functions.size() + " functions for user ID: " + userId);
                objectMapper.writeValue(resp.getWriter(), functions);

            } else if (pathInfo.startsWith("/name/")) {
                // GET /functions/name/{name} - получить функции по имени (только ADMIN)
                if (!checkPermission(currentUser.get(), "ADMIN", null)) {
                    sendForbidden(resp, "Admin access required");
                    return;
                }

                String name = pathInfo.substring(6);
                List<FunctionDTO> functions = functionService.getFunctionsByName(name);
                logger.info("Admin " + currentUser.get().getLogin() + " found " + functions.size() + " functions with name: " + name);
                objectMapper.writeValue(resp.getWriter(), functions);

            } else if (pathInfo.startsWith("/stats/")) {
                // GET /functions/stats/{functionId} - получить статистику функции
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

                FunctionService.FunctionStatistics stats = functionService.getFunctionStatistics(functionId);
                if (stats != null) {
                    objectMapper.writeValue(resp.getWriter(), stats);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function not found with ID: " + functionId));
                }

            } else {
                // GET /functions/{id} - получить функцию по ID
                Long id = Long.parseLong(pathInfo.substring(1));
                Optional<FunctionDTO> function = functionService.getFunctionById(id);

                if (function.isPresent()) {
                    // Получаем владельца функции для проверки прав
                    Optional<UserDTO> functionOwner = userService.getUserById(function.get().getUserId());
                    if (functionOwner.isEmpty()) {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function owner not found"));
                        return;
                    }

                    // USER может видеть только свои функции, ADMIN - любые
                    if (!checkPermission(currentUser.get(), "ADMIN", functionOwner.get().getLogin())) {
                        sendForbidden(resp, "Access denied to function");
                        return;
                    }

                    logger.info("User " + currentUser.get().getLogin() + " accessed function: " + function.get().getName());
                    objectMapper.writeValue(resp.getWriter(), function.get());
                } else {
                    logger.warning("Function not found with ID: " + id);
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function not found with ID: " + id));
                }
            }
        } catch (NumberFormatException e) {
            logger.warning("Invalid ID format: " + pathInfo);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Invalid ID format"));
        } catch (Exception e) {
            logger.severe("Error processing GET request: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Internal server error"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        // Аутентификация
        Optional<UserDTO> currentUser = authenticate(req);
        if (currentUser.isEmpty()) {
            sendUnauthorized(resp, "Authentication required");
            return;
        }

        try {
            FunctionDTO function = objectMapper.readValue(req.getInputStream(), FunctionDTO.class);
            logger.info("User " + currentUser.get().getLogin() + " creating new function: " + function.getName());

            // Валидация
            if (function.getUserId() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("User ID is required"));
                return;
            }

            if (function.getName() == null || function.getName().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function name is required"));
                return;
            }

            // Проверка прав: USER может создавать функции только для себя, ADMIN - для любого пользователя
            Optional<UserDTO> targetUser = userService.getUserById(function.getUserId());
            if (targetUser.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("User not found"));
                return;
            }

            if (!checkPermission(currentUser.get(), "ADMIN", targetUser.get().getLogin())) {
                sendForbidden(resp, "Access denied to create function for this user");
                return;
            }

            Long functionId = functionService.createFunction(
                    function.getUserId(),
                    function.getName(),
                    function.getSignature()
            );

            logger.info("Function created successfully with ID: " + functionId + " by user: " + currentUser.get().getLogin());
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), new SuccessResponse("Function created successfully", functionId));

        } catch (Exception e) {
            logger.severe("Error creating function: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Invalid function data: " + e.getMessage()));
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
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function ID is required"));
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            FunctionDTO functionUpdates = objectMapper.readValue(req.getInputStream(), FunctionDTO.class);

            // Получаем существующую функцию
            Optional<FunctionDTO> existingFunction = functionService.getFunctionById(id);
            if (existingFunction.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function not found with ID: " + id));
                return;
            }

            // Получаем владельца функции для проверки прав
            Optional<UserDTO> functionOwner = userService.getUserById(existingFunction.get().getUserId());
            if (functionOwner.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function owner not found"));
                return;
            }

            // Проверка прав: USER может обновлять только свои функции, ADMIN - любые
            if (!checkPermission(currentUser.get(), "ADMIN", functionOwner.get().getLogin())) {
                sendForbidden(resp, "Access denied to update function");
                return;
            }

            logger.info("User " + currentUser.get().getLogin() + " updating function with ID: " + id);

            boolean updated = functionService.updateFunction(
                    id,
                    functionUpdates.getUserId(),
                    functionUpdates.getName(),
                    functionUpdates.getSignature()
            );

            if (updated) {
                logger.info("Function updated successfully: " + id);
                objectMapper.writeValue(resp.getWriter(), new SuccessResponse("Function updated successfully", id));
            } else {
                logger.warning("Function not found for update: " + id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function not found with ID: " + id));
            }

        } catch (NumberFormatException e) {
            logger.warning("Invalid function ID format: " + pathInfo);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Invalid function ID format"));
        } catch (Exception e) {
            logger.severe("Error updating function: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Internal server error"));
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
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function ID is required"));
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));

            // Получаем существующую функцию
            Optional<FunctionDTO> existingFunction = functionService.getFunctionById(id);
            if (existingFunction.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function not found with ID: " + id));
                return;
            }

            // Получаем владельца функции для проверки прав
            Optional<UserDTO> functionOwner = userService.getUserById(existingFunction.get().getUserId());
            if (functionOwner.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function owner not found"));
                return;
            }

            // Проверка прав: USER может удалять только свои функции, ADMIN - любые
            if (!checkPermission(currentUser.get(), "ADMIN", functionOwner.get().getLogin())) {
                sendForbidden(resp, "Access denied to delete function");
                return;
            }

            logger.info("User " + currentUser.get().getLogin() + " deleting function with ID: " + id);

            boolean deleted = functionService.deleteFunction(id);
            if (deleted) {
                logger.info("Function deleted successfully: " + id);
                objectMapper.writeValue(resp.getWriter(), new SuccessResponse("Function deleted successfully", id));
            } else {
                logger.warning("Function not found for deletion: " + id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Function not found with ID: " + id));
            }

        } catch (NumberFormatException e) {
            logger.warning("Invalid function ID format: " + pathInfo);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Invalid function ID format"));
        } catch (Exception e) {
            logger.severe("Error deleting function: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Internal server error"));
        }
    }
}
