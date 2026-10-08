package DAO;

import database.DatabaseConnection;
import model.PartTimeEmployee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartTimeEmployeeDAO {

    public boolean addPartTimeEmployee(PartTimeEmployee employee) {

        String sql = """
                INSERT INTO employees
                (employee_id, name, address, phone, email,
                 department, position, employee_type,
                 basic_salary, allowance,
                 hours_worked, hourly_rate)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employee.getEmployeeId());
            ps.setString(2, employee.getName());
            ps.setString(3, employee.getAddress());
            ps.setString(4, employee.getPhone());
            ps.setString(5, employee.getEmail());
            ps.setString(6, employee.getDepartment());
            ps.setString(7, employee.getPosition());
            ps.setString(8, "Part Time");
            ps.setDouble(9, 0);
            ps.setDouble(10, 0);
            ps.setInt(11, employee.getHoursWorked());
            ps.setDouble(12, employee.getHourlyRate());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not add part-time employee: " + e.getMessage()
            );
        }
    }

    public List<Object[]> getAllPartTimeEmployees() {

        List<Object[]> list = new ArrayList<>();

        String sql = """
                SELECT employee_id, name, address, phone, email,
                       department, position,
                       hours_worked, hourly_rate,
                       (hours_worked * hourly_rate) AS total_salary
                FROM employees
                WHERE employee_type = 'Part Time'
                ORDER BY employee_id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                list.add(new Object[]{
                        rs.getInt("employee_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("position"),
                        rs.getInt("hours_worked"),
                        rs.getDouble("hourly_rate"),
                        rs.getDouble("total_salary")
                });
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not load part-time employees: " + e.getMessage()
            );
        }
    }

    public boolean updatePartTimeEmployee(PartTimeEmployee employee) {

        String sql = """
                UPDATE employees
                SET name = ?,
                    address = ?,
                    phone = ?,
                    email = ?,
                    department = ?,
                    position = ?,
                    hours_worked = ?,
                    hourly_rate = ?
                WHERE employee_id = ?
                AND employee_type = 'Part Time'
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employee.getName());
            ps.setString(2, employee.getAddress());
            ps.setString(3, employee.getPhone());
            ps.setString(4, employee.getEmail());
            ps.setString(5, employee.getDepartment());
            ps.setString(6, employee.getPosition());
            ps.setInt(7, employee.getHoursWorked());
            ps.setDouble(8, employee.getHourlyRate());
            ps.setInt(9, employee.getEmployeeId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not update part-time employee: " + e.getMessage()
            );
        }
    }

    public boolean deletePartTimeEmployee(int employeeId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String[] cleanup = {
                        "DELETE FROM notifications WHERE employee_id = ?",
                        "DELETE FROM payroll_records WHERE employee_id = ?",
                        "DELETE FROM leave_requests WHERE employee_id = ?",
                        "DELETE FROM leave_balances WHERE employee_id = ?",
                        "DELETE FROM attendance WHERE employee_id = ?"
                };

                for (String sql : cleanup) {
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, employeeId);
                        ps.executeUpdate();
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM employees WHERE employee_id = ? AND employee_type = 'Part Time'")) {
                    ps.setInt(1, employeeId);
                    boolean deleted = ps.executeUpdate() > 0;
                    conn.commit();
                    return deleted;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not delete part-time employee: " + e.getMessage(), e
            );
        }
    }
}