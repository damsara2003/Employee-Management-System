USE employee_db;

-- 1. Employee Profile + Photo
ALTER TABLE employees ADD COLUMN IF NOT EXISTS photo LONGBLOB NULL;

-- 2. Role-Based Access: link a login account to an employee record.
ALTER TABLE users ADD COLUMN IF NOT EXISTS employee_id INT NULL;

-- 3. Notifications
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

-- Example HR login (run once if you want an HR account):
-- INSERT INTO users (username,password,role,employee_id)
-- VALUES ('hr','hr123','HR',NULL);

-- Example employee login. Replace 1 with a real employee_id:
-- INSERT INTO users (username,password,role,employee_id)
-- VALUES ('employee1','emp123','EMPLOYEE',1);
