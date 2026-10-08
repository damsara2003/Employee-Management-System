package DAO;

import database.DatabaseConnection;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public User login(String username, String password) {
        String sql = "SELECT user_id, username, role, employee_id " +
                "FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int employeeId = rs.getInt("employee_id");
                    Integer mappedEmployeeId = rs.wasNull() ? null : employeeId;
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("role"),
                            mappedEmployeeId
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Login database error: " + e.getMessage(), e);
        }
        return null;
    }
}
