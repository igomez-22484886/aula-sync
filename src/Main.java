import model.SQLServerConnection;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        System.out.println("Main: Starting application...");

        try (Connection conn = SQLServerConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Main: Database connection established.");
            } else {
                System.err.println("Main: Failed to connect to database.");
            }
        } catch (Exception e) {
            System.err.println("Main: Unexpected error - " + e.getMessage());
        }

        System.out.println("Main: Application finished.");
    }
}