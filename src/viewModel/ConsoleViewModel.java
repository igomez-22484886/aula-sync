package viewModel;

import model.Classroom;
import model.Classroom.ClassroomStatus;
import model.User;
import model.daos.ClassroomDAO;
import model.daos.ReservationDAO;
import model.daos.UserDAO;
import model.functions.ExportMetric;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class ConsoleViewModel {
    UserDAO userDAO = new UserDAO();
    ReservationDAO reservationDAO = new ReservationDAO();
    ClassroomDAO classroomDAO = new ClassroomDAO();

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

    public boolean reserveClassroom(String userIdStr, String classRoomIdStr, String dateStr, String startStr, String endStr) {
        try {
            int userId = Integer.parseInt(userIdStr);
            int classroomId = Integer.parseInt(classRoomIdStr);
            Date reservationDate = Date.valueOf(dateStr);
            Time startTime = Time.valueOf(startStr + ":00");
            Time endTime = Time.valueOf(endStr + ":00");

            return reservationDAO.createReservation(userId, classroomId, reservationDate, startTime, endTime);

        } catch (Exception e) {
            System.out.println("reserveClassroom: Error occurred while reserving classroom. Please try again.");
            // e.printStackTrace();
            return false;
        }
    }

    public void cancelClassroomReservation(String reservationId) {
        try {
            int id = Integer.parseInt(reservationId);
            boolean success = reservationDAO.cancelReservation(id);

            if (success) {
                System.out.println("cancelClassroomReservation: Reservation canceled successfully.");
            } else {
                System.out.println("cancelClassroomReservation: Failed to cancel reservation.");
            }

        } catch (Exception e) {
            System.out.println("cancelClassroomReservation: Error occurred while cancelling reservation.");
            // e.printStackTrace();
        }
    }

    public List<String> getReservedClassrooms() {
        try {
            return reservationDAO.getAllReservations();
        } catch (Exception e) {
            System.out.println("getReservedClassrooms: Failed to get reserved classrooms.");
            // e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void exportUserMetrics() {
        try {
            ExportMetric exportMetric = new ExportMetric();
            exportMetric.exportTableToCSV("UserTable");
            System.out.println("exportUserMetrics: User metrics exported.");
        } catch (Exception e) {
            System.out.println("exportUserMetrics: Failed to export user metrics.");
            // e.printStackTrace();
        }
    }

    public void exportClassroomMetrics() {
        try {
            ExportMetric exportMetric = new ExportMetric();
            exportMetric.exportTableToCSV("ClassroomTable");
            System.out.println("exportClassroomMetrics: Classroom metrics exported.");
        } catch (Exception e) {
            System.out.println("exportClassroomMetrics: Failed to export classroom metrics.");
            // e.printStackTrace();
        }
    }

    public void exportReservationMetrics() {
        try {
            ExportMetric exportMetric = new ExportMetric();
            exportMetric.exportTableToCSV("ReservationTable");
            System.out.println("exportReservationMetrics: Reservation metrics exported.");
        } catch (Exception e) {
            System.out.println("exportReservationMetrics: Failed to export reservation metrics.");
            // e.printStackTrace();
        }
    }

    public String getUserIdByEmail(String email) {
        try {
            return userDAO.getUserIdByEmail(email);
        } catch (Exception e) {
            System.out.println("getUserIdByEmail: Error occurred while fetching user ID.");
            // e.printStackTrace();
            return null;
        }
    }

    public User getUserById(String id) {
        try {
            return userDAO.getUserById(id);
        } catch (Exception e) {
            System.out.println("getUserIdByEmail: Error occurred while fetching user.");
            // e.printStackTrace();
            return null;
        }
    }

    public void insertSampleClassrooms() {
        for (int building = 1; building <= 4; building++) {
            for (int floor = 1; floor <= 3; floor++) {
                for (int classNumber = 1; classNumber <= 5; classNumber++) {
                    String id = String.format("%d%d%02d", building, floor, classNumber);

                    int capacity = 30; // Capacidad fija por ahora
                    ClassroomStatus status = ClassroomStatus.AVAILABLE;

                    System.out.println("insertSampleClassrooms: Creating classroom with id " + id);

                    Classroom classroom = new Classroom(Integer.parseInt(id), capacity, status);
                    classroomDAO.insertClassroom(classroom);
                }
            }
        }
    }
}