package DAO;

import database.DatabaseConnection;
import model.Payroll;

import java.sql.*;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO {

    public Object[] getSalaryPreview(int employeeId) {
        String sql = "SELECT name, employee_type, basic_salary, allowance, " +
                "hours_worked, hourly_rate FROM employees WHERE employee_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                double basic = rs.getDouble("basic_salary");
                double allowance = rs.getDouble("allowance");
                int hours = rs.getInt("hours_worked");
                double rate = rs.getDouble("hourly_rate");
                double gross = rs.getString("employee_type").equals("Part Time")
                        ? hours * rate : basic + allowance;
                return new Object[]{rs.getString("name"), rs.getString("employee_type"),
                        basic, allowance, hours, rate, gross};
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load salary details: " + e.getMessage(), e);
        }
    }

    public boolean addPayroll(Payroll payroll) {
        String sql = "INSERT INTO payroll_records " +
                "(employee_id, payroll_month, basic_salary, allowance, deductions, gross_salary, net_salary, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, payroll.getEmployeeId());
            ps.setDate(2, Date.valueOf(payroll.getPayrollMonth().atDay(1)));
            ps.setDouble(3, payroll.getBasicSalary());
            ps.setDouble(4, payroll.getAllowance());
            ps.setDouble(5, payroll.getDeductions());
            ps.setDouble(6, payroll.getGrossSalary());
            ps.setDouble(7, payroll.getNetSalary());
            ps.setString(8, payroll.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not save payroll: " + e.getMessage(), e);
        }
    }

    public boolean updatePayroll(Payroll payroll) {
        String sql = "UPDATE payroll_records SET deductions=?, net_salary=?, status=? " +
                "WHERE payroll_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, payroll.getDeductions());
            ps.setDouble(2, payroll.getNetSalary());
            ps.setString(3, payroll.getStatus());
            ps.setInt(4, payroll.getPayrollId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not update payroll: " + e.getMessage(), e);
        }
    }

    public boolean deletePayroll(int payrollId) {
        String sql = "DELETE FROM payroll_records WHERE payroll_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, payrollId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete payroll: " + e.getMessage(), e);
        }
    }

    public List<Object[]> getAllPayroll() {
        String sql = "SELECT p.payroll_id, p.employee_id, e.name, p.payroll_month, " +
                "p.basic_salary, p.allowance, p.deductions, p.gross_salary, p.net_salary, p.status " +
                "FROM payroll_records p JOIN employees e ON p.employee_id=e.employee_id " +
                "ORDER BY p.payroll_month DESC, p.payroll_id DESC";
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new Object[]{rs.getInt("payroll_id"), rs.getInt("employee_id"),
                        rs.getString("name"), rs.getDate("payroll_month"),
                        rs.getDouble("basic_salary"), rs.getDouble("allowance"),
                        rs.getDouble("deductions"), rs.getDouble("gross_salary"),
                        rs.getDouble("net_salary"), rs.getString("status")});
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load payroll: " + e.getMessage(), e);
        }
    }

    public Payroll findPayroll(int payrollId) {
        String sql = "SELECT * FROM payroll_records WHERE payroll_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, payrollId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Payroll(rs.getInt("payroll_id"), rs.getInt("employee_id"),
                        YearMonth.from(rs.getDate("payroll_month").toLocalDate()),
                        rs.getDouble("basic_salary"), rs.getDouble("allowance"),
                        rs.getDouble("deductions"), rs.getDouble("gross_salary"),
                        rs.getDouble("net_salary"), rs.getString("status"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not find payroll: " + e.getMessage(), e);
        }
    }
}
