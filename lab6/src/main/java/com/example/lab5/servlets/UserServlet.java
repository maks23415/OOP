package com.example.lab5.servlets;

import com.example.lab5.manual.dto.UserDTO;
import com.example.lab5.manual.service.UserService;
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

@WebServlet("/users/*")
public class UserServlet extends HttpServlet {
    private UserService userService;
    private ObjectMapper objectMapper;
    private static final Logger logger = Logger.getLogger(UserServlet.class.getName());

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
        this.objectMapper = new ObjectMapper();
        logger.info("UserServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();

        logger.info("GET request for path: " + pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /users - получить всех пользователей
                List<UserDTO> users = userService.getAllUsers();
                logger.info("Retrieved " + users.size() + " users");
                objectMapper.writeValue(resp.getWriter(), users);

            } else if (pathInfo.startsWith("/login/")) {
                // GET /users/login/{login} - получить пользователя по логину
                String login = pathInfo.substring(7);
                Optional<UserDTO> user = userService.getUserByLogin(login);

                if (user.isPresent()) {
                    logger.info("Found user by login: " + login);
                    objectMapper.writeValue(resp.getWriter(), user.get());
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(),
                            new ErrorResponse("User not found with login: " + login));
                }

            } else if (pathInfo.startsWith("/role/")) {
                // GET /users/role/{role} - получить пользователей по роли
                String role = pathInfo.substring(6);
                List<UserDTO> users = userService.getUsersByRole(role);
                logger.info("Found " + users.size() + " users with role: " + role);
                objectMapper.writeValue(resp.getWriter(), users);

            } else {
                // GET /users/{id} - получить пользователя по ID
                Long id = Long.parseLong(pathInfo.substring(1));
                Optional<UserDTO> user = userService.getUserById(id);

                if (user.isPresent()) {
                    logger.info("Found user: " + user.get().getLogin());
                    objectMapper.writeValue(resp.getWriter(), user.get());
                } else {
                    logger.warning("User not found with ID: " + id);
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(),
                            new ErrorResponse("User not found with ID: " + id));
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
            UserDTO user = objectMapper.readValue(req.getInputStream(), UserDTO.class);
            logger.info("Creating new user: " + user.getLogin());

            // Валидация
            if (user.getLogin() == null || user.getLogin().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(),
                        new ErrorResponse("Login is required"));
                return;
            }

            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(),
                        new ErrorResponse("Password is required"));
                return;
            }

            Long userId = userService.createUser(user.getLogin(), user.getRole(), user.getPassword());
            logger.info("User created successfully with ID: " + userId);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(),
                    new SuccessResponse("User created successfully", userId));

        } catch (Exception e) {
            logger.severe("Error creating user: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("Invalid user data: " + e.getMessage()));
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
                    new ErrorResponse("User ID is required"));
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            UserDTO user = objectMapper.readValue(req.getInputStream(), UserDTO.class);

            logger.info("Updating user with ID: " + id);

            boolean updated = userService.updateUser(id, user.getLogin(), user.getRole(), user.getPassword());
            if (updated) {
                logger.info("User updated successfully: " + id);
                objectMapper.writeValue(resp.getWriter(),
                        new SuccessResponse("User updated successfully", id));
            } else {
                logger.warning("User not found for update: " + id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(),
                        new ErrorResponse("User not found with ID: " + id));
            }

        } catch (NumberFormatException e) {
            logger.warning("Invalid user ID format: " + pathInfo);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("Invalid user ID format"));
        } catch (Exception e) {
            logger.severe("Error updating user: " + e.getMessage());
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
                    new ErrorResponse("User ID is required"));
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            logger.info("Deleting user with ID: " + id);

            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                logger.info("User deleted successfully: " + id);
                objectMapper.writeValue(resp.getWriter(),
                        new SuccessResponse("User deleted successfully", id));
            } else {
                logger.warning("User not found for deletion: " + id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(),
                        new ErrorResponse("User not found with ID: " + id));
            }

        } catch (NumberFormatException e) {
            logger.warning("Invalid user ID format: " + pathInfo);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("Invalid user ID format"));
        } catch (Exception e) {
            logger.severe("Error deleting user: " + e.getMessage());
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