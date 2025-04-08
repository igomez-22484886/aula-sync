package model.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLServerConnection {
    public static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=master;encrypt=false";
    public static final String USER = "SA";
    public static final String PASSWORD = "c_hup@meLa1234";
    private static final Logger LOGGER = Logger.getLogger(SQLServerConnection.class.getName());

    public static Connection getConnection() {
        try {
            LOGGER.info("SQLServerConnection: Attempting to connect to the database...");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            LOGGER.info("SQLServerConnection: Connection successful!");
            return connection;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQLServerConnection: Connection failed!", e);
            return null;
        }
    }
}
