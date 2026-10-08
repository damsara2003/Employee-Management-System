NEXT 5 FEATURES ADDED
=====================

1. Employee Profile + Photo
   - Employee Profile menu
   - Search complete employee details by ID
   - Select and save employee photo to MySQL

2. Attendance Summary
   - Monthly Present / Absent / Late / Leave counts
   - Month format: YYYY-MM

3. Notifications
   - Automatically detects pending leave requests
   - Automatically detects Late attendance records
   - Mark notifications as read

4. Reports
   - Employee report
   - Attendance report
   - Leave report
   - Payroll report
   - Export CSV
   - Export PDF (built-in lightweight PDF writer; no extra library required)

5. Role-Based Access
   - ADMIN: full system access
   - HR: employee, attendance, leave, payroll, profile, summary, notifications and reports
   - EMPLOYEE: dashboard, own profile, own attendance summary and own notifications
   - Map an employee login using users.employee_id

DATABASE
========
Run database_setup.sql in MySQL after backing up your existing database.
It adds the employee photo column, user employee_id mapping and notifications table.

IMPORTANT
=========
The application uses the same database connection settings already in the project.
If your MySQL root password is different, update:
src/database/DatabaseConnection.java

Default existing admin login:
username: admin
password: admin123
role: ADMIN
