package com.quizzy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static String url;
    private static String username;
    private static String password;
    private static String configSource;

    static {
        loadCredentials();
    }

    private DatabaseConnection() {
    }

    public static synchronized void loadCredentials() {
        File externalConfigFile = resolveExternalConfigFile("config.properties");
        if (externalConfigFile != null && externalConfigFile.exists() && externalConfigFile.canRead()) {
            if (loadFromFile(externalConfigFile)) {
                configSource = "External configuration file (" + externalConfigFile.getAbsolutePath() + ")";
                return;
            }
        }

        if (loadFromClasspath("/com/quizzy/config.properties")
                || loadFromClasspath("/com/quizzy/config-default.properties")
                || loadFromClasspath("/config.properties")) {
            configSource = "Classpath resource fallback";
        }
    }

    private static File resolveExternalConfigFile(String filename) {
        try {
            URI locationUri = DatabaseConnection.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            File codeSourceFile = new File(locationUri);
            File parentDir = codeSourceFile.isFile() ? codeSourceFile.getParentFile() : codeSourceFile;
            File adjacentFile = new File(parentDir, filename);
            if (adjacentFile.exists()) {
                return adjacentFile;
            }
        } catch (Exception ignored) {
        }

        File cwdFile = new File(filename);
        if (cwdFile.exists()) {
            return cwdFile;
        }

        return null;
    }

    private static boolean loadFromFile(File file) {
        try (InputStream input = new FileInputStream(file)) {
            Properties props = new Properties();
            props.load(input);

            String propUrl = props.getProperty("db.url", props.getProperty("url"));
            String propUser = props.getProperty("db.username", props.getProperty("username"));
            String propPass = props.getProperty("db.password", props.getProperty("password"));

            if (propUrl != null && !propUrl.isBlank()
                    && propUser != null && !propUser.isBlank()
                    && propPass != null) {
                url = propUrl.trim();
                username = propUser.trim();
                password = propPass;
                return true;
            }
        } catch (Exception e) {
            System.err.println("Warning: Unable to read config file from " + file.getAbsolutePath() + ": " + e.getMessage());
        }
        return false;
    }

    private static boolean loadFromClasspath(String resourcePath) {
        try (InputStream input = DatabaseConnection.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                return false;
            }
            Properties props = new Properties();
            props.load(input);

            String propUrl = props.getProperty("db.url", props.getProperty("url"));
            String propUser = props.getProperty("db.username", props.getProperty("username"));
            String propPass = props.getProperty("db.password", props.getProperty("password"));

            if (propUrl != null && !propUrl.isBlank()
                    && propUser != null && !propUser.isBlank()
                    && propPass != null && !propPass.isBlank()) {
                url = propUrl.trim();
                username = propUser.trim();
                password = propPass;
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public static Connection getConnection() throws SQLException {
        if (url == null || url.isBlank() || username == null || username.isBlank() || password == null) {
            throw new SQLException("""
                [DatabaseConnection Error] Missing or incomplete database configuration!
                Please ensure 'config.properties' exists in the application directory or working directory:
                  - db.url=jdbc:sqlserver://localhost:1433;databaseName=QUIZZYDB;encrypt=true;trustServerCertificate=true
                  - db.username=your_username
                  - db.password=your_password
                """);
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

    public static String getConfigSource() {
        return configSource;
    }

}
