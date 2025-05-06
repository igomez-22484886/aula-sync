package model.daos;

import model.Classroom;
import model.Classroom.ClassroomStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static model.repository.SQLServerConnection.*;

public class ClassroomDAO {

    private static final Logger LOGGER = Logger.getLogger(ClassroomDAO.class.getName());

    public void insertClassroom(Classroom classroom) {
        String sql = "INSERT INTO ClassroomTable (ClassroomId, Capacity, Status) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, classroom.getClassroomId());
            stmt.setInt(2, classroom.getCapacity());
            stmt.setString(3, classroom.getStatus().getLabel());
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        classroom.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
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
                            rs.getInt("ClassroomId"),
                            rs.getInt("Capacity"),
                            ClassroomStatus.fromLabel(rs.getString("Status"))
                    );
                    classroom.setId(rs.getInt("Id"));
                    return classroom;
                } else {
                }
            }
        } catch (SQLException e) {
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
        } catch (SQLException e) {
        }
        return classrooms;
    }

    public boolean updateClassroom(Classroom classroom) {
        String sql = "UPDATE ClassroomTable SET Capacity = ?, Status = ? WHERE ClassroomId = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classroom.getCapacity());
            stmt.setString(2, classroom.getStatus().getLabel());
            stmt.setInt(3, classroom.getClassroomId());
            int updated = stmt.executeUpdate();
            return updated > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteClassroom(int id) {
        String sql = "DELETE FROM ClassroomTable WHERE ClassroomId = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int deleted = stmt.executeUpdate();
            return deleted > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public void updateClassroomStatus(int classroomId, ClassroomStatus classroomStatus) {
        String sql = "UPDATE ClassroomTable SET Status = ? WHERE ClassroomId = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, classroomStatus.getLabel());
            stmt.setInt(2, classroomId);
            int rows = stmt.executeUpdate();
        } catch (SQLException e) {
        }
    }
}