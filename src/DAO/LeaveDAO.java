package DAO;

import database.DatabaseConnection;
import model.LeaveRequest;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {

    public boolean addLeaveRequest(LeaveRequest leave) {
        ensureBalanceRow(leave.getEmployeeId(), leave.getStartDate().getYear());

        String sql = "INSERT INTO leave_requests " +
                "(employee_id, leave_type, start_date, end_date, reason, status) " +
                "VALUES (?, ?, ?, ?, ?, 'Pending')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, leave.getEmployeeId());
            ps.setString(2, leave.getLeaveType());
            ps.setDate(3, Date.valueOf(leave.getStartDate()));
            ps.setDate(4, Date.valueOf(leave.getEndDate()));
            ps.setString(5, leave.getReason());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not add leave request: " + e.getMessage(), e);
        }
    }

    public List<Object[]> getAllLeaveRequests() {
        String sql = "SELECT leave_id, employee_id, leave_type, start_date, end_date, " +
                "DATEDIFF(end_date, start_date) + 1 AS days, reason, status " +
                "FROM leave_requests ORDER BY leave_id DESC";
        List<Object[]> rows = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getInt("leave_id"), rs.getInt("employee_id"),
                        rs.getString("leave_type"), rs.getDate("start_date"),
                        rs.getDate("end_date"), rs.getInt("days"),
                        rs.getString("reason"), rs.getString("status")
                });
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load leave requests: " + e.getMessage(), e);
        }
    }

    public boolean updateStatus(int leaveId, String newStatus) {
        if (!newStatus.equals("Approved") && !newStatus.equals("Rejected")) {
            throw new IllegalArgumentException("Invalid leave status.");
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String select = "SELECT employee_id, leave_type, start_date, end_date, status " +
                        "FROM leave_requests WHERE leave_id = ? FOR UPDATE";
                int employeeId;
                String type;
                LocalDate start;
                LocalDate end;
                String currentStatus;

                try (PreparedStatement ps = conn.prepareStatement(select)) {
                    ps.setInt(1, leaveId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) return false;
                        employeeId = rs.getInt("employee_id");
                        type = rs.getString("leave_type");
                        start = rs.getDate("start_date").toLocalDate();
                        end = rs.getDate("end_date").toLocalDate();
                        currentStatus = rs.getString("status");
                    }
                }

                if (currentStatus.equals(newStatus)) return true;
                if (currentStatus.equals("Rejected") && newStatus.equals("Approved")) {
                    checkBalance(conn, employeeId, type, start, end);
                }
                if (currentStatus.equals("Approved") && newStatus.equals("Rejected")) {
                    // Rejection after approval returns the days to the balance.
                } else if (newStatus.equals("Approved")) {
                    checkBalance(conn, employeeId, type, start, end);
                    changeBalance(conn, employeeId, type, start.getYear(), -days(start, end));
                }
                if (currentStatus.equals("Approved") && newStatus.equals("Rejected")) {
                    changeBalance(conn, employeeId, type, start.getYear(), days(start, end));
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE leave_requests SET status = ? WHERE leave_id = ?")) {
                    ps.setString(1, newStatus);
                    ps.setInt(2, leaveId);
                    boolean updated = ps.executeUpdate() > 0;
                    conn.commit();
                    return updated;
                }
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof RuntimeException re) throw re;
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not update leave status: " + e.getMessage(), e);
        }
    }

    private void checkBalance(Connection conn, int employeeId, String type,
                              LocalDate start, LocalDate end) throws SQLException {
        int year = start.getYear();
        ensureBalanceRow(conn, employeeId, year);
        String column = balanceColumn(type);
        int requested = days(start, end);
        String sql = "SELECT " + column + " FROM leave_balances WHERE employee_id = ? AND leave_year = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || rs.getInt(1) < requested) {
                    throw new IllegalArgumentException("Insufficient " + type + " leave balance.");
                }
            }
        }
    }

    private void changeBalance(Connection conn, int employeeId, String type,
                               int year, int amount) throws SQLException {
        String column = balanceColumn(type);
        String sql = "UPDATE leave_balances SET " + column + " = " + column + " + ? " +
                "WHERE employee_id = ? AND leave_year = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setInt(2, employeeId);
            ps.setInt(3, year);
            ps.executeUpdate();
        }
    }

    private String balanceColumn(String type) {
        return switch (type) {
            case "Annual" -> "annual_remaining";
            case "Casual" -> "casual_remaining";
            case "Medical" -> "medical_remaining";
            default -> throw new IllegalArgumentException("Select a valid leave type.");
        };
    }

    private int days(LocalDate start, LocalDate end) {
        return (int) (java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1);
    }

    public Object[] getLeaveBalance(int employeeId, int year) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensureBalanceRow(conn, employeeId, year);
            String sql = "SELECT annual_remaining, casual_remaining, medical_remaining " +
                    "FROM leave_balances WHERE employee_id = ? AND leave_year = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, employeeId);
                ps.setInt(2, year);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Object[]{rs.getInt(1), rs.getInt(2), rs.getInt(3)};
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load leave balance: " + e.getMessage(), e);
        }
    }

    public boolean employeeExists(int employeeId) {
        String sql = "SELECT 1 FROM employees WHERE employee_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException("Could not check employee: " + e.getMessage(), e);
        }
    }

    private void ensureBalanceRow(int employeeId, int year) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensureBalanceRow(conn, employeeId, year);
        } catch (SQLException e) {
            throw new RuntimeException("Could not prepare leave balance: " + e.getMessage(), e);
        }
    }

    private void ensureBalanceRow(Connection conn, int employeeId, int year) throws SQLException {
        String sql = "INSERT IGNORE INTO leave_balances " +
                "(employee_id, leave_year, annual_remaining, casual_remaining, medical_remaining) " +
                "VALUES (?, ?, 14, 7, 7)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, year);
            ps.executeUpdate();
        }
    }
}
