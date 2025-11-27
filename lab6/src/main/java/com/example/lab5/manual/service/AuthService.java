package com.example.lab5.manual.service;

import com.example.lab5.manual.dao.UserDAO;
import com.example.lab5.manual.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Optional<UserDTO> authenticate(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            logger.warn("Missing or invalid Authorization header");
            return Optional.empty();
        }

        try {
            String base64Credentials = authHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                    StandardCharsets.UTF_8);
            final String[] values = credentials.split(":", 2);

            if (values.length != 2) {
                logger.warn("Invalid credentials format");
                return Optional.empty();
            }

            String login = values[0];
            String password = values[1];

            Optional<UserDTO> userOpt = userDAO.findByLogin(login);
            if (userOpt.isEmpty()) {
                logger.warn("User not found: {}", login);
                return Optional.empty();
            }

            UserDTO user = userOpt.get();
            boolean authenticated = user.getPassword().equals(password);

            if (authenticated) {
                logger.info("User authenticated: {} with role: {}", login, user.getRole());
                return Optional.of(user);
            } else {
                logger.warn("Authentication failed for user: {}", login);
                return Optional.empty();
            }

        } catch (Exception e) {
            logger.error("Authentication error", e);
            return Optional.empty();
        }
    }

    public boolean hasPermission(UserDTO user, String requiredRole, String resourceOwner) {
        if (user == null) {
            logger.warn("Permission check failed: user is null");
            return false;
        }

        // ADMIN имеет полный доступ
        if ("ADMIN".equals(user.getRole())) {
            logger.debug("Admin user {} has permission", user.getLogin());
            return true;
        }

        // USER может работать только со своими данными
        if ("USER".equals(user.getRole())) {
            boolean hasAccess = user.getLogin().equals(resourceOwner);
            if (!hasAccess) {
                logger.warn("User {} attempted to access resource owned by {}",
                        user.getLogin(), resourceOwner);
            }
            return hasAccess;
        }

        logger.warn("User {} with role {} has no permission for required role {}",
                user.getLogin(), user.getRole(), requiredRole);
        return false;
    }

    public boolean isValidRole(String role) {
        return "ADMIN".equals(role) || "USER".equals(role);
    }
}