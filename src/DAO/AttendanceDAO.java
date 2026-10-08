package DAO;

import database.DatabaseConnection;
import model.Attendance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public boolean addAttendance(Attendance attendance) {
        String sql = "INSERT INTO attendance " +
                "(attendance_id, employee_id, date, check_in, check_out, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, attendance.getAttendanceId());
            statement.setInt(2, attendance.getEmployeeId());
            statement.setString(3, attendance.getDate());
            statement.setString(4, attendance.getCheckIn());
            statement.setString(5, attendance.getCheckOut());
            statement.setString(6, attendance.getStatus());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Could not save attendance: " + e.getMessage(), e);
        }
    }

    public List<Object[]> getAllAttendance() {
        String sql = "SELECT attendance_id, employee_id, date, check_in, check_out, status " +
                "FROM attendance ORDER BY date DESC, attendance_id DESC";

        List<Object[]> rows = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {
                rows.add(new Object[]{
                        result.getInt("attendance_id"),
                        result.getInt("employee_id"),
                        result.getString("date"),
                        result.getString("check_in"),
                        result.getString("check_out"),
                        result.getString("status")
                });
            }
            return rows;

        } catch (SQLException e) {
            throw new RuntimeException("Could not load attendance: " + e.getMessage(), e);
        }
    }
}
