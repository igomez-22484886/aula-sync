package viewModel;

import model.User;
import model.daos.UserDAO;

public class ConsoleViewModel {
    UserDAO userDAO = new UserDAO();

    public boolean signUp(String email, String password) {
        return userDAO.checkCredentials(email, password);
    }

    public boolean registerStudent(String email, String password) {
        System.out.println("registerStudent: Checking if institution exists...");
        if (!userDAO.checkInstitutionExists()) {
            System.out.println("registerStudent: Registration blocked - No institution found.");
            return false;
        }

        String id = userDAO.extractUsernameFromEmail(email);
        String userName = "e" + id;
        return userDAO.registerUser(new User(Integer.parseInt(id), userName, email, password));
    }

    public boolean registerTeacher(String email, String password) {
        System.out.println("registerTeacher: Checking if institution exists...");
        if (!userDAO.checkInstitutionExists()) {
            System.out.println("registerTeacher: Registration blocked - No institution found.");
            return false;
        }

        String id = userDAO.extractUsernameFromEmail(email);
        String userName = "p" + id;
        return userDAO.registerUser(new User(Integer.parseInt(id), userName, email, password));
    }

    public boolean registerInstitution(String email, String password) {
        if (userDAO.checkInstitutionExists()) {
            System.out.println("registerInstitution: Error - An institution account already exists. Cannot register another one.");
            return false;
        }
        String id = userDAO.extractUsernameFromEmail(email);
        String userName = "a" + id;
        return userDAO.registerUser(new User(Integer.parseInt(id), userName, email, password));
    }

    public boolean checkForInstitution() {
        return userDAO.checkInstitutionExists();
    }

    public boolean reserveClassroom() {
        try {
            System.out.println("reserveClassroom: Enter the classroom ID to reserve: ");

            // Simulate classroom reservation logic here (database interaction or internal logic)
            System.out.println("reserveClassroom: Classroom reserved successfully!");
            return true;
        } catch (Exception e) {
            System.out.println("reserveClassroom: Error occurred while reserving the classroom. Please try again.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelClassroomReservation() {
        try {
            System.out.println("cancelClassroomReservation: Enter the reservation ID to cancel: ");

            // Simulate canceling the classroom reservation logic here
            System.out.println("cancelClassroomReservation: Reservation canceled successfully!");
            return true;
        } catch (Exception e) {
            System.out.println("cancelClassroomReservation: Error occurred while canceling the reservation. Please try again.");
            e.printStackTrace();
            return false;
        }
    }

    public void viewReservedClassrooms() {
        try {
            // Simulate fetching the list of reserved classrooms
            String reservedClassrooms = "Room 101, Room 102, Room 203"; // Example list of reserved classrooms
            if (reservedClassrooms.isEmpty()) {
                System.out.println("viewReservedClassrooms: No classrooms are currently reserved.");
            } else {
                System.out.println("viewReservedClassrooms: Reserved Classrooms:\n" + reservedClassrooms);
            }
        } catch (Exception e) {
            System.out.println("viewReservedClassrooms: Error occurred while fetching reserved classrooms. Please try again.");
            e.printStackTrace();
        }
    }

    public boolean exportMetrics() {
        try {
            // Simulate exporting system metrics
            System.out.println("exportMetrics: Exporting metrics...");
            // Example logic for exporting metrics (e.g., to a file, database, etc.)
            System.out.println("exportMetrics: Metrics exported successfully!");
            return true;
        } catch (Exception e) {
            System.out.println("exportMetrics: Error occurred while exporting metrics. Please try again.");
            e.printStackTrace();
            return false;
        }
    }
}