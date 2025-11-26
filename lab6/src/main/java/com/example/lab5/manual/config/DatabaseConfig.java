package com.example.lab5.manual.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("manual/application.properties")) {
            if (input == null) {
                logger.error("Не найден файл конфигурации manual/application.properties");
                throw new RuntimeException("Configuration file not found");
            }
            properties.load(input);
            logger.info("Конфигурация базы данных загружена");
        } catch (IOException e) {
            logger.error("Ошибка загрузки конфигурации", e);
            throw new RuntimeException("Error loading configuration", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("database.url");
        String username = properties.getProperty("database.username");
        String password = properties.getProperty("database.password");

        logger.debug("Подключение к базе данных: {}", url);
        return DriverManager.getConnection(url, username, password);
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
