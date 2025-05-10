package model.daos;

import model.Classroom;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static model.repository.SQLServerConnection.*;

public class ReservationDAO {

    private static final Logger LOGGER = Logger.getLogger(ReservationDAO.class.getName());

    public List<String> getReservationsByUser(int userId) {
        System.out.println("getReservationsByUser: Retrieving reservations for user ID: " + userId);
        List<String> reservations = new ArrayList<>();
        String sql = "SELECT * FROM ReservationTable WHERE UserId = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            System.out.println("getReservationsByUser: Executing query for user ID: " + userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String reservation = "Reservation ID: " + rs.getInt("ReservationId") +
                        ", Classroom: " + rs.getInt("ClassroomId") +
                        ", Date: " + rs.getDate("ReservationDate") +
                        ", From: " + rs.getTime("StartTime") +
                        ", To: " + rs.getTime("EndTime");
                reservations.add(reservation);
                System.out.println("getReservationsByUser: Found reservation: " + reservation);
            }
            System.out.println("getReservationsByUser: Retrieved " + reservations.size() + " reservations for user ID: " + userId);

        } catch (SQLException e) {
            System.out.println("getReservationsByUser: SQL error occurred: " + e.getMessage());
        }

        return reservations;
    }

    public List<Integer> getAvailableClassrooms(Date reservationDate, Time startTime, Time endTime) {
        System.out.println("getAvailableClassrooms: Retrieving available classrooms for date: " + reservationDate +
                ", start: " + startTime + ", end: " + endTime);
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
            System.out.println("getAvailableClassrooms: Executing query for date: " + reservationDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int classroomId = rs.getInt("ClassroomId");
                classrooms.add(classroomId);
                System.out.println("getAvailableClassrooms: Found available classroom ID: " + classroomId);
            }
            System.out.println("getAvailableClassrooms: Retrieved " + classrooms.size() + " available classrooms");

        } catch (SQLException e) {
            System.out.println("getAvailableClassrooms: SQL error occurred: " + e.getMessage());
        }

        return classrooms;
    }

    public boolean createReservation(int userId, int classroomId, Date date, Time startTime, Time endTime) {
        System.out.println("createReservation: Creating reservation for user ID: " + userId +
                ", classroom ID: " + classroomId + ", date: " + date +
                ", start: " + startTime + ", end: " + endTime);

        if (checkClassroomReservation(classroomId, date, startTime, endTime)) {
            System.out.println("createReservation: Classroom ID " + classroomId + " is already reserved for the specified time");
            return false;
        }

        String insertSql = """
                INSERT INTO ReservationTable (UserId, ClassroomId, ReservationDate, StartTime, EndTime)
                VALUES (?, ?, ?, ?, ?)
                """;

        String updateStatusSql = """
                UPDATE ClassroomTable SET Status = '%s' WHERE ClassroomId = ?""".formatted(Classroom.ClassroomStatus.RESERVED.getLabel());

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);
            System.out.println("createReservation: Starting transaction");

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateStatusSql)) {

                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, classroomId);
                insertStmt.setDate(3, date);
                insertStmt.setTime(4, startTime);
                insertStmt.setTime(5, endTime);
                System.out.println("createReservation: Executing insert into ReservationTable");
                int rowsInserted = insertStmt.executeUpdate();

                updateStmt.setInt(1, classroomId);
                System.out.println("createReservation: Updating ClassroomTable status to RESERVED for classroom ID: " + classroomId);
                updateStmt.executeUpdate();

                conn.commit();
                System.out.println("createReservation: Transaction committed, reservation created successfully");
                return rowsInserted > 0;

            } catch (SQLException e) {
                System.out.println("createReservation: SQL error occurred, rolling back: " + e.getMessage());
                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            System.out.println("createReservation: Database connection error: " + e.getMessage());
            return false;
        }
    }

    private boolean checkClassroomReservation(int classroomId, Date date, Time startTime, Time endTime) {
        System.out.println("checkClassroomReservation: Checking reservation for classroom ID: " + classroomId +
                ", date: " + date + ", start: " + startTime + ", end: " + endTime);
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
            System.out.println("checkClassroomReservation: Executing query to check reservation overlap");

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("checkClassroomReservation: Found " + count + " overlapping reservations");
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("checkClassroomReservation: SQL error occurred: " + e.getMessage());
        }
        System.out.println("checkClassroomReservation: No overlapping reservations found");
        return false;
    }

    public List<String> getAllReservations() {
        System.out.println("getAllReservations: Retrieving all reservations");
        List<String> reservations = new ArrayList<>();
        String sql = "SELECT * FROM ReservationTable";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("getAllReservations: Executing query to retrieve all reservations");
            while (rs.next()) {
                String reservation = "Reservation ID: " + rs.getInt("ReservationId") +
                        ", User ID: " + rs.getInt("UserId") +
                        ", Classroom ID: " + rs.getInt("ClassroomId") +
                        ", Date: " + rs.getDate("ReservationDate") +
                        ", From: " + rs.getTime("StartTime") +
                        ", To: " + rs.getTime("EndTime");
                reservations.add(reservation);
                System.out.println("getAllReservations: Found reservation: " + reservation);
            }
            System.out.println("getAllReservations: Retrieved " + reservations.size() + " reservations");

        } catch (SQLException e) {
            System.out.println("getAllReservations: SQL error occurred: " + e.getMessage());
        }

        return reservations;
    }

    public List<String> getReservationsByClassroom(int classroomId) {
        System.out.println("getReservationsByClassroom: Retrieving reservations for classroom ID: " + classroomId);
        List<String> reservations = new ArrayList<>();
        String sql = "SELECT * FROM ReservationTable WHERE ClassroomId = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classroomId);
            System.out.println("getReservationsByClassroom: Executing query for classroom ID: " + classroomId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String reservation = "Reservation ID: " + rs.getInt("ReservationId") +
                        ", User ID: " + rs.getInt("UserId") +
                        ", Date: " + rs.getDate("ReservationDate") +
                        ", From: " + rs.getTime("StartTime") +
                        ", To: " + rs.getTime("EndTime");
                reservations.add(reservation);
                System.out.println("getReservationsByClassroom: Found reservation: " + reservation);
            }
            System.out.println("getReservationsByClassroom: Retrieved " + reservations.size() + " reservations for classroom ID: " + classroomId);

        } catch (SQLException e) {
            System.out.println("getReservationsByClassroom: SQL error occurred: " + e.getMessage());
        }

        return reservations;
    }

    public boolean cancelReservation(int reservationId) {
        System.out.println("cancelReservation: Cancelling reservation ID: " + reservationId);
        String getClassroomSql = "SELECT ClassroomId FROM ReservationTable WHERE ReservationId = ?";
        String deleteSql = "DELETE FROM ReservationTable WHERE ReservationId = ?";
        String updateStatusSql = "UPDATE ClassroomTable SET Status = 'Available' WHERE ClassroomId = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);
            System.out.println("cancelReservation: Starting transaction");

            try (PreparedStatement getStmt = conn.prepareStatement(getClassroomSql);
                 PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateStatusSql)) {

                getStmt.setInt(1, reservationId);
                System.out.println("cancelReservation: Checking if reservation ID " + reservationId + " exists");
                ResultSet rs = getStmt.executeQuery();
                int classroomId = -1;
                if (rs.next()) {
                    classroomId = rs.getInt("ClassroomId");
                    System.out.println("cancelReservation: Found classroom ID: " + classroomId + " for reservation ID: " + reservationId);
                } else {
                    System.out.println("cancelReservation: Reservation ID " + reservationId + " not found");
                    conn.rollback();
                    return false;
                }

                deleteStmt.setInt(1, reservationId);
                System.out.println("cancelReservation: Deleting reservation ID: " + reservationId);
                int rowsDeleted = deleteStmt.executeUpdate();

                updateStmt.setInt(1, classroomId);
                System.out.println("cancelReservation: Updating classroom ID " + classroomId + " status to Available");
                updateStmt.executeUpdate();

                conn.commit();
                System.out.println("cancelReservation: Transaction committed, reservation ID " + reservationId + " canceled successfully");
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
        System.out.println("getClassroomsReservedAt: Retrieving classrooms reserved at date: " + currentDate + ", time: " + currentTime);
        List<Integer> reservedClassrooms = new ArrayList<>();
        String sql = """
                    SELECT ClassroomId FROM ReservationTable
                    WHERE ReservationDate = ? AND ? BETWEEN StartTime AND EndTime
                """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, currentDate);
            stmt.setTime(2, currentTime);
            System.out.println("getClassroomsReservedAt: Executing query for date: " + currentDate + ", time: " + currentTime);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int classroomId = rs.getInt("ClassroomId");
                reservedClassrooms.add(classroomId);
                System.out.println("getClassroomsReservedAt: Found reserved classroom ID: " + classroomId);
            }
            System.out.println("getClassroomsReservedAt: Retrieved " + reservedClassrooms.size() + " reserved classrooms");

        } catch (SQLException e) {
            System.out.println("getClassroomsReservedAt: SQL error occurred: " + e.getMessage());
        }

        return reservedClassrooms;
    }

    public boolean cancelOwnReservation(int reservationId, String userId) {
        System.out.println("cancelOwnReservation: Cancelling reservation ID: " + reservationId + " for user ID: " + userId);
        String getReservationSql = "SELECT ClassroomId, UserId FROM ReservationTable WHERE ReservationId = ?";
        String deleteSql = "DELETE FROM ReservationTable WHERE ReservationId = ?";
        String updateStatusSql = "UPDATE ClassroomTable SET Status = 'Available' WHERE ClassroomId = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);
            System.out.println("cancelOwnReservation: Starting transaction");

            try (PreparedStatement getStmt = conn.prepareStatement(getReservationSql);
                 PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateStatusSql)) {

                // Verify reservation exists and belongs to the user
                getStmt.setInt(1, reservationId);
                System.out.println("cancelOwnReservation: Checking if reservation ID " + reservationId + " exists");
                ResultSet rs = getStmt.executeQuery();
                if (!rs.next()) {
                    System.out.println("cancelOwnReservation: Reservation ID " + reservationId + " not found");
                    conn.rollback();
                    return false;
                }

                int classroomId = rs.getInt("ClassroomId");
                int reservationUserId = rs.getInt("UserId");
                System.out.println("cancelOwnReservation: Found classroom ID: " + classroomId + ", user ID: " + reservationUserId);
                int parsedUserId = Integer.parseInt(userId);
                System.out.println("cancelOwnReservation: Parsed user ID: " + parsedUserId);

                if (reservationUserId != parsedUserId) {
                    System.out.println("cancelOwnReservation: User ID " + parsedUserId + " does not own reservation ID " + reservationId);
                    conn.rollback();
                    return false;
                }

                // Delete the reservation
                deleteStmt.setInt(1, reservationId);
                System.out.println("cancelOwnReservation: Deleting reservation ID: " + reservationId);
                int rowsDeleted = deleteStmt.executeUpdate();

                // Update classroom status
                updateStmt.setInt(1, classroomId);
                System.out.println("cancelOwnReservation: Updating classroom ID " + classroomId + " status to Available");
                updateStmt.executeUpdate();

                conn.commit();
                System.out.println("cancelOwnReservation: Transaction committed, reservation ID " + reservationId + " canceled successfully by user ID " + parsedUserId);
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