package model.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLServerConnection {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=master;encrypt=false";
    private static final String USER = "SA";
    private static final String PASSWORD = "c_hup@meLa1234";
    private static final Logger LOGGER = Logger.getLogger(SQLServerConnection.class.getName());

    public static Connection getConnection() {
        int attempts = 5;
        int waitTime = 5000; // 5s

        for (int i = 0; i < attempts; i++) {
            try {
                LOGGER.info("SQLServerConnection: Attempting to connect to the database... (Attempt " + (i+1) + ")");
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                LOGGER.info("SQLServerConnection: Connection successful!");
                return connection;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "SQLServerConnection: Connection failed, retrying...", e);
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    LOGGER.severe("SQLServerConnection: Thread interrupted!");
                    return null;
                }
            }
        }

        LOGGER.severe("SQLServerConnection: Could not establish a connection after multiple attempts.");
        return null;
    }

}
