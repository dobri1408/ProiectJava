package Service;

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
    private final boolean isH2;
    
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
            // Încercăm să încărcăm din calea directă
            InputStream input = getClass().getResourceAsStream("/resources/db.properties");
            
            // Dacă nu găsim, încercăm cu calea completă
            if (input == null) {
                input = getClass().getClassLoader().getResourceAsStream("resources/db.properties");
            }
            
            // Dacă tot nu găsim, încercăm cu calea relativă
            if (input == null) {
                input = new FileInputStream("src/resources/db.properties");
            }
            
            if (input == null) {
                System.out.println("Sorry, unable to find db.properties");
                throw new IOException("Cannot find db.properties");
            }
            
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
        this.isH2 = this.driver.contains("h2");

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
    
    public boolean isH2Database() {
        return isH2;
    }
}