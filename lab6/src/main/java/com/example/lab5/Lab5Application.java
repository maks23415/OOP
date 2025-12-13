package com.example.lab5;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

@WebListener
public class Lab5Application implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(Lab5Application.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("=== Lab5 Manual Application Starting ===");
        logger.info("Initializing database connections...");
        logger.info("Loading configuration...");
        logger.info("=== Lab5 Manual Application Started Successfully ===");

        // Здесь можно инициализировать ваши DAO, сервисы и т.д.
        try {
            // Инициализация connection pool и других ресурсов
            initDatabase();
            logger.info("Database initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize application: " + e.getMessage());
            throw new RuntimeException("Application initialization failed", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("=== Lab5 Manual Application Shutting Down ===");

        // Очистка ресурсов
        try {
            cleanupDatabase();
            logger.info("Database connections closed");
        } catch (Exception e) {
            logger.warning("Error during shutdown: " + e.getMessage());
        }

        logger.info("=== Lab5 Manual Application Stopped ===");
    }

    private void initDatabase() {
        // Инициализация базы данных, connection pool и т.д.
        // Это будет вызываться при старте приложения
    }

    private void cleanupDatabase() {
        // Очистка ресурсов базы данных
        // Это будет вызываться при остановке приложения
    }
}