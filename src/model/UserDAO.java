package model;

import java.sql.*;

public class UserDAO {
    private Connection connection;
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=AulaSync;trustServerCertificate=true";
    private static final String USER = "SA";
    private static final String PASSWORD = "c_hup@meLa1234";

    /**
     * Constructor de las conexiones
     * @param connection
     */
    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Constructor para el registro de usuarios
     * @param user
     */
    public void registerUser(User user) {
        String sql = "INSERT INTO UserTable (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());

            stmt.executeUpdate();
            System.out.println("Usuario registrado exitosamente");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Verificación de que el usuario existe
     * @param username
     * @return
     */
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

    /**
     * Verificación de las credenciales
     * @param email
     * @param password
     * @return
     */
    public boolean verificarCredenciales(String email, String password) {
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
}
