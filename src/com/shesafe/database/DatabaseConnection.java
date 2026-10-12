package com.shesafe.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DatabaseConnection
 * Manages JDBC connections to MySQL database (shesafe_db).
 * Reads configuration from external 'db.properties' to avoid hardcoded credentials.
 */
public class DatabaseConnection {

    private static String dbUrl = "jdbc:mysql://localhost:3306/shesafe_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String dbUser = "root";
    private static String dbPassword = "";
    private static String dbDriver = "com.mysql.cj.jdbc.Driver";

    private static boolean isConfigLoaded = false;

    // Static block to initialize driver and load properties
    static {
        loadConfiguration();
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            // Check for legacy driver fallback
            try {
                Class.forName("com.mysql.jdbc.Driver");
                dbDriver = "com.mysql.jdbc.Driver";
            } catch (ClassNotFoundException ex) {
                System.err.println("[DatabaseConnection] MySQL JDBC Driver not found on classpath.");
                System.err.println("Please add mysql-connector-j-8.x.x.jar to your project libraries.");
            }
        }
    }

    /**
     * Loads database configuration from external db.properties file.
     * Searches classpath first, then project root, then src folder.
     */
    public static synchronized void loadConfiguration() {
        if (isConfigLoaded) {
            return;
        }

        Properties props = new Properties();
        InputStream input = null;

        try {
            // 1. Try loading from Classpath (/db.properties)
            input = DatabaseConnection.class.getResourceAsStream("/db.properties");

            // 2. Try loading from working directory file (db.properties)
            if (input == null) {
                File localFile = new File("db.properties");
                if (localFile.exists()) {
                    input = new FileInputStream(localFile);
                }
            }

            // 3. Try loading from src directory (src/db.properties)
            if (input == null) {
                File srcFile = new File("src/db.properties");
                if (srcFile.exists()) {
                    input = new FileInputStream(srcFile);
                }
            }

            if (input != null) {
                props.load(input);
                if (props.getProperty("db.url") != null) dbUrl = props.getProperty("db.url").trim();
                if (props.getProperty("db.user") != null) dbUser = props.getProperty("db.user").trim();
                if (props.getProperty("db.password") != null) dbPassword = props.getProperty("db.password").trim();
                if (props.getProperty("db.driver") != null) dbDriver = props.getProperty("db.driver").trim();
                isConfigLoaded = true;
            } else {
                System.out.println("[DatabaseConnection] db.properties not found. Using default local configuration.");
            }
        } catch (Exception e) {
            System.err.println("[DatabaseConnection] Failed to read db.properties: " + e.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Establishes and returns a fresh JDBC connection to the MySQL database.
     *
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        loadConfiguration();
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    /**
     * Quick utility to test whether MySQL connection is currently operational.
     *
     * @return true if database connection succeeded, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Diagnostic message explaining connection status (useful for user dialogs).
     */
    public static String getConnectionErrorMessage(SQLException e) {
        String msg = e.getMessage().toLowerCase();
        if (msg.contains("communications link failure") || msg.contains("connection refused")) {
            return "Cannot reach MySQL server on localhost:3306.\nPlease make sure MySQL Server (or XAMPP/WAMP) is running.";
        } else if (msg.contains("access denied")) {
            return "MySQL Access Denied: Please verify your username and password in 'db.properties'.";
        } else if (msg.contains("unknown database") || msg.contains("shesafe_db")) {
            return "Database 'shesafe_db' was not found.\nPlease execute 'database_schema.sql' in MySQL Workbench / phpMyAdmin.";
        }
        return "Database error: " + e.getMessage();
    }
}
