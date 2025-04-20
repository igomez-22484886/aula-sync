package view;

import viewModel.ConsoleViewModel;

import java.util.Scanner;

public class ConsoleView {

    private final Scanner scanner;
    private final ConsoleViewModel consoleViewModel = new ConsoleViewModel();

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    public void showInitialMenu() {
        try {
            if (!consoleViewModel.checkForInstitution()) {
                System.out.println("Warning: No educational institution account found. Please create one to use the application properly.");
            }

            while (true) {
                System.out.println("\n=== AulaSync Console Menu ===");
                System.out.println("1. Sign Up");
                System.out.println("2. Register User");
                System.out.println("3. Exit");
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
                        showSignUpMenu();
                        break;
                    case 2:
                        showRegisterMenu();
                        break;
                    case 3:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid option, please try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("showInitialMenu: Error occurred while displaying the menu. Please try again.");
            e.printStackTrace();
        }
    }

    private void showSignUpMenu() {
        try {
            System.out.println("showSignUpMenu: Displaying sign-up menu...");

            System.out.print("showSignUpMenu: Enter your corporate email: ");
            String email = scanner.nextLine();
            if (email == null || email.trim().isEmpty()) {
                System.out.println("showSignUpMenu: Error - Email cannot be empty.");
                return;
            }

            System.out.print("showSignUpMenu: Enter your password: ");
            String password = scanner.nextLine();
            if (password == null || password.trim().isEmpty()) {
                System.out.println("showSignUpMenu: Error - Password cannot be empty.");
                return;
            }

            System.out.print("showSignUpMenu: Confirm your password: ");
            String confirmPassword = scanner.nextLine();
            if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
                System.out.println("showSignUpMenu: Error - Confirm password cannot be empty.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                System.out.println("showSignUpMenu: Error - Passwords do not match.");
                return;
            }

            boolean result = consoleViewModel.signUp(email, password);
            if (result) {
                System.out.println("showSignUpMenu: Sign-up successful!");
                showPrincipalMenu();
            } else {
                System.out.println("showSignUpMenu: Sign-up failed. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("showSignUpMenu: Error occurred during the sign-up process. Please try again.");
            e.printStackTrace();
        }
    }

    private void showRegisterMenu() {
        try {
            System.out.println("showRegisterMenu: Displaying user registration menu...");

            System.out.println("showRegisterMenu: Select the type of user to register:");
            System.out.println("1. Student");
            System.out.println("2. Teacher");
            System.out.println("3. Educational Institution");
            System.out.print("showRegisterMenu: Enter your choice: ");

            int userType = -1;
            if (scanner.hasNextInt()) {
                userType = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("showRegisterMenu: Error - Invalid input.");
                scanner.nextLine();
                return;
            }

            if ((userType == 1 || userType == 2) && !consoleViewModel.checkForInstitution()) {
                System.out.println("showRegisterMenu: Registration blocked - No institution exists. Please register an institution first.");
                return;
            }

            // Block institution registration if one already exists
            if (userType == 3 && consoleViewModel.checkForInstitution()) {
                System.out.println("showRegisterMenu: Error - An institution account already exists. You cannot register another one.");
                return;
            }

            System.out.print("showRegisterMenu: Enter your email: ");
            String email = scanner.nextLine();
            if (email == null || email.trim().isEmpty()) {
                System.out.println("showRegisterMenu: Error - Email cannot be empty.");
                return;
            }

            System.out.print("showRegisterMenu: Create a password: ");
            String password = scanner.nextLine();
            if (password == null || password.trim().isEmpty()) {
                System.out.println("showRegisterMenu: Error - Password cannot be empty.");
                return;
            }

            System.out.print("showRegisterMenu: Confirm your password: ");
            String confirmPassword = scanner.nextLine();
            if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
                System.out.println("showRegisterMenu: Error - Confirm password cannot be empty.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                System.out.println("showRegisterMenu: Error - Passwords do not match.");
                return;
            }

            boolean result = false;

            switch (userType) {
                case 1:
                    result = consoleViewModel.registerStudent(email, password);
                    break;
                case 2:
                    result = consoleViewModel.registerTeacher(email, password);
                    break;
                case 3:
                    result = consoleViewModel.registerInstitution(email, password);
                    break;
                default:
                    System.out.println("showRegisterMenu: Error - Invalid user type.");
                    return;
            }

            if (result) {
                System.out.println("showRegisterMenu: Registration successful!");
            } else {
                System.out.println("showRegisterMenu: Registration failed. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("showRegisterMenu: Error occurred during the registration process. Please try again.");
            e.printStackTrace();
        }
    }


    private void showPrincipalMenu() {
        try {
            while (true) {
                System.out.println("\n=== Principal Menu ===");
                System.out.println("1. Reserve a Classroom");
                System.out.println("2. Cancel Classroom Reservation");
                System.out.println("3. View Reserved Classrooms");
                System.out.println("4. Export Metrics");
                System.out.println("5. Log Out");
                System.out.print("showPrincipalMenu: Enter your choice: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("showPrincipalMenu: Error - Please enter a valid number.");
                    scanner.next();
                    continue;
                }

                int choice = scanner.nextInt();
                scanner.nextLine();  // consume newline

                switch (choice) {
                    case 1:
                        consoleViewModel.reserveClassroom();
                        break;
                    case 2:
                        consoleViewModel.cancelClassroomReservation();
                        break;
                    case 3:
                        consoleViewModel.viewReservedClassrooms();
                        break;
                    case 4:
                        consoleViewModel.exportMetrics();
                        break;
                    case 5:
                        System.out.println("showPrincipalMenu: Logging out...");
                        return;
                    default:
                        System.out.println("showPrincipalMenu: Invalid option. Please try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("showPrincipalMenu: Error occurred while displaying the principal menu. Please try again.");
            e.printStackTrace();
        }
    }
}
