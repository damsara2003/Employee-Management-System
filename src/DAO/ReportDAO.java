package DAO;

import database.DatabaseConnection;
import model.ReportData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public ReportData getReport(String type) {
        return switch (type) {
            case "Employees" -> employees();
            case "Attendance" -> attendance();
            case "Leave" -> leave();
            case "Payroll" -> payroll();
            default -> throw new IllegalArgumentException("Unknown report type.");
        };
    }

    private ReportData employees() {
        String sql = "SELECT employee_id,name,department,position,employee_type,basic_salary,allowance " +
                "FROM employees ORDER BY employee_id";
        return query("Employee Report",
                new String[]{"ID","Name","Department","Position","Type","Basic Salary","Allowance"},
                sql, rs -> new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),
                        rs.getString(5),rs.getDouble(6),rs.getDouble(7)});
    }

    private ReportData attendance() {
        String sql = "SELECT a.attendance_id,a.employee_id,e.name,a.date,a.check_in,a.check_out,a.status " +
                "FROM attendance a JOIN employees e ON e.employee_id=a.employee_id " +
                "ORDER BY a.date DESC";
        return query("Attendance Report",
                new String[]{"Attendance ID","Employee ID","Name","Date","Check In","Check Out","Status"},
                sql, rs -> new Object[]{rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),
                        rs.getString(5),rs.getString(6),rs.getString(7)});
    }

    private ReportData leave() {
        String sql = "SELECT lr.leave_id,lr.employee_id,e.name,lr.leave_type,lr.start_date,lr.end_date," +
                "DATEDIFF(lr.end_date,lr.start_date)+1,lr.status FROM leave_requests lr " +
                "JOIN employees e ON e.employee_id=lr.employee_id ORDER BY lr.leave_id DESC";
        return query("Leave Report",
                new String[]{"Leave ID","Employee ID","Name","Type","Start","End","Days","Status"},
                sql, rs -> new Object[]{rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),
                        rs.getDate(5),rs.getDate(6),rs.getInt(7),rs.getString(8)});
    }

    private ReportData payroll() {
        String sql = "SELECT p.payroll_id,p.employee_id,e.name,p.payroll_month,p.basic_salary,p.allowance," +
                "p.deductions,p.gross_salary,p.net_salary,p.status FROM payroll_records p " +
                "JOIN employees e ON e.employee_id=p.employee_id ORDER BY p.payroll_month DESC";
        return query("Payroll Report",
                new String[]{"Payroll ID","Employee ID","Name","Month","Basic","Allowance","Deductions","Gross","Net","Status"},
                sql, rs -> new Object[]{rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getDate(4),rs.getDouble(5),
                        rs.getDouble(6),rs.getDouble(7),rs.getDouble(8),rs.getDouble(9),rs.getString(10)});
    }

    @FunctionalInterface
    private interface RowMapper { Object[] map(ResultSet rs) throws SQLException; }

    private ReportData query(String title, String[] headers, String sql, RowMapper mapper) {
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rows.add(mapper.map(rs));
            return new ReportData(title, headers, rows);
        } catch (SQLException e) {
            throw new RuntimeException("Could not load report: " + e.getMessage(), e);
        }
    }
}
