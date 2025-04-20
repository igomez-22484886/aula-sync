package model.daos;

import model.User;
import java.sql.*;
import static model.repository.SQLServerConnection.*;

public class UserDAO {
    public boolean registerUser(User user) {
        System.out.println("registerUser: Starting registration process...");

        if (user.getUserName().startsWith("a")) {
            System.out.println("registerUser: Detected institution-type user, checking for existing institution...");
            if (checkInstitutionExists()) {
                System.out.println("registerUser: Registration denied - An institution already exists.");
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
            System.out.println("registerUser: User registered successfully.");
            result = true;

        } catch (SQLException e) {
            System.out.println("registerUser: Error during registration.");
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return false;
    }

    public String extractUsernameFromEmail(String email) {
        System.out.println("extractUsernameFromEmail: Extracting username from email...");

        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            System.out.println("extractUsernameFromEmail: Error - Invalid email format.");
            return null;
        }

        String usernamePart = email.substring(0, atIndex);
        System.out.println("extractUsernameFromEmail: Extracted username = " + usernamePart);
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
            e.printStackTrace();
        }
        return false;
    }

    public Integer getUserIdByEmail(String email) {
        String sql = "SELECT UserId FROM UserTable WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Integer userId = rs.getInt("UserId");
                System.out.println("getUserIdByEmail: User found, UserId: " + userId);
                return userId;
            } else {
                System.out.println("getUserIdByEmail: No user found with email: " + email);
                return null;
            }

        } catch (SQLException e) {
            System.out.println("getUserIdByEmail: SQLException occurred: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
