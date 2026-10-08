package DAO;

import database.DatabaseConnection;

import java.sql.*;

public class AttendanceSummaryDAO {

    public Object[] getMonthlySummary(int employeeId, int year, int month) {
        String sql = "SELECT " +
                "SUM(CASE WHEN status='Present' THEN 1 ELSE 0 END) AS present_count, " +
                "SUM(CASE WHEN status='Absent' THEN 1 ELSE 0 END) AS absent_count, " +
                "SUM(CASE WHEN status='Late' THEN 1 ELSE 0 END) AS late_count, " +
                "SUM(CASE WHEN status='Leave' THEN 1 ELSE 0 END) AS leave_count " +
                "FROM attendance WHERE employee_id=? " +
                "AND STR_TO_DATE(date,'%Y-%m-%d') >= ? " +
                "AND STR_TO_DATE(date,'%Y-%m-%d') < ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setDate(2, Date.valueOf(java.time.LocalDate.of(year, month, 1)));
            ps.setDate(3, Date.valueOf(java.time.LocalDate.of(year, month, 1).plusMonths(1)));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                            rs.getInt("present_count"),
                            rs.getInt("absent_count"),
                            rs.getInt("late_count"),
                            rs.getInt("leave_count")
                    };
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load attendance summary: " + e.getMessage(), e);
        }
        return new Object[]{0, 0, 0, 0};
    }
}
