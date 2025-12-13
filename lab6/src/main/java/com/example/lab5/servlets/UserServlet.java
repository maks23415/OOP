package com.example.lab5.servlets;

import com.example.lab5.manual.dto.UserDTO;
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

@WebServlet("/users/*")
public class UserServlet extends BaseAuthServlet {
    private static final Logger logger = Logger.getLogger(UserServlet.class.getName());
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        this.objectMapper = new ObjectMapper();
        logger.info("UserServlet initialized with Basic Auth");
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
                // GET /users - получить всех пользователей (только для ADMIN)
                if (!checkPermission(currentUser.get(), "ADMIN", null)) {
                    sendForbidden(resp, "Admin access required");
                    return;
                }

                List<UserDTO> users = userService.getAllUsers();
                logger.info("Admin " + currentUser.get().getLogin() + " retrieved " + users.size() + " users");
                objectMapper.writeValue(resp.getWriter(), users);

            } else if (pathInfo.startsWith("/login/")) {
                // GET /users/login/{login} - получить пользователя по логину
                String login = pathInfo.substring(7);

                // USER может видеть только свой профиль, ADMIN - любой
                if (!checkPermission(currentUser.get(), "ADMIN", login)) {
                    sendForbidden(resp, "Access denied to user data");
                    return;
                }

                Optional<UserDTO> user = userService.getUserByLogin(login);
                if (user.isPresent()) {
                    logger.info("User " + currentUser.get().getLogin() + " accessed user: " + login);
                    objectMapper.writeValue(resp.getWriter(), user.get());
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(resp.getWriter(),
                            new ErrorResponse("User not found with login: " + login));
                }

            } else if (pathInfo.startsWith("/role/")) {
                // GET /users/role/{role} - получить пользователей по роли (только ADMIN)
                if (!checkPermission(currentUser.get(), "ADMIN", null)) {
                    sendForbidden(resp, "Admin access required");
                    return;
                }

                String role = pathInfo.substring(6);
                List<UserDTO> users = userService.getUsersByRole(role);
                logger.info("Admin " + currentUser.get().getLogin() + " found " + users.size() + " users with role: " + role);
                objectMapper.writeValue(resp.getWriter(), users);

            } else {
                // GET /users/{id} - получить пользователя по ID
                Long id = Long.parseLong(pathInfo.substring(1));
                Optional<UserDTO> user = userService.getUserById(id);

                if (user.isPresent()) {
                    // Проверка прав доступа
                    if (!checkPermission(currentUser.get(), "ADMIN", user.get().getLogin())) {
                        sendForbidden(resp, "Access denied to user data");
                        return;
                    }

                    logger.info("User " + currentUser.get().getLogin() + " accessed user ID: " + id);
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

        // Регистрация нового пользователя - доступна без аутентификации
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

            // По умолчанию роль USER, если не указана
            if (user.getRole() == null || user.getRole().trim().isEmpty()) {
                user.setRole("USER");
            }

            // Проверка валидности роли
            if (!authService.isValidRole(user.getRole())) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(),
                        new ErrorResponse("Invalid role. Allowed roles: USER, ADMIN"));
                return;
            }

            Long userId = userService.createUser(user.getLogin(), user.getRole(), user.getPassword());
            logger.info("User created successfully with ID: " + userId + " and role: " + user.getRole());

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

        // Аутентификация
        Optional<UserDTO> currentUser = authenticate(req);
        if (currentUser.isEmpty()) {
            sendUnauthorized(resp, "Authentication required");
            return;
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("User ID is required"));
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            UserDTO userUpdates = objectMapper.readValue(req.getInputStream(), UserDTO.class);

            // Получаем пользователя для обновления
            Optional<UserDTO> targetUser = userService.getUserById(id);
            if (targetUser.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(),
                        new ErrorResponse("User not found with ID: " + id));
                return;
            }

            // Проверка прав: USER может обновлять только себя, ADMIN - любого
            if (!checkPermission(currentUser.get(), "ADMIN", targetUser.get().getLogin())) {
                sendForbidden(resp, "Access denied to update user data");
                return;
            }

            logger.info("User " + currentUser.get().getLogin() + " updating user ID: " + id);

            boolean updated = userService.updateUser(id,
                    userUpdates.getLogin(),
                    userUpdates.getRole(),
                    userUpdates.getPassword());

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

        // Аутентификация
        Optional<UserDTO> currentUser = authenticate(req);
        if (currentUser.isEmpty()) {
            sendUnauthorized(resp, "Authentication required");
            return;
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse("User ID is required"));
            return;
        }

        try {
            Long id = Long.parseLong(pathInfo.substring(1));

            // Получаем пользователя для удаления
            Optional<UserDTO> targetUser = userService.getUserById(id);
            if (targetUser.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(),
                        new ErrorResponse("User not found with ID: " + id));
                return;
            }

            // Проверка прав: только ADMIN может удалять пользователей
            if (!checkPermission(currentUser.get(), "ADMIN", null)) {
                sendForbidden(resp, "Admin access required to delete users");
                return;
            }

            logger.info("Admin " + currentUser.get().getLogin() + " deleting user ID: " + id);

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
}