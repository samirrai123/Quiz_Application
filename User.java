package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private int user_id;
    private String username;
    private String password;
    private String email;
    private String role;

    public boolean register(String username, String password, String email, String role) {
        String query = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseHandler.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, role);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int login(String username, String password) {
        String query = "SELECT user_id, role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseHandler.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.user_id = rs.getInt("user_id");
                this.role = rs.getString("role");
                return this.user_id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getRole() {
        return role;
    }
}