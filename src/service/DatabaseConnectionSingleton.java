package service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionSingleton {
    private static DatabaseConnectionSingleton instance;
    private Connection connection;
    private final String url;
    private final String user;
    private final String password;
    private final String driver;
    
    // Register a shutdown hook to ensure the connection is closed when the JVM exits
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (instance != null) {
                instance.closeConnection();
                System.out.println("Database connection closed by shutdown hook");
            }
        }));
    }

    private DatabaseConnectionSingleton() {
        Properties props = new Properties();
        try {
            InputStream input = new FileInputStream("src/resources/db.properties");
            
            props.load(input);
            input.close();
        } catch (IOException e) {
            System.err.println("Error loading database properties: " + e.getMessage());
            throw new RuntimeException("Cannot load database properties", e);
        }

        this.url = props.getProperty("db.url");
        this.user = props.getProperty("db.user");
        this.password = props.getProperty("db.password");
        this.driver = props.getProperty("db.driver");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver not found: " + e.getMessage());
            throw new RuntimeException("Database driver not found", e);
        }
    }

    public static synchronized DatabaseConnectionSingleton getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionSingleton();
        }
        return instance;
    }

    public synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
                // Set auto-commit to false to enable transaction control
                connection.setAutoCommit(false);
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            throw e;
        }
    }
    
    public void commitTransaction() {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit() && !connection.isClosed()) {
                    connection.commit();
                    System.out.println("Transaction committed successfully");
                }
            } catch (SQLException e) {
                System.err.println("Error committing transaction: " + e.getMessage());
                try {
                    connection.rollback();
                    System.err.println("Transaction rolled back");
                } catch (SQLException re) {
                    System.err.println("Error rolling back transaction: " + re.getMessage());
                }
            }
        }
    }
    
    public void rollbackTransaction() {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit() && !connection.isClosed()) {
                    connection.rollback();
                    System.out.println("Transaction rolled back");
                }
            } catch (SQLException e) {
                System.err.println("Error rolling back transaction: " + e.getMessage());
            }
        }
    }

    public synchronized void closeConnection() {
        if (connection != null) {
            try {
                // Try to commit any pending transactions before closing
                if (!connection.getAutoCommit() && !connection.isClosed()) {
                    try {
                        connection.commit();
                    } catch (SQLException e) {
                        System.err.println("Error committing transaction before close: " + e.getMessage());
                        try {
                            connection.rollback();
                        } catch (SQLException re) {
                            System.err.println("Error rolling back transaction: " + re.getMessage());
                        }
                    }
                }
                
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
    
}