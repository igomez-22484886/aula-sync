package model.daos;

import model.User;

import java.sql.*;
import java.util.logging.Logger;

import static model.repository.SQLServerConnection.*;

public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    public boolean registerUser(User user) {

        if (user.getUserName().startsWith("a")) {
            if (checkInstitutionExists()) {
                return false;
            }
        }

        String sql = "INSERT INTO UserTable (username, password, email) VALUES (?, ?, ?)";
        boolean result = false;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());

            stmt.executeUpdate();
            result = true;

        } catch (SQLException e) {
        }

        return result;
    }

    public boolean userExists(String username) {
        String sql = "SELECT COUNT(*) FROM UserTable WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
        }
        return false;
    }

    public boolean checkCredentials(String email, String password) {
        String sql = "SELECT * FROM UserTable WHERE email = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
        }
        return false;
    }

    public String extractUsernameFromEmail(String email) {

        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            return null;
        }

        String usernamePart = email.substring(0, atIndex);
        return usernamePart;
    }

    public boolean checkInstitutionExists() {
        String sql = "SELECT COUNT(*) FROM UserTable WHERE username LIKE 'a%'";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
        }
        return false;
    }

    public String getUserIdByEmail(String email) {
        String sql = "SELECT UserId FROM UserTable WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("UserId");
                return Integer.toString(userId);
            } else {
                return null;
            }

        } catch (SQLException e) {
            return null;
        }
    }

    public User getUserById(String id) {
        String sql = "SELECT * FROM UserTable WHERE UserId = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("UserId"),
                        rs.getString("UserName"),
                        rs.getString("Email"),
                        rs.getString("Password")
                );
                return user;
            } else {
                return null;
            }

        } catch (SQLException e) {
            return null;
        }
    }
}