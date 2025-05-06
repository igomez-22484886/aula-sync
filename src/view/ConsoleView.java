package view;

import model.User;
import viewModel.ConsoleViewModel;

import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Scanner;

public class ConsoleView {

    private final Scanner scanner;
    private final ConsoleViewModel consoleViewModel = new ConsoleViewModel();
    private String currentUserId = null;
    private User currentUser = null;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    public void showInitialMenu() {
        try {
            boolean institutionExists = consoleViewModel.checkForInstitution();
            if (!institutionExists) {
                System.out.println("Warning: No educational institution account found. Please create one to use the application properly.");
            }

            while (true) {
                System.out.println("\n=== AulaSync Console Menu ===");
                System.out.println("1. Sign Up");
                if (!institutionExists) {
                    System.out.println("2. Register User");
                }
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
                        if (!institutionExists) {
                            showRegisterMenu();
                        } else {
                            System.out.println("Error: Register option is disabled because an institution already exists.");
                        }
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
            // e.printStackTrace();
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
                currentUserId = consoleViewModel.getUserIdByEmail(email);
                showPrincipalMenu();
            } else {
                System.out.println("showSignUpMenu: Sign-up failed. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("showSignUpMenu: Error occurred during the sign-up process. Please try again.");
            // e.printStackTrace();
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
            // e.printStackTrace();
        }
    }


    private void showPrincipalMenu() {
        try {
            currentUser = consoleViewModel.getUserById(currentUserId);

            while (true) {
                System.out.println("\n=== Principal Menu ===");
                System.out.println("1. Reserve a Classroom");
                System.out.println("2. Cancel Classroom Reservation");
                System.out.println("3. View Reserved Classrooms");
                System.out.println("4. Export Metrics");
                System.out.println("5. Log Out");

                boolean isAdmin = currentUser.getUserName().startsWith("a");
                boolean isPrincipal = currentUser.getUserName().startsWith("p");

                if (isAdmin || isPrincipal) {
                    System.out.println("\n--- Administrator Tools: ---");

                    if (isAdmin) {
                        System.out.println("6. Create Teacher User");
                    }

                    System.out.println("7. Create Student User");

                    if (isAdmin) {
                        System.out.println("8. Insert Sample Classrooms");
                    }
                }

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
                        reserveClassroom();
                        break;
                    case 2:
                        cancelClassroomReservation();
                        break;
                    case 3:
                        viewReservedClassrooms();
                        break;
                    case 4:
                        exportMetrics();
                        break;
                    case 5:
                        System.out.println("showPrincipalMenu: Logging out...");
                        return;
                    case 8:
                        if (isAdmin) {
                            consoleViewModel.insertSampleClassrooms();
                        } else {
                            System.out.println("Access denied.");
                        }
                        break;
                    case 6:
                        if (isAdmin) {
                            System.out.print("Enter teacher email: ");
                            String teacherEmail = scanner.nextLine();
                            System.out.print("Enter teacher password: ");
                            String teacherPassword = scanner.nextLine();
                            if (consoleViewModel.registerTeacher(teacherEmail, teacherPassword)) {
                                System.out.println("Teacher registered successfully.");
                            } else {
                                System.out.println("Failed to register teacher.");
                            }
                        } else {
                            System.out.println("Access denied.");
                        }
                        break;
                    case 7:
                        if (isPrincipal || isAdmin) {
                            System.out.print("Enter student email: ");
                            String studentEmail = scanner.nextLine();
                            System.out.print("Enter student password: ");
                            String studentPassword = scanner.nextLine();
                            if (consoleViewModel.registerStudent(studentEmail, studentPassword)) {
                                System.out.println("Student registered successfully.");
                            } else {
                                System.out.println("Failed to register student.");
                            }
                        } else {
                            System.out.println("Access denied.");
                        }
                        break;
                    default:
                        System.out.println("showPrincipalMenu: Invalid option. Please try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("showPrincipalMenu: Error occurred while displaying the principal menu. Please try again.");
            // e.printStackTrace();
        }
    }


    private boolean reserveClassroom() {
        try {
            System.out.print("reserveClassroom: Enter the classroom ID to reserve: ");
            String classRoomId = scanner.nextLine();

            System.out.print("reserveClassroom: Enter reservation date (yyyy-MM-dd): ");
            String date = scanner.nextLine();

            System.out.print("reserveClassroom: Enter start time (HH:mm): ");
            String startTime = scanner.nextLine();

            System.out.print("reserveClassroom: Enter end time (HH:mm): ");
            String endTime = scanner.nextLine();

            boolean success = consoleViewModel.reserveClassroom(currentUserId, classRoomId, date, startTime, endTime);

            if (success) {
                System.out.println("reserveClassroom: Classroom reserved successfully!");
            } else {
                System.out.println("reserveClassroom: Failed to reserve the classroom.");
            }

            return success;
        } catch (Exception e) {
            System.out.println("reserveClassroom: Error occurred while reserving the classroom. Please try again.");
            // e.printStackTrace();
            return false;
        }
    }


    public boolean cancelClassroomReservation() {
        try {
            System.out.println("cancelClassroomReservation: Enter the reservation ID to cancel: ");

            String reservationId = scanner.next();
            consoleViewModel.cancelClassroomReservation(reservationId);

            return true;
        } catch (Exception e) {
            System.out.println("cancelClassroomReservation: Error occurred while canceling the reservation. Please try again.");
            // e.printStackTrace();
            return false;
        }
    }

    public void viewReservedClassrooms() {
        try {
            List<String> reservedClassrooms = consoleViewModel.getReservedClassrooms();

            if (reservedClassrooms.isEmpty()) {
                System.out.println("viewReservedClassrooms: No classrooms are currently reserved.");
            } else {
                System.out.println("viewReservedClassrooms: Reserved Classrooms:\n" + reservedClassrooms);
            }
        } catch (Exception e) {
            System.out.println("viewReservedClassrooms: Error occurred while fetching reserved classrooms. Please try again.");
            // e.printStackTrace();
        }
    }

    public boolean exportMetrics() {
        try {
            System.out.println("exportMetrics: Choose the metrics to export:");
            System.out.println("1. User metrics");
            System.out.println("2. Classroom metrics");
            System.out.println("3. Reservation metrics");
            System.out.println("4. Export all");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    consoleViewModel.exportUserMetrics();
                    break;
                case "2":
                    consoleViewModel.exportClassroomMetrics();
                    break;
                case "3":
                    consoleViewModel.exportReservationMetrics();
                    break;
                case "4":
                    consoleViewModel.exportUserMetrics();
                    consoleViewModel.exportClassroomMetrics();
                    consoleViewModel.exportReservationMetrics();
                    break;
                default:
                    System.out.println("exportMetrics: Invalid option. Please try again.");
                    return false;
            }

            System.out.println("exportMetrics: Metrics exported successfully!");
            return true;

        } catch (Exception e) {
            System.out.println("exportMetrics: Error occurred while exporting metrics. Please try again.");
            // e.printStackTrace();
            return false;
        }
    }
}
