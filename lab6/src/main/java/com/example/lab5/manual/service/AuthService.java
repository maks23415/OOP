package com.example.lab5.manual.service;

import com.example.lab5.manual.dao.UserDAO;
import com.example.lab5.manual.dto.UserDTO;
import com.example.lab5.manual.logging.SecurityLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // Метод с HttpServletRequest для улучшенного логирования
    public Optional<UserDTO> authenticate(String authHeader, HttpServletRequest request) {
        String clientIP = getClientIP(request);

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            logger.warn("Missing or invalid Authorization header from IP: {}", clientIP);
            SecurityLogger.logAuthenticationFailure("unknown", "Missing Authorization header", clientIP);
            return Optional.empty();
        }

        try {
            String base64Credentials = authHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                    StandardCharsets.UTF_8);
            final String[] values = credentials.split(":", 2);

            if (values.length != 2) {
                logger.warn("Invalid credentials format from IP: {}", clientIP);
                SecurityLogger.logAuthenticationFailure("unknown", "Invalid credentials format", clientIP);
                return Optional.empty();
            }

            String login = values[0];
            String password = values[1];

            Optional<UserDTO> userOpt = userDAO.findByLogin(login);
            if (userOpt.isEmpty()) {
                logger.warn("User not found: {} from IP: {}", login, clientIP);
                SecurityLogger.logAuthenticationFailure(login, "User not found", clientIP);
                return Optional.empty();
            }

            UserDTO user = userOpt.get();
            boolean authenticated = user.getPassword().equals(password);

            if (authenticated) {
                logger.info("User authenticated: {} with role: {} from IP: {}", login, user.getRole(), clientIP);
                SecurityLogger.logAuthenticationSuccess(login, user.getRole(), clientIP);
                return Optional.of(user);
            } else {
                logger.warn("Authentication failed for user: {} from IP: {}", login, clientIP);
                SecurityLogger.logAuthenticationFailure(login, "Invalid password", clientIP);
                return Optional.empty();
            }

        } catch (Exception e) {
            logger.error("Authentication error from IP: {}", clientIP, e);
            SecurityLogger.logAuthenticationFailure("unknown", "Authentication error: " + e.getMessage(), clientIP);
            return Optional.empty();
        }
    }

    // Перегруженный метод для обратной совместимости
    public Optional<UserDTO> authenticate(String authHeader) {
        // Создаем mock request для обратной совместимости
        return authenticate(authHeader, null);
    }

    public boolean hasPermission(UserDTO user, String requiredRole, String resourceOwner, String action, String resource) {
        if (user == null) {
            logger.warn("Permission check failed: user is null");
            return false;
        }

        // ADMIN имеет полный доступ
        if ("ADMIN".equals(user.getRole())) {
            logger.debug("Admin user {} has permission for {} on {}", user.getLogin(), action, resource);
            SecurityLogger.logAuthorizationSuccess(user.getLogin(), action, resource);
            return true;
        }

        // USER может работать только со своими данными
        if ("USER".equals(user.getRole())) {
            boolean hasAccess = user.getLogin().equals(resourceOwner);
            if (hasAccess) {
                SecurityLogger.logAuthorizationSuccess(user.getLogin(), action, resource);
            } else {
                logger.warn("User {} attempted to access resource owned by {} for action: {}",
                        user.getLogin(), resourceOwner, action);
                SecurityLogger.logAuthorizationFailure(user.getLogin(), action, resource,
                        "Access denied to resource owned by " + resourceOwner);
            }
            return hasAccess;
        }

        logger.warn("User {} with role {} has no permission for {} on {}",
                user.getLogin(), user.getRole(), action, resource);
        SecurityLogger.logAuthorizationFailure(user.getLogin(), action, resource,
                "Invalid role: " + user.getRole());
        return false;
    }

    // Перегруженный метод для обратной совместимости
    public boolean hasPermission(UserDTO user, String requiredRole, String resourceOwner) {
        return hasPermission(user, requiredRole, resourceOwner, "access", "resource");
    }

    public boolean isValidRole(String role) {
        return "ADMIN".equals(role) || "USER".equals(role);
    }

    private String getClientIP(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}