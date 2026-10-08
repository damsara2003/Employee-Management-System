package DAO;

import database.DatabaseConnection;
import model.FullTimeEmployee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public boolean addEmployee(FullTimeEmployee employee) {
        String sql = "INSERT INTO employees " +
                "(employee_id, name, address, phone, email, department, position, " +
                "employee_type, basic_salary, allowance) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setEmployeeParameters(statement, employee);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Could not add employee: " + e.getMessage(), e);
        }
    }

    public List<Object[]> getAllEmployees() {
        String sql = "SELECT employee_id, name, address, phone, email, department, " +
                "position, employee_type, basic_salary, allowance " +
                "FROM employees ORDER BY employee_id";

        List<Object[]> rows = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {
                rows.add(new Object[]{
                        result.getInt("employee_id"),
                        result.getString("name"),
                        result.getString("department"),
                        result.getString("position"),
                        result.getString("employee_type"),
                        result.getDouble("basic_salary"),
                        result.getDouble("allowance"),
                        result.getDouble("basic_salary") + result.getDouble("allowance")
                });
            }
            return rows;

        } catch (SQLException e) {
            throw new RuntimeException("Could not load employees: " + e.getMessage(), e);
        }
    }

    public Object[] getEmployeeById(int employeeId) {
        String sql = "SELECT employee_id, name, address, phone, email, department, " +
                "position, employee_type, basic_salary, allowance " +
                "FROM employees WHERE employee_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, employeeId);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) return null;

                return new Object[]{
                        result.getInt("employee_id"),
                        result.getString("name"),
                        result.getString("address"),
                        result.getString("phone"),
                        result.getString("email"),
                        result.getString("department"),
                        result.getString("position"),
                        result.getString("employee_type"),
                        result.getDouble("basic_salary"),
                        result.getDouble("allowance")
                };
            }

        } catch (SQLException e) {
            throw new RuntimeException("Could not find employee: " + e.getMessage(), e);
        }
    }

    public boolean updateEmployee(FullTimeEmployee employee) {
        String sql = "UPDATE employees SET name=?, address=?, phone=?, email=?, " +
                "department=?, position=?, basic_salary=?, allowance=? " +
                "WHERE employee_id=?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, employee.getName());
            statement.setString(2, employee.getAddress());
            statement.setString(3, employee.getPhone());
            statement.setString(4, employee.getEmail());
            statement.setString(5, employee.getDepartment());
            statement.setString(6, employee.getPosition());
            statement.setDouble(7, employee.getBasicSalary());
            statement.setDouble(8, employee.getAllowance());
            statement.setInt(9, employee.getEmployeeId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Could not update employee: " + e.getMessage(), e);
        }
    }

    public boolean deleteEmployee(int employeeId) {
        // Delete dependent records first so the operation also works with
        // older databases whose foreign keys were not created with ON DELETE CASCADE.
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String[] cleanup = {
                        "DELETE FROM notifications WHERE employee_id = ?",
                        "DELETE FROM payroll_records WHERE employee_id = ?",
                        "DELETE FROM leave_requests WHERE employee_id = ?",
                        "DELETE FROM leave_balances WHERE employee_id = ?",
                        "DELETE FROM attendance WHERE employee_id = ?"
                };

                for (String sql : cleanup) {
                    try (PreparedStatement ps = connection.prepareStatement(sql)) {
                        ps.setInt(1, employeeId);
                        ps.executeUpdate();
                    }
                }

                try (PreparedStatement ps = connection.prepareStatement(
                        "DELETE FROM employees WHERE employee_id = ?")) {
                    ps.setInt(1, employeeId);
                    boolean deleted = ps.executeUpdate() > 0;
                    connection.commit();
                    return deleted;
                }
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete employee: " + e.getMessage(), e);
        }
    }

    public int getEmployeeCount() {
        String sql = "SELECT COUNT(*) FROM employees";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Could not count employees: " + e.getMessage(), e);
        }
    }

    public double getTotalSalary() {
        String sql = "SELECT COALESCE(SUM(basic_salary + allowance), 0) FROM employees";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            result.next();
            return result.getDouble(1);
        } catch (SQLException e) {
            throw new RuntimeException("Could not calculate salary total: " + e.getMessage(), e);
        }
    }

    private void setEmployeeParameters(PreparedStatement statement,
                                       FullTimeEmployee employee) throws SQLException {
        statement.setInt(1, employee.getEmployeeId());
        statement.setString(2, employee.getName());
        statement.setString(3, employee.getAddress());
        statement.setString(4, employee.getPhone());
        statement.setString(5, employee.getEmail());
        statement.setString(6, employee.getDepartment());
        statement.setString(7, employee.getPosition());
        statement.setString(8, "Full Time");
        statement.setDouble(9, employee.getBasicSalary());
        statement.setDouble(10, employee.getAllowance());
    }
}
