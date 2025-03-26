package view;

import java.util.Scanner;

public class ConsoleView {

    private final Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== AulaSync Console Menu ===");
            System.out.println("1. View user by ID");
            System.out.println("2. View all classrooms");
            System.out.println("3. View reservations by date");
            System.out.println("4. Exit");
            System.out.print("Select an option: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Error: Please enter a valid number.");
                scanner.next();
                continue;
            }

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    System.out.println("Feature not implemented yet.");
                    break;
                case 2:
                    System.out.println("Feature not implemented yet.");
                    break;
                case 3:
                    System.out.println("Feature not implemented yet.");
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }
}
