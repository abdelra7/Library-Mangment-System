package com.library.app.db;

import com.library.app.util.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {

    private static final Logger logger = new Logger(DatabaseConnection.class.getName());

    // Static instance of the DatabaseConnection singleton
    private static DatabaseConnection instance;

    // PostgreSQL JDBC connection properties
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/mydata2";
    private static final String DB_USER = "postgres"; // <-- غيّر حسب اسم المستخدم عندك
    private static final String DB_PASSWORD = "123"; // <-- غيّر حسب الباسورد

    // Connection object
    private Connection connection;


    private DatabaseConnection() {
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Establish connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logger.info("PostgreSQL database connection established successfully");

        } catch (SQLException e) {
            logger.error("SQL Error in DatabaseConnection constructor", e);
            e.printStackTrace();  // Print full stack trace for debugging
            throw new RuntimeException("Failed to connect to the database: " + e.getMessage());

        } catch (ClassNotFoundException e) {
            logger.error("PostgreSQL JDBC Driver not found", e);
            throw new RuntimeException("PostgreSQL JDBC Driver not found: " + e.getMessage());
        }
    }


    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        } else {
            try {
                if (instance.connection == null || instance.connection.isClosed()) {
                    logger.info("Reconnecting to database...");
                    instance = new DatabaseConnection();
                }
            } catch (SQLException e) {
                logger.error("Error checking database connection", e);
                instance = new DatabaseConnection();
            }
        }
        return instance;
    }


    public Connection getConnection() {
        return connection;
    }


    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed");
            }
        } catch (SQLException e) {
            logger.error("Error closing database connection", e);
        }
    }


    public static void resetConnection() {
        if (instance != null) {
            instance.closeConnection();
            instance = null;
        }
    }
}
