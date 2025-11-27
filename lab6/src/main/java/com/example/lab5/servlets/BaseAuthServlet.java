package com.example.lab5.servlets;

import com.example.lab5.manual.dto.UserDTO;
import com.example.lab5.manual.service.AuthService;
import com.example.lab5.manual.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public abstract class BaseAuthServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(BaseAuthServlet.class);
    protected AuthService authService;
    protected UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
        this.authService = new AuthService(new com.example.lab5.manual.dao.UserDAO());
    }

    protected Optional<UserDTO> authenticate(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return authService.authenticate(authHeader);
    }

    protected boolean checkPermission(UserDTO user, String requiredRole, String resourceOwner) {
        return authService.hasPermission(user, requiredRole, resourceOwner);
    }

    protected void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        logger.warn("Unauthorized access: {}", message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setHeader("WWW-Authenticate", "Basic realm=\"Lab5 API\"");
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + message + "\"}");
    }

    protected void sendForbidden(HttpServletResponse response, String message) throws IOException {
        logger.warn("Forbidden access: {}", message);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"" + message + "\"}");
    }

    // Вспомогательные классы для ответов
    protected static class SuccessResponse {
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

    protected static class ErrorResponse {
        private String error;
        private boolean success = false;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
        public boolean isSuccess() { return success; }
    }
}