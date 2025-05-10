package model.daos;

import model.User;

import java.sql.*;
import java.util.logging.Logger;

import static model.repository.SQLServerConnection.*;

public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    public boolean registerUser(User user) {
        // System.out.println("registerUser Attempting to register user: " + user);

        if (user.getUserName().startsWith("a")) {
            if (checkInstitutionExists()) {
                // System.out.println("registerUser Institution already exists. Cannot register another institution user.");
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

            int rows = stmt.executeUpdate();
            result = rows > 0;
            // System.out.println("registerUser Rows inserted: " + rows);

        } catch (SQLException e) {
            System.out.println("registerUser SQL Exception: " + e.getMessage());
        }

        return result;
    }

    public boolean userExists(String username) {
        // System.out.println("userExists Checking if user exists: " + username);
        String sql = "SELECT COUNT(*) FROM UserTable WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                // System.out.println("userExists User exists: " + exists);
                return exists;
            }

        } catch (SQLException e) {
            System.out.println("userExists SQL Exception: " + e.getMessage());
        }
        return false;
    }

    public boolean checkCredentials(String email, String password) {
        // System.out.println("checkCredentials Verifying credentials for email: " + email);
        String sql = "SELECT * FROM UserTable WHERE email = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            boolean valid = rs.next();
            // System.out.println("checkCredentials Credentials valid: " + valid);
            return valid;

        } catch (SQLException e) {
            System.out.println("checkCredentials SQL Exception: " + e.getMessage());
        }
        return false;
    }

    public String extractUsernameFromEmail(String email) {
        // System.out.println("extractUsernameFromEmail Extracting username from: " + email);

        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            // System.out.println("extractUsernameFromEmail Invalid email format");
            return null;
        }

        String usernamePart = email.substring(0, atIndex);
        // System.out.println("extractUsernameFromEmail Extracted username: " + usernamePart);
        return usernamePart;
    }

    public boolean checkInstitutionExists() {
        // System.out.println("checkInstitutionExists Checking for institution users...");
        String sql = "SELECT COUNT(*) FROM UserTable WHERE username LIKE 'a%'";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                // System.out.println("checkInstitutionExists Institution exists: " + exists);
                return exists;
            }
        } catch (SQLException e) {
            System.out.println("checkInstitutionExists SQL Exception: " + e.getMessage());
        }
        return false;
    }

    public String getUserIdByEmail(String email) {
        // System.out.println("getUserIdByEmail Fetching user ID for email: " + email);
        String sql = "SELECT UserId FROM UserTable WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("UserId");
                // System.out.println("getUserIdByEmail Found user ID: " + userId);
                return Integer.toString(userId);
            } else {
                // System.out.println("getUserIdByEmail No user found with email: " + email);
                return null;
            }

        } catch (SQLException e) {
            System.out.println("getUserIdByEmail SQL Exception: " + e.getMessage());
            return null;
        }
    }

    public User getUserById(String id) {
        // System.out.println("getUserById Fetching user with ID: " + id);
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
                // System.out.println("getUserById User found: " + user);
                return user;
            } else {
                // System.out.println("getUserById No user found with ID: " + id);
                return null;
            }

        } catch (SQLException e) {
            System.out.println("getUserById SQL Exception: " + e.getMessage());
            return null;
        }
    }
}
