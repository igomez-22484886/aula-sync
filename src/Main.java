import model.repository.SQLServerConnection;
import view.ConsoleView;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        System.out.println("Main: Starting application...");

        try (Connection conn = SQLServerConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Main: Database connection established.");

                // Iniciar Vista de la Consola
                ConsoleView consoleView = new ConsoleView();
                consoleView.showInitialMenu();
            } else {
                System.err.println("Main: Failed to connect to database.");
            }
        } catch (Exception e) {
            System.err.println("Main: Unexpected error - " + e.getMessage());
        }

        System.out.println("Main: Application finished.");
    }
}