CREATE DATABASE IF NOT EXISTS employee_db;
USE employee_db;

CREATE TABLE IF NOT EXISTS employees (
    employee_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(200) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    position VARCHAR(100) NOT NULL,
    employee_type VARCHAR(50) NOT NULL,
    basic_salary DOUBLE NOT NULL DEFAULT 0,
    allowance DOUBLE NOT NULL DEFAULT 0,
    hours_worked INT NOT NULL DEFAULT 0,
    hourly_rate DOUBLE NOT NULL DEFAULT 0
);

-- Safe migration for an existing database used by the current project.
ALTER TABLE employees ADD COLUMN IF NOT EXISTS hours_worked INT NOT NULL DEFAULT 0;
ALTER TABLE employees ADD COLUMN IF NOT EXISTS hourly_rate DOUBLE NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS attendance (
    attendance_id INT PRIMARY KEY,
    employee_id INT NOT NULL,
    date VARCHAR(20) NOT NULL,
    check_in VARCHAR(10),
    check_out VARCHAR(10),
    status VARCHAR(30) NOT NULL,
    CONSTRAINT fk_attendance_employee
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL
);

INSERT IGNORE INTO users (username, password, role)
VALUES ('admin', 'admin123', 'ADMIN');

-- =============================
-- LEAVE MANAGEMENT
-- =============================
CREATE TABLE IF NOT EXISTS leave_balances (
    employee_id INT NOT NULL,
    leave_year INT NOT NULL,
    annual_remaining INT NOT NULL DEFAULT 14,
    casual_remaining INT NOT NULL DEFAULT 7,
    medical_remaining INT NOT NULL DEFAULT 7,
    PRIMARY KEY (employee_id, leave_year),
    CONSTRAINT fk_leave_balance_employee
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS leave_requests (
    leave_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    leave_type VARCHAR(30) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(500) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'Pending',
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_leave_employee
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON DELETE CASCADE
);

-- =============================
-- PAYROLL MANAGEMENT
-- =============================
CREATE TABLE IF NOT EXISTS payroll_records (
    payroll_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    payroll_month DATE NOT NULL,
    basic_salary DOUBLE NOT NULL DEFAULT 0,
    allowance DOUBLE NOT NULL DEFAULT 0,
    deductions DOUBLE NOT NULL DEFAULT 0,
    gross_salary DOUBLE NOT NULL DEFAULT 0,
    net_salary DOUBLE NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'Generated',
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payroll_employee
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON DELETE CASCADE,
    UNIQUE KEY uq_employee_payroll_month (employee_id, payroll_month)
);

-- =====================================================
-- NEXT 5 FEATURES
-- 1) Employee Profile + Photo
-- 2) Attendance Summary
-- 3) Notifications
-- 4) Reports (CSV/PDF from Java)
-- 5) Role-Based Access
-- =====================================================

ALTER TABLE employees ADD COLUMN IF NOT EXISTS photo LONGBLOB NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS employee_id INT NULL;

-- Optional FK for user -> employee mapping. If your MySQL setup already
-- contains this FK, do not run this line a second time.
-- ALTER TABLE users ADD CONSTRAINT fk_user_employee
--     FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
--     ON DELETE SET NULL;

CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NULL,
    title VARCHAR(150) NOT NULL,
    message VARCHAR(500) NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    notification_key VARCHAR(150) NOT NULL UNIQUE,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_employee
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
        ON DELETE CASCADE
);

-- Example role accounts can be created manually after an employee exists:
-- INSERT INTO users (username,password,role,employee_id)
-- VALUES ('hr','hr123','HR',NULL);
-- INSERT INTO users (username,password,role,employee_id)
-- VALUES ('employee1','emp123','EMPLOYEE',1);
