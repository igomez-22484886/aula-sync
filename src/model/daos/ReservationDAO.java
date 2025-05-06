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
        List<String> reservations = new ArrayList<>();
        String sql = "SELECT * FROM ReservationTable WHERE UserId = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservations.add("Reservation ID: " + rs.getInt("ReservationId") + ", Classroom: " + rs.getInt("ClassroomId") + ", Date: " + rs.getDate("ReservationDate") + ", From: " + rs.getTime("StartTime") + ", To: " + rs.getTime("EndTime"));
            }


        } catch (SQLException e) {
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


        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, reservationDate);
            stmt.setTime(2, startTime);
            stmt.setTime(3, endTime);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                classrooms.add(rs.getInt("ClassroomId"));
            }


        } catch (SQLException e) {
        }

        return classrooms;
    }

    public boolean createReservation(int userId, int classroomId, Date date, Time startTime, Time endTime) {

        if (checkClassroomReservation(classroomId, date, startTime, endTime)) {
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

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql); PreparedStatement updateStmt = conn.prepareStatement(updateStatusSql)) {

                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, classroomId);
                insertStmt.setDate(3, date);
                insertStmt.setTime(4, startTime);
                insertStmt.setTime(5, endTime);

                int rowsInserted = insertStmt.executeUpdate();

                updateStmt.setInt(1, classroomId);
                updateStmt.executeUpdate();

                conn.commit();

                return rowsInserted > 0;
            } catch (SQLException e) {
                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            return false;
        }
    }

    private boolean checkClassroomReservation(int classroomId, Date date, Time startTime, Time endTime) {
        String checkSql = """
                SELECT COUNT(*) FROM ReservationTable
                WHERE ClassroomId = ? AND ReservationDate = ?
                AND (StartTime < CAST(? AS TIME) AND EndTime > CAST(? AS TIME))
                """;


        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, classroomId);
            checkStmt.setDate(2, date);
            checkStmt.setTime(3, endTime);   // overlap condition
            checkStmt.setTime(4, startTime); // overlap condition

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
        }
        return false;
    }

    public List<String> getAllReservations() {
        List<String> reservations = new ArrayList<>();
        String sql = "SELECT * FROM ReservationTable";


        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reservations.add("Reservation ID: " + rs.getInt("ReservationId") + ", User ID: " + rs.getInt("UserId") + ", Classroom ID: " + rs.getInt("ClassroomId") + ", Date: " + rs.getDate("ReservationDate") + ", From: " + rs.getTime("StartTime") + ", To: " + rs.getTime("EndTime"));
            }


        } catch (SQLException e) {
        }

        return reservations;
    }

    public List<String> getReservationsByClassroom(int classroomId) {
        List<String> reservations = new ArrayList<>();
        String sql = "SELECT * FROM ReservationTable WHERE ClassroomId = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classroomId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservations.add("Reservation ID: " + rs.getInt("ReservationId") + ", User ID: " + rs.getInt("UserId") + ", Date: " + rs.getDate("ReservationDate") + ", From: " + rs.getTime("StartTime") + ", To: " + rs.getTime("EndTime"));
            }


        } catch (SQLException e) {
        }

        return reservations;
    }

    public boolean cancelReservation(int reservationId) {
        String getClassroomSql = "SELECT ClassroomId FROM ReservationTable WHERE ReservationId = ?";
        String deleteSql = "DELETE FROM ReservationTable WHERE ReservationId = ?";
        String updateStatusSql = "UPDATE ClassroomTable SET Status = 'Available' WHERE ClassroomId = ?";


        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);

            try (PreparedStatement getStmt = conn.prepareStatement(getClassroomSql); PreparedStatement deleteStmt = conn.prepareStatement(deleteSql); PreparedStatement updateStmt = conn.prepareStatement(updateStatusSql)) {

                int classroomId = -1;
                getStmt.setInt(1, reservationId);
                ResultSet rs = getStmt.executeQuery();
                if (rs.next()) {
                    classroomId = rs.getInt("ClassroomId");
                } else {
                    return false;
                }

                deleteStmt.setInt(1, reservationId);
                int rowsDeleted = deleteStmt.executeUpdate();

                updateStmt.setInt(1, classroomId);
                updateStmt.executeUpdate();

                conn.commit();

                return rowsDeleted > 0;

            } catch (SQLException e) {
                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            return false;
        }
    }

    public List<Integer> getClassroomsReservedAt(Date currentDate, Time currentTime) {
        List<Integer> reservedClassrooms = new ArrayList<>();
        String sql = """
                    SELECT ClassroomId FROM ReservationTable
                    WHERE ReservationDate = ? AND ? BETWEEN StartTime AND EndTime
                """;


        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, currentDate);
            stmt.setTime(2, currentTime);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservedClassrooms.add(rs.getInt("ClassroomId"));
            }


        } catch (SQLException e) {
        }

        return reservedClassrooms;
    }
}