package model;

public class Attendance {
    private final int attendanceId;
    private final int employeeId;
    private final String date;
    private final String checkIn;
    private final String checkOut;
    private final String status;

    public Attendance(int attendanceId, int employeeId, String date,
                      String checkIn, String checkOut, String status) {
        this.attendanceId = attendanceId;
        this.employeeId = employeeId;
        this.date = date;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
    }

    public int getAttendanceId() { return attendanceId; }
    public int getEmployeeId() { return employeeId; }
    public String getDate() { return date; }
    public String getCheckIn() { return checkIn; }
    public String getCheckOut() { return checkOut; }
    public String getStatus() { return status; }
}
