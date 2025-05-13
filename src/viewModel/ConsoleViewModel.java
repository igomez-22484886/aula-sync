package viewModel;

import model.Classroom;
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
        // System.out.println("signUp: Checking credentials for email: " + email);
        boolean result = userDAO.checkCredentials(email, password);
        // System.out.println("signUp: Credentials check result: " + result);
        return result;
    }

    public boolean registerStudent(String email, String password) {
        // System.out.println("registerStudent: Registering student with email: " + email);
        if (!userDAO.checkInstitutionExists()) {
            // System.out.println("registerStudent: No institution exists, registration failed");
            return false;
        }

        String id = userDAO.extractUsernameFromEmail(email);
        // System.out.println("registerStudent: Extracted ID from email: " + id);
        String userName = "e" + id;
        // System.out.println("registerStudent: Generated username: " + userName);
        boolean result = userDAO.registerUser(new User(Integer.parseInt(id), userName, email, password));
        // System.out.println("registerStudent: Student registration result: " + result);
        return result;
    }

    public boolean registerTeacher(String email, String password) {
        // System.out.println("registerTeacher: Registering teacher with email: " + email);
        if (!userDAO.checkInstitutionExists()) {
            // System.out.println("registerTeacher: No institution exists, registration failed");
            return false;
        }

        String id = userDAO.extractUsernameFromEmail(email);
        // System.out.println("registerTeacher: Extracted ID from email: " + id);
        String userName = "p" + id;
        // System.out.println("registerTeacher: Generated username: " + userName);
        boolean result = userDAO.registerUser(new User(Integer.parseInt(id), userName, email, password));
        // System.out.println("registerTeacher: Teacher registration result: " + result);
        return result;
    }

    public boolean registerInstitution(String email, String password) {
        // System.out.println("registerInstitution: Registering institution with email: " + email);
        if (userDAO.checkInstitutionExists()) {
            // System.out.println("registerInstitution: Institution already exists, registration failed");
            return false;
        }
        String id = userDAO.extractUsernameFromEmail(email);
        // System.out.println("registerInstitution: Extracted ID from email: " + id);
        String userName = "a" + id;
        // System.out.println("registerInstitution: Generated username: " + userName);
        boolean result = userDAO.registerUser(new User(Integer.parseInt(id), userName, email, password));
        // System.out.println("registerInstitution: Institution registration result: " + result);
        return result;
    }

    public boolean checkForInstitution() {
        // System.out.println("checkForInstitution: Checking if institution exists");
        boolean result = userDAO.checkInstitutionExists();
        // System.out.println("checkForInstitution: Institution exists: " + result);
        return result;
    }

    public boolean reserveClassroom(String userIdStr, String classRoomIdStr, String dateStr, String startStr, String endStr) {
        // System.out.println("reserveClassroom: Creating reservation with userId: " + userIdStr + ", classroomId: " + classRoomIdStr + ", date: " + dateStr + ", start: " + startStr + ", end: " + endStr);
        try {
            int userId = Integer.parseInt(userIdStr);
            int classroomId = Integer.parseInt(classRoomIdStr);
            // System.out.println("reserveClassroom: Parsed userId: " + userId + ", classroomId: " + classroomId);
            Date reservationDate = Date.valueOf(dateStr);
            Time startTime = Time.valueOf(startStr + ":00");
            Time endTime = Time.valueOf(endStr + ":00");
            // System.out.println("reserveClassroom: Converted date: " + reservationDate + ", startTime: " + startTime + ", endTime: " + endTime);

            boolean result = reservationDAO.createReservation(userId, classroomId, reservationDate, startTime, endTime);
            // System.out.println("reserveClassroom: Reservation creation result: " + result);
            return result;

        } catch (Exception e) {
            //System.out.println("reserveClassroom: Error creating reservation: " + e.getMessage());
            System.out.println("Something went wrong, ask your administrator for help");
            return false;
        }
    }

    public void cancelClassroomReservation(String reservationId) {
        // System.out.println("cancelClassroomReservation: Cancelling reservation with ID: " + reservationId);
        try {
            int id = Integer.parseInt(reservationId);
            // System.out.println("cancelClassroomReservation: Parsed reservation ID: " + id);
            boolean success = reservationDAO.cancelReservation(id);

            if (success) {
                System.out.println("Reservation canceled successfully.");
            } else {
                System.out.println("Failed to cancel reservation. Ask your administrator for help");
            }

        } catch (Exception e) {
            //System.out.println("cancelClassroomReservation: Error cancelling reservation: " + e.getMessage());
            System.out.println("Something went wrong, ask your administrator for help");
        }
    }

    public void cancelOwnClassroomReservation(String reservationId, String userId) {
        // System.out.println("cancelClassroomReservation: Cancelling reservation with ID: " + reservationId);
        try {
            int id = Integer.parseInt(reservationId);
            // System.out.println("cancelClassroomReservation: Parsed reservation ID: " + id);
            boolean success = reservationDAO.cancelOwnReservation(id, userId);

            if (success) {
                System.out.println("Reservation canceled successfully.");
            } else {
                System.out.println("Failed to cancel reservation. Ask your administrator for help");
            }

        } catch (Exception e) {
            //System.out.println("cancelOwnClassroomReservation: Error cancelling reservation: " + e.getMessage());
            System.out.println("Something went wrong, ask your administrator for help");
        }
    }

    public List<String> getReservedClassrooms() {
        // System.out.println("getReservedClassrooms: Retrieving all reservations");
        try {
            List<String> reservations = reservationDAO.getAllReservations();
            // System.out.println("getReservedClassrooms: Retrieved " + reservations.size() + " reservations");
            return reservations;
        } catch (Exception e) {
            //System.out.println("getReservedClassrooms: Error retrieving reservations: " + e.getMessage());
            System.out.println("Something went wrong, ask your administrator for help");
            return new ArrayList<>();
        }
    }

    public void exportUserMetrics() {
        // System.out.println("exportUserMetrics: Starting export of UserTable metrics");
        try {
            ExportMetric.exportTableToCSV("UserTable");
            // System.out.println("exportUserMetrics: User metrics exported successfully.");
        } catch (Exception e) {
            //System.out.println("exportUserMetrics: Failed to export user metrics: " + e.getMessage());
            System.out.println("Something went wrong, ask your administrator for help");
        }
    }

    public void exportClassroomMetrics() {
        // System.out.println("exportClassroomMetrics: Starting export of ClassroomTable metrics");
        try {
            ExportMetric.exportTableToCSV("ClassroomTable");
            // System.out.println("exportClassroomMetrics: Classroom metrics exported successfully.");
        } catch (Exception e) {
            //System.out.println("exportClassroomMetrics: Failed to export classroom metrics: " + e.getMessage());
            System.out.println("Something went wrong, ask your administrator for help");
        }
    }

    public void exportReservationMetrics() {
        // System.out.println("exportReservationMetrics: Starting export of ReservationTable metrics");
        try {
            ExportMetric.exportTableToCSV("ReservationTable");
            // System.out.println("exportReservationMetrics: Reservation metrics exported successfully.");
        } catch (Exception e) {
            //System.out.println("exportReservationMetrics: Failed to export reservation metrics: " + e.getMessage());
            System.out.println("Something went wrong, ask your administrator for help");
        }
    }

    public void exportMostDemandedClassrooms() {
        // System.out.println("exportMostDemandedClassrooms: Starting export of most demanded classrooms metrics");
        try {
            ExportMetric.exportMostDemandedClassrooms();
            // System.out.println("exportMostDemandedClassrooms: Most demanded classrooms metrics exported successfully.");
        } catch (Exception e) {
            //System.out.println("exportMostDemandedClassrooms: Failed to export most demanded classrooms metrics: " + e.getMessage());
            System.out.println("Something went wrong, ask your administrator for help");
        }
    }

    public void exportPeakHours() {
        // System.out.println("exportPeakHours: Starting export of peak hours metrics");
        try {
            ExportMetric.exportPeakHours();
            // System.out.println("exportPeakHours: Peak hours metrics exported successfully.");
        } catch (Exception e) {
            //System.out.println("exportPeakHours: Failed to export peak hours metrics: " + e.getMessage());
            System.out.println("Something went wrong, ask your administrator for help");
        }
    }

    public void exportMostActiveUsers() {
        // System.out.println("exportMostActiveUsers: Starting export of most active users metrics");
        try {
            ExportMetric.exportMostActiveUsers();
            // System.out.println("exportMostActiveUsers: Most active users metrics exported successfully.");
        } catch (Exception e) {
            //System.out.println("exportMostActiveUsers: Failed to export most active users metrics: " + e.getMessage());
            System.out.println("Something went wrong, ask your administrator for help");
        }
    }

    public void exportAverageOccupancyTime() {
        // System.out.println("exportAverageOccupancyTime: Starting export of average occupancy time metrics");
        try {
            ExportMetric.exportAverageOccupancyTime();
            // System.out.println("exportAverageOccupancyTime: Average occupancy time metrics exported successfully.");
        } catch (Exception e) {
            //System.out.println("exportAverageOccupancyTime: Failed to export average occupancy time metrics: " + e.getMessage());
            System.out.println("Something went wrong, ask your administrator for help");
        }
    }

    public void exportOccupancyPercentage() {
        // System.out.println("exportOccupancyPercentage: Starting export of occupancy percentage metrics");
        try {
            ExportMetric.exportOccupancyPercentage();
            // System.out.println("exportOccupancyPercentage: Occupancy percentage metrics exported successfully.");
        } catch (Exception e) {
            //System.out.println("exportOccupancyPercentage: Failed to export occupancy percentage metrics: " + e.getMessage());
            System.out.println("Something went wrong, ask your administrator for help");
        }
    }

    public String getUserIdByEmail(String email) {
        try {
            return userDAO.getUserIdByEmail(email);
        } catch (Exception e) {
            return null;
        }
    }

    public User getUserById(String id) {
        try {
            return userDAO.getUserById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean createClassroom(int classroomId, int capacity) {
        Classroom existing = classroomDAO.getClassroomById(classroomId);
        if (existing != null) {
            return false;
        }

        Classroom classroom = new Classroom(classroomId, capacity);
        classroomDAO.insertClassroom(classroom);
        return true;
    }


    public void insertSampleClassrooms() {
        // System.out.println("insertSampleClassrooms: Starting insertion of sample classrooms");
        for (int building = 1; building <= 4; building++) {
            for (int floor = 1; floor <= 3; floor++) {
                for (int classNumber = 1; classNumber <= 5; classNumber++) {
                    String id = String.format("%d%d%02d", building, floor, classNumber);
                    // System.out.println("insertSampleClassrooms: Creating classroom with ID: " + id);

                    int capacity = 30; // Capacidad fija por ahora
                    Classroom classroom = new Classroom(Integer.parseInt(id), capacity);
                    classroomDAO.insertClassroom(classroom);
                    // System.out.println("insertSampleClassrooms: Inserted classroom with ID: " + id);
                }
            }
        }

        System.out.println("Completed insertion of sample classrooms");
    }

    public int getClassroomCount() {
        // System.out.println("getClassroomCount: Retrieving classroom count");
        List<Classroom> classrooms = classroomDAO.getAllClassrooms();
        // System.out.println("getClassroomCount: Total classrooms retrieved: " + classrooms.size());
        return classrooms.size();
    }
}