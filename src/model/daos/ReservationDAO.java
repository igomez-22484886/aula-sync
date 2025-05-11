package model.daos;

import model.Classroom;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static model.repository.SQLServerConnection.*;

public class ReservationDAO {

    private static final Logger LOGGER = Logger.getLogger(ReservationDAO.class.getName());
    private final UserDAO userDAO;

    public ReservationDAO() {
        this.userDAO = new UserDAO();
    }

    public List<String> getReservationsByUser(int userId) {
        List<String> reservations = new ArrayList<>();
        String sql = "SELECT * FROM ReservationTable WHERE UserId = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String reservation = "Reservation ID: " + rs.getInt("ReservationId") +
                        ", Classroom: " + rs.getInt("ClassroomId") +
                        ", Date: " + rs.getDate("ReservationDate") +
                        ", From: " + rs.getTime("StartTime") +
                        ", To: " + rs.getTime("EndTime");
                reservations.add(reservation);
            }

        } catch (SQLException e) {
            System.out.println("getReservationsByUser: SQL error occurred: " + e.getMessage());
        }

        return reservations;
    }

    public List<Integer> getAvailableClassrooms(Date reservationDate, Time startTime, Time endTime) {
        List<Integer> classrooms = new ArrayList<>();
        String sql = """
                SELECT ClassroomId FROM ClassroomTable WHERE ClassroomId NOT IN (
                    SELECT ClassroomId FROM ReservationTable
                    WHERE ReservationDate = ? AND (
                        (? < EndTime AND ? > StartTime)
                    )
                )
                """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, reservationDate);
            stmt.setTime(2, startTime);
            stmt.setTime(3, endTime);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int classroomId = rs.getInt("ClassroomId");
                classrooms.add(classroomId);
            }

        } catch (SQLException e) {
            System.out.println("getAvailableClassrooms: SQL error occurred: " + e.getMessage());
        }

        return classrooms;
    }

    public boolean createReservation(int userId, int classroomId, Date date, Time startTime, Time endTime) {
        String insertSql = """
                INSERT INTO ReservationTable (UserId, ClassroomId, ReservationDate, StartTime, EndTime)
                VALUES (?, ?, ?, ?, ?)
                """;
        String checkSql = """
                SELECT ReservationId, UserId FROM ReservationTable
                WHERE ClassroomId = ? AND ReservationDate = ?
                AND (StartTime < CAST(? AS TIME) AND EndTime > CAST(? AS TIME))
                """;
        String deleteSql = "DELETE FROM ReservationTable WHERE ReservationId = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);

            User newUser = userDAO.getUserById(String.valueOf(userId));
            if (newUser == null) {
                // System.out.println("createReservation: User ID " + userId + " not found");
                conn.rollback();
                return false;
            }

            int newPriority = newUser.getPriority();
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, classroomId);
                checkStmt.setDate(2, date);
                checkStmt.setTime(3, endTime);
                checkStmt.setTime(4, startTime);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    int existingReservationId = rs.getInt("ReservationId");
                    int existingUserId = rs.getInt("UserId");

                    User existingUser = userDAO.getUserById(String.valueOf(existingUserId));
                    if (existingUser == null) {
                        // System.out.println("createReservation: Existing user ID " + existingUserId + " not found");
                        conn.rollback();
                        return false;
                    }

                    int existingPriority = existingUser.getPriority();
                    if (newPriority > existingPriority) {
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                            deleteStmt.setInt(1, existingReservationId);
                            deleteStmt.executeUpdate();
                            System.out.println("Replaced reservation ID " + existingReservationId + " due to higher priority");
                        }
                    } else {
                        System.out.println("Reservation rejected due to lower or equal priority");
                        conn.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, classroomId);
                insertStmt.setDate(3, date);
                insertStmt.setTime(4, startTime);
                insertStmt.setTime(5, endTime);
                int rowsInserted = insertStmt.executeUpdate();

                conn.commit();
                return rowsInserted > 0;
            }

        } catch (SQLException e) {
            System.out.println("createReservation: SQL error occurred: " + e.getMessage());
            return false;
        }
    }

    private boolean checkClassroomReservation(int classroomId, Date date, Time startTime, Time endTime) {
        String checkSql = """
                SELECT COUNT(*) FROM ReservationTable
                WHERE ClassroomId = ? AND ReservationDate = ?
                AND (StartTime < CAST(? AS TIME) AND EndTime > CAST(? AS TIME))
                """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, classroomId);
            checkStmt.setDate(2, date);
            checkStmt.setTime(3, endTime);
            checkStmt.setTime(4, startTime);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("checkClassroomReservation: SQL error occurred: " + e.getMessage());
        }
        return false;
    }

    public List<String> getAllReservations() {
        List<String> reservations = new ArrayList<>();
        String sql = "SELECT * FROM ReservationTable";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String reservation = "Reservation ID: " + rs.getInt("ReservationId") +
                        ", User ID: " + rs.getInt("UserId") +
                        ", Classroom ID: " + rs.getInt("ClassroomId") +
                        ", Date: " + rs.getDate("ReservationDate") +
                        ", From: " + rs.getTime("StartTime") +
                        ", To: " + rs.getTime("EndTime");
                reservations.add(reservation);
            }

        } catch (SQLException e) {
            System.out.println("getAllReservations: SQL error occurred: " + e.getMessage());
        }

        return reservations;
    }

    public List<String> getReservationsByClassroom(int classroomId) {
        List<String> reservations = new ArrayList<>();
        String sql = "SELECT * FROM ReservationTable WHERE ClassroomId = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classroomId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String reservation = "Reservation ID: " + rs.getInt("ReservationId") +
                        ", User ID: " + rs.getInt("UserId") +
                        ", Date: " + rs.getDate("ReservationDate") +
                        ", From: " + rs.getTime("StartTime") +
                        ", To: " + rs.getTime("EndTime");
                reservations.add(reservation);
            }

        } catch (SQLException e) {
            System.out.println("getReservationsByClassroom: SQL error occurred: " + e.getMessage());
        }

        return reservations;
    }

    public boolean cancelReservation(int reservationId) {
        String getClassroomSql = "SELECT ClassroomId FROM ReservationTable WHERE ReservationId = ?";
        String deleteSql = "DELETE FROM ReservationTable WHERE ReservationId = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);

            try (PreparedStatement getStmt = conn.prepareStatement(getClassroomSql);
                 PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {

                getStmt.setInt(1, reservationId);
                ResultSet rs = getStmt.executeQuery();
                int classroomId = -1;
                if (rs.next()) {
                    classroomId = rs.getInt("ClassroomId");
                } else {
                    conn.rollback();
                    return false;
                }

                deleteStmt.setInt(1, reservationId);
                int rowsDeleted = deleteStmt.executeUpdate();

                conn.commit();
                return rowsDeleted > 0;

            } catch (SQLException e) {
                System.out.println("cancelReservation: SQL error occurred, rolling back: " + e.getMessage());
                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            System.out.println("cancelReservation: Database connection error: " + e.getMessage());
            return false;
        }
    }

    public List<Integer> getClassroomsReservedAt(Date currentDate, Time currentTime) {
        List<Integer> reservedClassrooms = new ArrayList<>();
        String sql = """
                    SELECT ClassroomId FROM ReservationTable
                    WHERE ReservationDate = ? AND ? BETWEEN StartTime AND EndTime
                """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, currentDate);
            stmt.setTime(2, currentTime);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int classroomId = rs.getInt("ClassroomId");
                reservedClassrooms.add(classroomId);
            }

        } catch (SQLException e) {
            System.out.println("getClassroomsReservedAt: SQL error occurred: " + e.getMessage());
        }

        return reservedClassrooms;
    }

    public boolean cancelOwnReservation(int reservationId, String userId) {
        String getReservationSql = "SELECT ClassroomId, UserId FROM ReservationTable WHERE ReservationId = ?";
        String deleteSql = "DELETE FROM ReservationTable WHERE ReservationId = ?";
        String updateStatusSql = "UPDATE ClassroomTable SET Status = 'Available' WHERE ClassroomId = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);

            try (PreparedStatement getStmt = conn.prepareStatement(getReservationSql);
                 PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateStatusSql)) {

                getStmt.setInt(1, reservationId);
                ResultSet rs = getStmt.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }

                int classroomId = rs.getInt("ClassroomId");
                int reservationUserId = rs.getInt("UserId");
                int parsedUserId = Integer.parseInt(userId);

                if (reservationUserId != parsedUserId) {
                    conn.rollback();
                    return false;
                }

                deleteStmt.setInt(1, reservationId);
                int rowsDeleted = deleteStmt.executeUpdate();

                updateStmt.setInt(1, classroomId);
                updateStmt.executeUpdate();

                conn.commit();
                return rowsDeleted > 0;

            } catch (SQLException e) {
                System.out.println("cancelOwnReservation: SQL error occurred, rolling back: " + e.getMessage());
                conn.rollback();
                return false;
            } catch (NumberFormatException e) {
                System.out.println("cancelOwnReservation: Invalid user ID format: " + userId);
                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            System.out.println("cancelOwnReservation: Database connection error: " + e.getMessage());
            return false;
        }
    }
}