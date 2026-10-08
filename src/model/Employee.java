package model;

public abstract class Employee {
    private final int employeeId;
    private final String name;
    private final String address;
    private final String phone;
    private final String email;
    private final String department;
    private final String position;

    public Employee(int employeeId, String name, String address, String phone,
                    String email, String department, String position) {
        this.employeeId = employeeId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.department = department;
        this.position = position;
    }

    public int getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public String getPosition() { return position; }

    public abstract double calculateSalary();

    public void displayDetails() {
        System.out.println("Employee ID : " + employeeId);
        System.out.println("Name        : " + name);
        System.out.println("Address     : " + address);
        System.out.println("Phone       : " + phone);
        System.out.println("Email       : " + email);
        System.out.println("Department  : " + department);
        System.out.println("Position    : " + position);
    }
}
