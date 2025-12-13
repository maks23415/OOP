package com.example.lab5.manual.dao;

import com.example.lab5.manual.config.DatabaseConfig;
import com.example.lab5.manual.dto.PointDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PointDAO {
    private static final Logger logger = LoggerFactory.getLogger(PointDAO.class);

    public Long createPoint(PointDTO point) {
        String sql = "INSERT INTO points (f_id, x_value, y_value) VALUES (?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, point.getFunctionId());
            stmt.setDouble(2, point.getXValue());
            stmt.setDouble(3, point.getYValue());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Long id = rs.getLong(1);
                logger.debug("Создана точка с ID: {}, f_id: {}, x: {}", id, point.getFunctionId(), point.getXValue());
                return id;
            } else {
                throw new SQLException("Создание точки не удалось, ID не получен");
            }
        } catch (SQLException e) {
            logger.error("Ошибка при создании точки для функции {}: x={}, y={}",
                    point.getFunctionId(), point.getXValue(), point.getYValue(), e);
            throw new RuntimeException("Database error", e);
        }
    }

    public int createPoints(List<PointDTO> points) {
        String sql = "INSERT INTO points (f_id, x_value, y_value) VALUES (?, ?, ?)";
        int createdCount = 0;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (PointDTO point : points) {
                stmt.setLong(1, point.getFunctionId());
                stmt.setDouble(2, point.getXValue());
                stmt.setDouble(3, point.getYValue());
                stmt.addBatch();
            }

            int[] results = stmt.executeBatch();
            createdCount = results.length;
            logger.info("Создано {} точек", createdCount);

        } catch (SQLException e) {
            logger.error("Ошибка при создании пакета точек", e);
            throw new RuntimeException("Database error", e);
        }
        return createdCount;
    }

    public Optional<PointDTO> findById(Long id) {
        String sql = "SELECT id, f_id, x_value, y_value FROM points WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                PointDTO point = mapResultSetToPoint(rs);
                return Optional.of(point);
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Ошибка при поиске точки по ID: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<PointDTO> findByFunctionId(Long functionId) {
        String sql = "SELECT id, f_id, x_value, y_value FROM points WHERE f_id = ? ORDER BY x_value";
        List<PointDTO> points = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, functionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                points.add(mapResultSetToPoint(rs));
            }
            logger.debug("Найдено {} точек для функции с ID {}", points.size(), functionId);
            return points;
        } catch (SQLException e) {
            logger.error("Ошибка при поиске точек по ID функции: {}", functionId, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<PointDTO> findByXRange(Long functionId, Double minX, Double maxX) {
        String sql = "SELECT id, f_id, x_value, y_value FROM points WHERE f_id = ? AND x_value BETWEEN ? AND ? ORDER BY x_value";
        List<PointDTO> points = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, functionId);
            stmt.setDouble(2, minX);
            stmt.setDouble(3, maxX);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                points.add(mapResultSetToPoint(rs));
            }
            logger.debug("Найдено {} точек в диапазоне x=[{}, {}] для функции {}",
                    points.size(), minX, maxX, functionId);
            return points;
        } catch (SQLException e) {
            logger.error("Ошибка при поиске точек по диапазону X: {} - {}", minX, maxX, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<PointDTO> findByYRange(Long functionId, Double minY, Double maxY) {
        String sql = "SELECT id, f_id, x_value, y_value FROM points WHERE f_id = ? AND y_value BETWEEN ? AND ? ORDER BY y_value";
        List<PointDTO> points = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, functionId);
            stmt.setDouble(2, minY);
            stmt.setDouble(3, maxY);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                points.add(mapResultSetToPoint(rs));
            }
            return points;
        } catch (SQLException e) {
            logger.error("Ошибка при поиске точек по диапазону Y: {} - {}", minY, maxY, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<PointDTO> findAll() {
        String sql = "SELECT id, f_id, x_value, y_value FROM points ORDER BY f_id, x_value";
        List<PointDTO> points = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                points.add(mapResultSetToPoint(rs));
            }
            logger.debug("Найдено {} точек", points.size());
            return points;
        } catch (SQLException e) {
            logger.error("Ошибка при получении всех точек", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public boolean updatePoint(PointDTO point) {
        String sql = "UPDATE points SET f_id = ?, x_value = ?, y_value = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, point.getFunctionId());
            stmt.setDouble(2, point.getXValue());
            stmt.setDouble(3, point.getYValue());
            stmt.setLong(4, point.getId());

            int affectedRows = stmt.executeUpdate();
            boolean updated = affectedRows > 0;

            if (updated) {
                logger.info("Обновлена точка с ID: {}", point.getId());
            }
            return updated;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении точки с ID: {}", point.getId(), e);
            throw new RuntimeException("Database error", e);
        }
    }

    public boolean deletePoint(Long id) {
        String sql = "DELETE FROM points WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            boolean deleted = affectedRows > 0;

            if (deleted) {
                logger.info("Удалена точка с ID: {}", id);
            }
            return deleted;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении точки с ID: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public int deleteByFunctionId(Long functionId) {
        String sql = "DELETE FROM points WHERE f_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, functionId);
            int affectedRows = stmt.executeUpdate();

            logger.info("Удалено {} точек функции с ID: {}", affectedRows, functionId);
            return affectedRows;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении точек функции с ID: {}", functionId, e);
            throw new RuntimeException("Database error", e);
        }
    }

    private PointDTO mapResultSetToPoint(ResultSet rs) throws SQLException {
        PointDTO point = new PointDTO();
        point.setId(rs.getLong("id"));
        point.setFunctionId(rs.getLong("f_id"));
        point.setXValue(rs.getDouble("x_value"));
        point.setYValue(rs.getDouble("y_value"));
        return point;
    }
}
