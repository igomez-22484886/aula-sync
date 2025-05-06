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
import java.util.logging.Logger;

public class ConsoleViewModel {
    UserDAO userDAO = new UserDAO();
    ReservationDAO reservationDAO = new ReservationDAO();
    ClassroomDAO classroomDAO = new ClassroomDAO();

    private static final Logger LOGGER = Logger.getLogger(ConsoleViewModel.class.getName());

    public boolean signUp(String email, String password) {
        return userDAO.checkCredentials(email, password);
    }

    public boolean registerStudent(String email, String password) {
        if (!userDAO.checkInstitutionExists()) {
            return false;
        }

        String id = userDAO.extractUsernameFromEmail(email);
        String userName = "e" + id;
        return userDAO.registerUser(new User(Integer.parseInt(id), userName, email, password));
    }

    public boolean registerTeacher(String email, String password) {
        if (!userDAO.checkInstitutionExists()) {
            return false;
        }

        String id = userDAO.extractUsernameFromEmail(email);
        String userName = "p" + id;
        return userDAO.registerUser(new User(Integer.parseInt(id), userName, email, password));
    }

    public boolean registerInstitution(String email, String password) {
        if (userDAO.checkInstitutionExists()) {
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
            // e.printStackTrace();
            return false;
        }
    }

    public void cancelClassroomReservation(String reservationId) {
        try {
            int id = Integer.parseInt(reservationId);
            boolean success = reservationDAO.cancelReservation(id);

            if (success) {
                System.out.println("Reservation canceled successfully.");
            } else {
                System.out.println("Failed to cancel reservation.");
            }

        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public List<String> getReservedClassrooms() {
        try {
            return reservationDAO.getAllReservations();
        } catch (Exception e) {
            // e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void exportUserMetrics() {
        try {
            ExportMetric exportMetric = new ExportMetric();
            exportMetric.exportTableToCSV("UserTable");
            System.out.println("User metrics exported.");
        } catch (Exception e) {
            System.out.println("Failed to export user metrics.");
            // e.printStackTrace();
        }
    }

    public void exportClassroomMetrics() {
        try {
            ExportMetric exportMetric = new ExportMetric();
            exportMetric.exportTableToCSV("ClassroomTable");
            System.out.println("Classroom metrics exported.");
        } catch (Exception e) {
            System.out.println("Failed to export classroom metrics.");
            // e.printStackTrace();
        }
    }

    public void exportReservationMetrics() {
        try {
            ExportMetric exportMetric = new ExportMetric();
            exportMetric.exportTableToCSV("ReservationTable");
            System.out.println("Reservation metrics exported.");
        } catch (Exception e) {
            System.out.println("Failed to export reservation metrics.");
            // e.printStackTrace();
        }
    }

    public String getUserIdByEmail(String email) {
        try {
            return userDAO.getUserIdByEmail(email);
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        }
    }

    public User getUserById(String id) {
        try {
            return userDAO.getUserById(id);
        } catch (Exception e) {
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


                    Classroom classroom = new Classroom(Integer.parseInt(id), capacity, status);
                    classroomDAO.insertClassroom(classroom);
                }
            }
        }
    }
}