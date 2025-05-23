package view;

import model.User;
import viewModel.ConsoleViewModel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
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
            while (true) {
                boolean institutionExists = consoleViewModel.checkForInstitution();

                System.out.println("\n=== Aula-Sync Console Menu ===");

                if (institutionExists) {
                    System.out.println("1. Sign Up");
                } else {
                    System.out.println("2. Register User");
                }

                System.out.println("3. Exit");
                System.out.println("Select an option: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Error: Please enter a valid number.");
                    scanner.nextLine();
                    continue;
                }

                int option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        if (institutionExists) {
                            showSignUpMenu();
                        } else {
                            System.out.println("Error: Sign Up option is disabled because there is not an institution registered.");
                        }
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
            System.out.println("Error occurred while displaying the menu. Please try again.");
        }
    }


    private void showSignUpMenu() {
        try {
            System.out.println("\nEnter your corporate email: ");
            String email = scanner.nextLine();
            if (email == null || email.trim().isEmpty()) {
                System.out.println("Error: Email cannot be empty.");
                return;
            }

            System.out.println("Enter your password: ");
            String password = scanner.nextLine();
            if (password == null || password.trim().isEmpty()) {
                System.out.println("Error: Password cannot be empty.");
                return;
            }

            boolean result = consoleViewModel.signUp(email, password);
            if (result) {
                System.out.println("Sign-up successful!");
                currentUserId = consoleViewModel.getUserIdByEmail(email);
                showPrincipalMenu();
            } else {
                System.out.println("Sign-up failed. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error occurred during the sign-up process. Please try again.");
        }
    }

    private void showRegisterMenu() {
        try {
            System.out.println("\nSelect the type of user to register:");
            System.out.println("1. Educational Institution");
            System.out.println("Enter your choice: ");

            int userType = -1;
            if (scanner.hasNextInt()) {
                userType = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("Error: Invalid input.");
                scanner.nextLine();
                return;
            }

            System.out.println("Enter your email: ");
            String email = scanner.nextLine();
            if (email == null || email.trim().isEmpty()) {
                System.out.println("Error: Email cannot be empty.");
                return;
            }

            System.out.println("Create a password: ");
            String password = scanner.nextLine();
            if (password == null || password.trim().isEmpty()) {
                System.out.println("Error: Password cannot be empty.");
                return;
            }

            System.out.println("Confirm your password: ");
            String confirmPassword = scanner.nextLine();
            if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
                System.out.println("Error: Confirm password cannot be empty.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                System.out.println("Error: Passwords do not match.");
                return;
            }

            boolean result;

            if (userType == 1) {
                result = consoleViewModel.registerInstitution(email, password);
            } else {
                System.out.println("Error: Invalid user type.");
                return;
            }

            if (result) {
                System.out.println("Registration successful!");
            } else {
                System.out.println("Registration failed. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error occurred during the registration process. Please try again.");
        }
    }

    private void showPrincipalMenu() {
        try {
            currentUser = consoleViewModel.getUserById(currentUserId);

            while (true) {
                boolean isAdmin = currentUser.getUserName().startsWith("a");
                boolean isPrincipal = currentUser.getUserName().startsWith("p");
                boolean isStudent = currentUser.getUserName().startsWith("e");

                System.out.println("\n=== Principal Menu ===");
                System.out.println("--- User: " + currentUser.getUserName() + " ---");
                System.out.println("1. Reserve a Classroom");
                if (!isStudent) {
                    System.out.println("2. Cancel Classroom Reservation");
                } else {
                    System.out.println("2. Cancel Own Classroom Reservation");
                }
                System.out.println("3. View Reserved Classrooms");
                if (!isStudent) {
                    System.out.println("4. Export Metrics");
                }
                System.out.println("5. Log Out");


                if (isAdmin || isPrincipal) {
                    System.out.println("\n--- Administrator Tools: ---");

                    if (isAdmin) {
                        System.out.println("6. Create Teacher User");
                    }

                    System.out.println("7. Create Student User");

                    if (isAdmin) {
                        System.out.println("8. Insert Classrooms");
                    }
                }

                System.out.println("Enter your choice: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Error: Please enter a valid number.");
                    scanner.nextLine();
                    continue;
                }

                int choice = scanner.nextInt();
                scanner.nextLine();  // consume newline


                switch (choice) {
                    case 1:
                        reserveClassroom();
                        break;
                    case 2:
                        if (!isStudent) {
                            cancelClassroomReservation();
                        } else {
                            cancelOwnClassroomReservation();
                        }
                        break;
                    case 3:
                        viewReservedClassrooms();
                        break;
                    case 4:
                        if (!isStudent) {
                            if (consoleViewModel.getClassroomCount() > 0) {
                                System.out.println("Exporting metrics...");
                            } else {
                                System.out.println("You can not export metrics because there are no classrooms registered.");
                                break;
                            }
                            exportMetrics();
                        } else {
                            System.out.println("You are a student, ask your teacher for metrics");
                        }
                        break;
                    case 5:
                        System.out.println("Logging out...");
                        return;
                    case 6:
                        if (isAdmin) {
                            System.out.println("Enter teacher email: ");
                            String teacherEmail = scanner.nextLine();
                            System.out.println("Enter teacher password: ");
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
                            System.out.println("Enter student email: ");
                            String studentEmail = scanner.nextLine();
                            System.out.println("Enter student password: ");
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
                    case 8:
                        if (!isAdmin) {
                            System.out.println("Access denied.");
                            break;
                        }

                        System.out.println("\n1. Insert Classroom Manually");
                        System.out.println("2. Insert Sample Classrooms");
                        System.out.print("Choose an option: ");

                        if (!scanner.hasNextInt()) {
                            System.out.println("Invalid input. Please enter a number.");
                            scanner.nextLine();
                            break;
                        }

                        int classroomOption = scanner.nextInt();
                        scanner.nextLine();

                        switch (classroomOption) {
                            case 1:
                                createClassroomManually();
                                break;
                            case 2:
                                consoleViewModel.insertSampleClassrooms();
                                break;
                            default:
                                System.out.println("Invalid option. Please try again.");
                        }
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while displaying the principal menu. Please try again.");
        }
    }

    private void reserveClassroom() {
        try {
            System.out.println("\nEnter the classroom ID to reserve: ");
            String classRoomId = scanner.nextLine();

            if (!classRoomId.matches("^[1-4][1-3][0-9]{2}$")) {
                System.out.println("Invalid classroom ID format.");
                return;
            }

            System.out.println("Enter reservation date (yyyy-MM-dd): ");
            String dateStr = scanner.nextLine();
            LocalDate date = LocalDate.parse(dateStr);

            if (date.isBefore(LocalDate.now())) {
                System.out.println("Date cannot be in the past.");
                return;
            }

            System.out.println("Enter start time (HH:mm): ");
            LocalTime start = LocalTime.parse(scanner.nextLine());

            System.out.println("Enter end time (HH:mm): ");
            LocalTime end = LocalTime.parse(scanner.nextLine());

            if (end.isBefore(start)) {
                System.out.println("End time cannot be before start time.");
                return;
            }

            boolean success = consoleViewModel.reserveClassroom(currentUserId, classRoomId, dateStr, start.toString(), end.toString());

            if (success) {
                System.out.println("Classroom reserved successfully!");
            } else {
                System.out.println("Failed to reserve the classroom. Ask for support!");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date or time format.");
        } catch (Exception e) {
            System.out.println("Error occurred while reserving the classroom. Please try again.");
        }
    }

    public void cancelClassroomReservation() {
        try {
            System.out.println("\nEnter the reservation ID to cancel: ");
            String reservationId = scanner.nextLine();
            consoleViewModel.cancelClassroomReservation(reservationId);
        } catch (Exception e) {
            System.out.println("Error occurred while canceling the reservation. Please try again.");
        }
    }

    public void cancelOwnClassroomReservation() {
        try {
            System.out.println("\nEnter the reservation ID to cancel: ");
            String reservationId = scanner.nextLine();
            consoleViewModel.cancelOwnClassroomReservation(reservationId, currentUserId);
        } catch (Exception e) {
            System.out.println("Error occurred while canceling the reservation. Please try again.");
        }
    }

    public void viewReservedClassrooms() {
        try {
            List<String> reservedClassrooms = consoleViewModel.getReservedClassrooms();

            if (reservedClassrooms.isEmpty()) {
                System.out.println("No classrooms are currently reserved.");
            } else {
                System.out.println("Reserved Classrooms:" + reservedClassrooms);
            }
        } catch (Exception e) {
            System.out.println("Error occurred while fetching reserved classrooms. Please try again.");
        }
    }

    public void exportMetrics() {
        try {
            System.out.println("\nChoose the metrics to export:");
            System.out.println("1. Most Demanded Classrooms (Daily, Weekly, Monthly)");
            System.out.println("2. Peak Hours");
            System.out.println("3. Most Active Users");
            System.out.println("4. Average Occupancy Time");
            System.out.println("5. Occupancy Percentage");
            System.out.println("6. Raw UserTable Data");
            System.out.println("7. Raw ClassroomTable Data");
            System.out.println("8. Raw ReservationTable Data");
            System.out.println("9. Export All Metrics and Tables");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    consoleViewModel.exportMostDemandedClassrooms();
                    break;
                case "2":
                    consoleViewModel.exportPeakHours();
                    break;
                case "3":
                    consoleViewModel.exportMostActiveUsers();
                    break;
                case "4":
                    consoleViewModel.exportAverageOccupancyTime();
                    break;
                case "5":
                    consoleViewModel.exportOccupancyPercentage();
                    break;
                case "6":
                    consoleViewModel.exportUserMetrics();
                    break;
                case "7":
                    consoleViewModel.exportClassroomMetrics();
                    break;
                case "8":
                    consoleViewModel.exportReservationMetrics();
                    break;
                case "9":
                    consoleViewModel.exportMostDemandedClassrooms();
                    consoleViewModel.exportPeakHours();
                    consoleViewModel.exportMostActiveUsers();
                    consoleViewModel.exportAverageOccupancyTime();
                    consoleViewModel.exportOccupancyPercentage();
                    consoleViewModel.exportUserMetrics();
                    consoleViewModel.exportClassroomMetrics();
                    consoleViewModel.exportReservationMetrics();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    return;
            }

            System.out.println("Metrics exported successfully!");
        } catch (Exception e) {
            System.out.println("Error occurred while exporting metrics: " + e.getMessage());
        }
    }

    public void createClassroomManually() {
        try {
            System.out.println("Enter Classroom ID format: BFP (Building-Floor-Position). Example: 3102 = Building 3, Floor 1, Class 02.: ");
            int classroomId = Integer.parseInt(scanner.nextLine());

            System.out.println("Enter Classroom Capacity: ");
            int capacity = Integer.parseInt(scanner.nextLine());

            boolean success = consoleViewModel.createClassroom(classroomId, capacity);

            if (success) {
                System.out.println("Classroom created successfully with ID: " + classroomId);
            } else {
                System.out.println("A classroom with this ID already exists.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter numeric values.");
        } catch (Exception e) {
            System.out.println("An error occurred while creating the classroom. Please try again.");
        }
    }

}