package model;

public class User {

    private final int userId;
    private final String username;
    private final String role;
    private final Integer employeeId;

    public User(int userId, String username, String role) {
        this(userId, username, role, null);
    }

    public User(int userId, String username, String role, Integer employeeId) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.employeeId = employeeId;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public Integer getEmployeeId() { return employeeId; }
}
