package model;

import java.time.LocalDate;

public class LeaveRequest {
    private final int leaveId;
    private final int employeeId;
    private final String leaveType;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String reason;
    private final String status;

    public LeaveRequest(int leaveId, int employeeId, String leaveType,
                        LocalDate startDate, LocalDate endDate,
                        String reason, String status) {
        this.leaveId = leaveId;
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
    }

    public int getLeaveId() { return leaveId; }
    public int getEmployeeId() { return employeeId; }
    public String getLeaveType() { return leaveType; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
}
