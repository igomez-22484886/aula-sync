package model.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static model.repository.SQLServerConnection.*;

public class ReservationDAO {
    public List<String> getReservationsByUser(int userId) {
        List<String> reservations = new ArrayList<>();
        String sql = "SELECT * FROM ReservationTable WHERE UserId = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reservations.add("Reservation ID: " + rs.getInt("ReservationId") +
                        ", Classroom: " + rs.getInt("ClassroomId") +
                        ", Date: " + rs.getDate("ReservationDate") +
                        ", From: " + rs.getTime("StartTime") +
                        ", To: " + rs.getTime("EndTime"));
            }

        } catch (SQLException e) {
            System.out.println("getReservationsByUser: Failed to retrieve reservations.");
            e.printStackTrace();
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
                classrooms.add(rs.getInt("ClassroomId"));
            }

        } catch (SQLException e) {
            System.out.println("getAvailableClassrooms: Failed to get available classrooms.");
            e.printStackTrace();
        }

        return classrooms;
    }

    public boolean createReservation(int userId, int classroomId, Date date, Time startTime, Time endTime) {
        String sql = """
            INSERT INTO ReservationTable (UserId, ClassroomId, ReservationDate, StartTime, EndTime)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, classroomId);
            stmt.setDate(3, date);
            stmt.setTime(4, startTime);
            stmt.setTime(5, endTime);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("createReservation: Failed to create reservation.");
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getAllReservations() {
        List<String> reservations = new ArrayList<>();
        String sql = "SELECT * FROM ReservationTable";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reservations.add("Reservation ID: " + rs.getInt("ReservationId") +
                        ", User ID: " + rs.getInt("UserId") +
                        ", Classroom ID: " + rs.getInt("ClassroomId") +
                        ", Date: " + rs.getDate("ReservationDate") +
                        ", From: " + rs.getTime("StartTime") +
                        ", To: " + rs.getTime("EndTime"));
            }

        } catch (SQLException e) {
            System.out.println("getAllReservations: Failed to retrieve reservations.");
            e.printStackTrace();
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
                reservations.add("Reservation ID: " + rs.getInt("ReservationId") +
                        ", User ID: " + rs.getInt("UserId") +
                        ", Date: " + rs.getDate("ReservationDate") +
                        ", From: " + rs.getTime("StartTime") +
                        ", To: " + rs.getTime("EndTime"));
            }

        } catch (SQLException e) {
            System.out.println("getReservationsByClassroom: Failed to retrieve reservations.");
            e.printStackTrace();
        }

        return reservations;
    }

    public boolean cancelReservation(int reservationId) {
        String sql = "DELETE FROM ReservationTable WHERE ReservationId = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservationId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("cancelReservation: Failed to cancel reservation.");
            e.printStackTrace();
            return false;
        }
    }
}

