package model.daos;

import model.Classroom;
import model.Classroom.ClassroomStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static model.repository.SQLServerConnection.*;

public class ClassroomDAO {
    public void insertClassroom(Classroom classroom) {
        String sql = "INSERT INTO ClassroomTable (ClassroomId, Capacity, Status) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, classroom.getClassroomId()); // Se guarda ClassroomId, no id
            stmt.setInt(2, classroom.getCapacity());
            stmt.setString(3, classroom.getStatus().getLabel());
            int rowsInserted = stmt.executeUpdate();
            System.out.println("ClassroomDAO: Inserted " + rowsInserted + " row(s).");

            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        classroom.setId(generatedKeys.getInt(1)); // Obtener el id generado
                        System.out.println("ClassroomDAO: Generated ClassroomId = " + classroom.getId());
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("ClassroomDAO: Error inserting classroom - " + e.getMessage());
        }
    }

    public Classroom getClassroomById(int id) {
        String sql = "SELECT * FROM ClassroomTable WHERE ClassroomId = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Classroom classroom = new Classroom(
                            rs.getInt("ClassroomId"),    // Obtén el ClassroomId de la DB
                            rs.getInt("Capacity"),
                            ClassroomStatus.fromLabel(rs.getString("Status"))
                    );
                    classroom.setId(rs.getInt("Id")); // Asignar el id del registro
                    System.out.println("ClassroomDAO: Retrieved classroom with ClassroomId = " + id);
                    return classroom;
                } else {
                    System.out.println("ClassroomDAO: No classroom found with ClassroomId = " + id);
                }
            }
        } catch (SQLException e) {
            System.out.println("ClassroomDAO: Error retrieving classroom by ClassroomId - " + e.getMessage());
        }
        return null;
    }

    public List<Classroom> getAllClassrooms() {
        String sql = "SELECT * FROM ClassroomTable";
        List<Classroom> classrooms = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                classrooms.add(new Classroom(
                        rs.getInt("ClassroomId"),
                        rs.getInt("Capacity"),
                        ClassroomStatus.fromLabel(rs.getString("Status"))
                ));
            }
            System.out.println("ClassroomDAO: Retrieved " + classrooms.size() + " classrooms.");
        } catch (SQLException e) {
            System.out.println("ClassroomDAO: Error retrieving all classrooms - " + e.getMessage());
        }
        return classrooms;
    }

    public boolean updateClassroom(Classroom classroom) {
        String sql = "UPDATE ClassroomTable SET Capacity = ?, Status = ? WHERE ClassroomId = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classroom.getCapacity());
            stmt.setString(2, classroom.getStatus().getLabel());
            stmt.setInt(3, classroom.getClassroomId());  // Se usa ClassroomId para actualizar
            int updated = stmt.executeUpdate();
            System.out.println("ClassroomDAO: Updated " + updated + " row(s) for ClassroomId = " + classroom.getClassroomId());
            return updated > 0;
        } catch (SQLException e) {
            System.out.println("ClassroomDAO: Error updating classroom - " + e.getMessage());
            return false;
        }
    }

    public boolean deleteClassroom(int id) {
        String sql = "DELETE FROM ClassroomTable WHERE ClassroomId = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int deleted = stmt.executeUpdate();
            System.out.println("ClassroomDAO: Deleted " + deleted + " row(s) for ClassroomId = " + id);
            return deleted > 0;
        } catch (SQLException e) {
            System.out.println("ClassroomDAO: Error deleting classroom - " + e.getMessage());
            return false;
        }
    }
}