package model.daos;

import model.Classroom;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static model.repository.SQLServerConnection.*;

public class ClassroomDAO {

    private static final Logger LOGGER = Logger.getLogger(ClassroomDAO.class.getName());

    public void insertClassroom(Classroom classroom) {
        String sql = "INSERT INTO ClassroomTable (ClassroomId, Capacity) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // System.out.println("insertClassroom Preparing to insert classroom: " + classroom);

            stmt.setInt(1, classroom.getClassroomId());
            stmt.setInt(2, classroom.getCapacity());
            int rowsInserted = stmt.executeUpdate();

            // System.out.println("insertClassroom Rows inserted: " + rowsInserted);

            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        classroom.setId(generatedKeys.getInt(1));
                        // System.out.println("insertClassroom Generated ID: " + classroom.getId());
                    }
                }
            }
        } catch (SQLException e) {
          //  System.out.println("insertClassroom SQL Exception: " + e.getMessage());
        }
    }

    public Classroom getClassroomById(int id) {
        String sql = "SELECT * FROM ClassroomTable WHERE ClassroomId = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // System.out.println("getClassroomById Looking for classroom with ID: " + id);

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Classroom classroom = new Classroom(
                            rs.getInt("ClassroomId"),
                            rs.getInt("Capacity")
                    );
                    classroom.setId(rs.getInt("Id"));
                    // System.out.println("getClassroomById Classroom found: " + classroom);
                    return classroom;
                } else {
                    // System.out.println("getClassroomById No classroom found with ID: " + id);
                }
            }
        } catch (SQLException e) {
          //  System.out.println("getClassroomById SQL Exception: " + e.getMessage());
        }
        return null;
    }

    public List<Classroom> getAllClassrooms() {
        String sql = "SELECT * FROM ClassroomTable";
        List<Classroom> classrooms = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // System.out.println("getAllClassrooms Fetching all classrooms...");

            while (rs.next()) {
                Classroom classroom = new Classroom(
                        rs.getInt("ClassroomId"),
                        rs.getInt("Capacity")
                );
                classroom.setId(rs.getInt("Id"));
                classrooms.add(classroom);
            }

            // System.out.println("getAllClassrooms Total classrooms retrieved: " + classrooms.size());
        } catch (SQLException e) {
          //  System.out.println("getAllClassrooms SQL Exception: " + e.getMessage());
        }
        return classrooms;
    }

    public boolean updateClassroom(Classroom classroom) {
        String sql = "UPDATE ClassroomTable SET Capacity = ? WHERE ClassroomId = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // System.out.println("updateClassroom Updating classroom: " + classroom);

            stmt.setInt(1, classroom.getCapacity());
            stmt.setInt(2, classroom.getClassroomId());
            int updated = stmt.executeUpdate();

            // System.out.println("updateClassroom Rows updated: " + updated);
            return updated > 0;
        } catch (SQLException e) {
          //  System.out.println("updateClassroom SQL Exception: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteClassroom(int id) {
        String sql = "DELETE FROM ClassroomTable WHERE ClassroomId = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // System.out.println("deleteClassroom Deleting classroom with ID: " + id);

            stmt.setInt(1, id);
            int deleted = stmt.executeUpdate();

            // System.out.println("deleteClassroom Rows deleted: " + deleted);
            return deleted > 0;
        } catch (SQLException e) {
          //  System.out.println("deleteClassroom SQL Exception: " + e.getMessage());
            return false;
        }
    }
}
