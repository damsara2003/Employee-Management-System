package model;

public class FullTimeEmployee extends Employee {
    private final double basicSalary;
    private final double allowance;

    public FullTimeEmployee(int employeeId, String name, String address,
                            String phone, String email, String department,
                            String position, double basicSalary, double allowance) {
        super(employeeId, name, address, phone, email, department, position);
        this.basicSalary = basicSalary;
        this.allowance = allowance;
    }

    public double getBasicSalary() { return basicSalary; }
    public double getAllowance() { return allowance; }

    @Override
    public double calculateSalary() {
        return basicSalary + allowance;
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Employee Type: Full Time");
        System.out.println("Basic Salary : " + basicSalary);
        System.out.println("Allowance    : " + allowance);
        System.out.println("Net Salary   : " + calculateSalary());
    }
}
