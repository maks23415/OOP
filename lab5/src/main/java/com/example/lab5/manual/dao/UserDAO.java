package com.example.lab5.manual.dao;

import com.example.lab5.manual.config.DatabaseConfig;
import com.example.lab5.manual.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    public Long createUser(UserDTO user) {
        String sql = "INSERT INTO users (login, role, password) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getRole());
            stmt.setString(3, user.getPassword());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Создание пользователя не удалось, нет affected rows");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    logger.info("Создан пользователь с ID: {}, логин: {}", id, user.getLogin());
                    return id;
                } else {
                    throw new SQLException("Создание пользователя не удалось, ID не получен");
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при создании пользователя: {}", user.getLogin(), e);
            throw new RuntimeException("Database error", e);
        }
    }

    public Optional<UserDTO> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UserDTO user = mapResultSetToUser(rs);
                logger.debug("Найден пользователь по ID {}: {}", id, user.getLogin());
                return Optional.of(user);
            }
            logger.debug("Пользователь с ID {} не найден", id);
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Ошибка при поиске пользователя по ID: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public Optional<UserDTO> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UserDTO user = mapResultSetToUser(rs);
                logger.debug("Найден пользователь по логину: {}", login);
                return Optional.of(user);
            }
            logger.debug("Пользователь с логином {} не найден", login);
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Ошибка при поиске пользователя по логину: {}", login, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<UserDTO> findAll() {
        String sql = "SELECT * FROM users ORDER BY id";
        List<UserDTO> users = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            logger.debug("Найдено {} пользователей", users.size());
            return users;
        } catch (SQLException e) {
            logger.error("Ошибка при получении всех пользователей", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<UserDTO> findByRole(String role) {
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY login";
        List<UserDTO> users = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            logger.debug("Найдено {} пользователей с ролью {}", users.size(), role);
            return users;
        } catch (SQLException e) {
            logger.error("Ошибка при поиске пользователей по роли: {}", role, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public boolean updateUser(UserDTO user) {
        String sql = "UPDATE users SET login = ?, role = ?, password = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getRole());
            stmt.setString(3, user.getPassword());
            stmt.setLong(4, user.getId());

            int affectedRows = stmt.executeUpdate();
            boolean updated = affectedRows > 0;

            if (updated) {
                logger.info("Обновлен пользователь с ID: {}", user.getId());
            } else {
                logger.warn("Пользователь с ID {} не найден для обновления", user.getId());
            }
            return updated;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении пользователя с ID: {}", user.getId(), e);
            throw new RuntimeException("Database error", e);
        }
    }

    public boolean deleteUser(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            boolean deleted = affectedRows > 0;

            if (deleted) {
                logger.info("Удален пользователь с ID: {}", id);
            } else {
                logger.warn("Пользователь с ID {} не найден для удаления", id);
            }
            return deleted;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении пользователя с ID: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
    }


    private UserDTO mapResultSetToUser(ResultSet rs) throws SQLException {
        UserDTO user = new UserDTO();
        user.setId(rs.getLong("id"));
        user.setLogin(rs.getString("login"));
        user.setRole(rs.getString("role"));
        user.setPassword(rs.getString("password"));

        return user;
    }
}
