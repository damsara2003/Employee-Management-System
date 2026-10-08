package model;

import java.time.YearMonth;

public class Payroll {
    private final int payrollId;
    private final int employeeId;
    private final YearMonth payrollMonth;
    private final double basicSalary;
    private final double allowance;
    private final double deductions;
    private final double grossSalary;
    private final double netSalary;
    private final String status;

    public Payroll(int payrollId, int employeeId, YearMonth payrollMonth,
                   double basicSalary, double allowance, double deductions,
                   double grossSalary, double netSalary, String status) {
        this.payrollId = payrollId;
        this.employeeId = employeeId;
        this.payrollMonth = payrollMonth;
        this.basicSalary = basicSalary;
        this.allowance = allowance;
        this.deductions = deductions;
        this.grossSalary = grossSalary;
        this.netSalary = netSalary;
        this.status = status;
    }

    public int getPayrollId() { return payrollId; }
    public int getEmployeeId() { return employeeId; }
    public YearMonth getPayrollMonth() { return payrollMonth; }
    public double getBasicSalary() { return basicSalary; }
    public double getAllowance() { return allowance; }
    public double getDeductions() { return deductions; }
    public double getGrossSalary() { return grossSalary; }
    public double getNetSalary() { return netSalary; }
    public String getStatus() { return status; }
}
