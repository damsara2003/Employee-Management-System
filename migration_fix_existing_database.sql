USE employee_db;

-- Compatibility migration for databases created by older versions of the project.
-- Safe to run on the current database. It only changes columns when the old
-- column exists and the new column does not.

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'leave_balances'
          AND COLUMN_NAME = 'annual_leave'
    ) AND NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'leave_balances'
          AND COLUMN_NAME = 'annual_remaining'
    ),
    'ALTER TABLE leave_balances CHANGE COLUMN annual_leave annual_remaining INT NOT NULL DEFAULT 14',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'leave_balances'
          AND COLUMN_NAME = 'casual_leave'
    ) AND NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'leave_balances'
          AND COLUMN_NAME = 'casual_remaining'
    ),
    'ALTER TABLE leave_balances CHANGE COLUMN casual_leave casual_remaining INT NOT NULL DEFAULT 7',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'leave_balances'
          AND COLUMN_NAME = 'medical_leave'
    ) AND NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'leave_balances'
          AND COLUMN_NAME = 'medical_remaining'
    ),
    'ALTER TABLE leave_balances CHANGE COLUMN medical_leave medical_remaining INT NOT NULL DEFAULT 7',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Required notification key for automatic notification de-duplication.
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS notification_key VARCHAR(150) NULL;

UPDATE notifications
SET notification_key = CONCAT('legacy:', notification_id)
WHERE notification_key IS NULL OR notification_key = '';

SET @sql = IF(
    NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'notifications'
          AND INDEX_NAME = 'uq_notifications_key'
    ),
    'ALTER TABLE notifications ADD UNIQUE KEY uq_notifications_key (notification_key)',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Required employee payroll columns.
ALTER TABLE employees ADD COLUMN IF NOT EXISTS hours_worked INT NOT NULL DEFAULT 0;
ALTER TABLE employees ADD COLUMN IF NOT EXISTS hourly_rate DOUBLE NOT NULL DEFAULT 0;
ALTER TABLE employees ADD COLUMN IF NOT EXISTS photo LONGBLOB NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS employee_id INT NULL;

SELECT 'Employee Management System compatibility migration completed.' AS Result;
