package DAO;

import database.DatabaseConnection;

import java.sql.*;

public class EmployeeProfileDAO {

    public Object[] getProfile(int employeeId) {
        String sql = "SELECT employee_id, name, address, phone, email, department, " +
                "position, employee_type, basic_salary, allowance, photo " +
                "FROM employees WHERE employee_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Object[]{
                        rs.getInt("employee_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getString("position"),
                        rs.getString("employee_type"),
                        rs.getDouble("basic_salary"),
                        rs.getDouble("allowance"),
                        rs.getBytes("photo")
                };
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load employee profile: " + e.getMessage(), e);
        }
    }

    public boolean savePhoto(int employeeId, byte[] photo) {
        String sql = "UPDATE employees SET photo=? WHERE employee_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (photo == null) ps.setNull(1, Types.LONGVARBINARY);
            else ps.setBytes(1, photo);
            ps.setInt(2, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not save employee photo: " + e.getMessage(), e);
        }
    }
}
