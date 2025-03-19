package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://AULASYNC:1433;databaseName=AulaSync;encrypt=true;trustServerCertificate=true";
    private static final String USER = "SA";
    private static final String PASSWORD = "c_hup@meLa1234";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("DatabaseConnection: Connection established successfully.");
            return conn;
        } catch (SQLException e) {
            System.err.println("DatabaseConnection: Error connecting to the database - " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        getConnection();
    }
}
