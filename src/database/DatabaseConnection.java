package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL =
            "jdbc:mysql://localhost:3306/employee_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";

    // Keep the same password you already use in your current project.
    private static final String PASSWORD = "Damsara@23";

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
