package com.quizzy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static String url;
    private static String username;
    private static String password;

    static {
        loadCredentials();
    }

    private static void loadCredentials() {
        Properties props = new Properties();
        File configFile = new File("config.properties");

        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                props.load(input);
                url = props.getProperty("db.url");
                username = props.getProperty("db.username");
                password = props.getProperty("db.password");
            } catch (Exception e) {
                System.err.println("Warning: Unable to read config.properties: " + e.getMessage());
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        if (url == null || username == null || password == null) {
            throw new SQLException("Database configuration missing. Please ensure config.properties exists with db.url, db.username, and db.password.");
        }
        return DriverManager.getConnection(url, username, password);
    }

    public static String getUrl() {
        return url;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

}
