package DAO;

import database.DatabaseConnection;
import model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public void generateAutomaticNotifications() {
        String pendingSql = "SELECT lr.leave_id, lr.employee_id, e.name " +
                "FROM leave_requests lr JOIN employees e ON e.employee_id=lr.employee_id " +
                "WHERE lr.status='Pending'";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(pendingSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int leaveId = rs.getInt("leave_id");
                    int employeeId = rs.getInt("employee_id");
                    String name = rs.getString("name");
                    addIfMissing(conn, employeeId, "Pending Leave Request",
                            "Leave request #" + leaveId + " from " + name + " is pending.",
                            "LEAVE", "leave:" + leaveId);
                }
            }

            String lateSql = "SELECT employee_id, date, COUNT(*) AS late_count " +
                    "FROM attendance WHERE status='Late' GROUP BY employee_id, date";
            try (PreparedStatement ps = conn.prepareStatement(lateSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int employeeId = rs.getInt("employee_id");
                    String date = rs.getString("date");
                    addIfMissing(conn, employeeId, "Late Attendance",
                            "Employee ID " + employeeId + " was marked Late on " + date + ".",
                            "ATTENDANCE", "late:" + employeeId + ":" + date);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not generate notifications: " + e.getMessage(), e);
        }
    }

    private void addIfMissing(Connection conn, int employeeId, String title, String message,
                              String type, String uniqueKey) throws SQLException {
        String check = "SELECT notification_id FROM notifications WHERE notification_key=?";
        try (PreparedStatement ps = conn.prepareStatement(check)) {
            ps.setString(1, uniqueKey);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return;
            }
        }
        String insert = "INSERT INTO notifications " +
                "(employee_id,title,message,notification_type,notification_key) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setInt(1, employeeId);
            ps.setString(2, title);
            ps.setString(3, message);
            ps.setString(4, type);
            ps.setString(5, uniqueKey);
            ps.executeUpdate();
        }
    }

    public List<Notification> getNotifications(Integer employeeId, boolean unreadOnly) {
        StringBuilder sql = new StringBuilder(
                "SELECT notification_id, employee_id, title, message, notification_type, is_read, created_at " +
                "FROM notifications WHERE 1=1");
        if (employeeId != null) sql.append(" AND employee_id=?");
        if (unreadOnly) sql.append(" AND is_read=false");
        sql.append(" ORDER BY created_at DESC");

        List<Notification> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int i = 1;
            if (employeeId != null) ps.setInt(i++, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("employee_id");
                    Integer mapped = rs.wasNull() ? null : id;
                    list.add(new Notification(
                            rs.getInt("notification_id"), mapped,
                            rs.getString("title"), rs.getString("message"),
                            rs.getString("notification_type"), rs.getBoolean("is_read"),
                            rs.getTimestamp("created_at").toString()
                    ));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load notifications: " + e.getMessage(), e);
        }
    }

    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read=true WHERE notification_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not mark notification as read: " + e.getMessage(), e);
        }
    }
}
