package com.example.lab5.manual.dao;

import com.example.lab5.manual.config.DatabaseConfig;
import com.example.lab5.manual.dto.FunctionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FunctionDAO {
    private static final Logger logger = LoggerFactory.getLogger(FunctionDAO.class);

    public Long createFunction(FunctionDTO function) {
        String sql = "INSERT INTO functions (u_id, name, signature) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, function.getUserId());
            stmt.setString(2, function.getName());
            stmt.setString(3, function.getSignature());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Создание функции не удалось");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    logger.info("Создана функция с ID: {}, название: {}", id, function.getName());
                    return id;
                } else {
                    throw new SQLException("Создание функции не удалось, ID не получен");
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при создании функции: {}", function.getName(), e);
            throw new RuntimeException("Database error", e);
        }
    }

    public Optional<FunctionDTO> findById(Long id) {
        String sql = "SELECT * FROM functions WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                FunctionDTO function = mapResultSetToFunction(rs);
                logger.debug("Найдена функция по ID {}: {}", id, function.getName());
                return Optional.of(function);
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Ошибка при поиске функции по ID: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public void deleteFunctionsByUserId(Long userId) {
        String sql = "DELETE FROM functions WHERE u_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.executeUpdate();
            logger.info("Удалены функции пользователя с ID: {}", userId);
        } catch (SQLException e) {
            logger.error("Ошибка при удалении функций пользователя с ID: {}", userId, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<FunctionDTO> findByUserId(Long userId) {
        String sql = "SELECT * FROM functions WHERE u_id = ? ORDER BY name";
        List<FunctionDTO> functions = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                functions.add(mapResultSetToFunction(rs));
            }
            logger.debug("Найдено {} функций для пользователя с ID {}", functions.size(), userId);
            return functions;
        } catch (SQLException e) {
            logger.error("Ошибка при поиске функций по ID пользователя: {}", userId, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<FunctionDTO> findByName(String name) {
        String sql = "SELECT * FROM functions WHERE name LIKE ? ORDER BY name";
        List<FunctionDTO> functions = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                functions.add(mapResultSetToFunction(rs));
            }
            logger.debug("Найдено {} функций с именем содержащим '{}'", functions.size(), name);
            return functions;
        } catch (SQLException e) {
            logger.error("Ошибка при поиске функций по имени: {}", name, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<FunctionDTO> findAll() {
        String sql = "SELECT * FROM functions ORDER BY u_id, name";
        List<FunctionDTO> functions = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                functions.add(mapResultSetToFunction(rs));
            }
            logger.debug("Найдено {} функций", functions.size());
            return functions;
        } catch (SQLException e) {
            logger.error("Ошибка при получении всех функций", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public boolean updateFunction(FunctionDTO function) {
        String sql = "UPDATE functions SET u_id = ?, name = ?, signature = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, function.getUserId());
            stmt.setString(2, function.getName());
            stmt.setString(3, function.getSignature());
            stmt.setLong(4, function.getId());

            int affectedRows = stmt.executeUpdate();
            boolean updated = affectedRows > 0;

            if (updated) {
                logger.info("Обновлена функция с ID: {}", function.getId());
            }
            return updated;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении функции с ID: {}", function.getId(), e);
            throw new RuntimeException("Database error", e);
        }
    }

    public boolean deleteFunction(Long id) {
        String sql = "DELETE FROM functions WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            boolean deleted = affectedRows > 0;

            if (deleted) {
                logger.info("Удалена функция с ID: {}", id);
            }
            return deleted;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении функции с ID: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public boolean deleteByUserId(Long userId) {
        String sql = "DELETE FROM functions WHERE u_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            int affectedRows = stmt.executeUpdate();
            boolean deleted = affectedRows > 0;

            if (deleted) {
                logger.info("Удалено {} функций пользователя с ID: {}", affectedRows, userId);
            }
            return deleted;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении функций пользователя с ID: {}", userId, e);
            throw new RuntimeException("Database error", e);
        }
    }


    private FunctionDTO mapResultSetToFunction(ResultSet rs) throws SQLException {
        FunctionDTO function = new FunctionDTO();
        function.setId(rs.getLong("id"));
        function.setUserId(rs.getLong("u_id"));
        function.setName(rs.getString("name"));
        function.setSignature(rs.getString("signature"));
        function.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        function.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return function;
    }
}
