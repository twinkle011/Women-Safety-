-- ====================================================================
-- SheSafe – Women's Safety & Emergency Support System
-- Phase 4: MySQL Database Schema (Updated)
-- Database: shesafe_db
-- ====================================================================

-- 1. Create Database
CREATE DATABASE IF NOT EXISTS shesafe_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE shesafe_db;

-- 2. Table: users
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 3. Table: emergency_contacts
CREATE TABLE IF NOT EXISTS emergency_contacts (
    contact_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    contact_name VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(15) NOT NULL,
    relationship VARCHAR(50) DEFAULT 'Guardian',
    CONSTRAINT fk_contacts_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 4. Table: incident_reports
CREATE TABLE IF NOT EXISTS incident_reports (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    incident_type VARCHAR(100) NOT NULL,
    location VARCHAR(255) NOT NULL,
    incident_date DATE NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reports_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 5. Table: sos_alerts [NEW IN PHASE 4]
-- Stores every triggered SOS event with timestamp, location, and final delivery status
CREATE TABLE IF NOT EXISTS sos_alerts (
    alert_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    alert_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    location VARCHAR(255) NULL,
    location_type VARCHAR(50) DEFAULT 'NONE', -- 'IP_ESTIMATED', 'MANUAL_ENTRY', 'NONE'
    map_link VARCHAR(500) NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'TRIGGERED', -- 'DELIVERED', 'PARTIAL', 'FAILED', 'NO_CONTACTS'
    contacts_notified INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alerts_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 6. Table: sos_notifications [NEW IN PHASE 4]
-- Tracks delivery status and error diagnostics per emergency contact recipient
CREATE TABLE IF NOT EXISTS sos_notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    alert_id INT NOT NULL,
    contact_id INT NULL,
    recipient_name VARCHAR(100) NOT NULL,
    recipient_target VARCHAR(100) NOT NULL, -- Email address or phone
    channel VARCHAR(30) NOT NULL,           -- 'EMAIL', 'SMS_EXTERNAL', 'SYSTEM_LOG'
    delivery_status VARCHAR(30) NOT NULL,   -- 'DELIVERED', 'FAILED', 'TEST_MODE'
    status_details VARCHAR(255) NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_alert
        FOREIGN KEY (alert_id) REFERENCES sos_alerts(alert_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ====================================================================
-- Sample Demonstration Seed Data
-- Default user password is: password123
-- ====================================================================

-- Insert Sample User (Priya Sharma)
INSERT INTO users (user_id, full_name, email, phone, password_hash)
VALUES (
    1,
    'Priya Sharma',
    'priya@shesafe.com',
    '9876543210',
    '1000:c3VwZXJzZWNyZXRzYWx0:kU0p31+r12q/6/E3Vv/uDvhJ1jZ2g1dE9O2eWf3g7i0='
) ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- Insert Sample Emergency Contacts for User 1
INSERT INTO emergency_contacts (contact_id, user_id, contact_name, contact_phone, relationship)
VALUES
    (1, 1, 'Sunita Sharma (Mother)', '9876543211', 'Parent / Guardian'),
    (2, 1, 'Rajesh Sharma (Father)', '9876543212', 'Parent / Guardian'),
    (3, 1, 'Women Helpline', '1091', 'Police / Helpline'),
    (4, 1, 'Ananya Verma (Friend)', '9811122334', 'Close Friend')
ON DUPLICATE KEY UPDATE contact_name=VALUES(contact_name);

-- Insert Sample SOS Alert for User 1
INSERT INTO sos_alerts (alert_id, user_id, alert_time, location, location_type, map_link, status, contacts_notified)
VALUES (
    1,
    1,
    NOW() - INTERVAL 1 DAY,
    'Connaught Place, Central Delhi [IP_ESTIMATED]',
    'IP_ESTIMATED',
    'https://maps.google.com/?q=28.6304,77.2177',
    'DELIVERED',
    3
) ON DUPLICATE KEY UPDATE status=VALUES(status);

-- Insert Notification Log for Alert 1
INSERT INTO sos_notifications (alert_id, contact_id, recipient_name, recipient_target, channel, delivery_status, status_details)
VALUES
    (1, 1, 'Sunita Sharma (Mother)', 'sunita.sharma@example.com', 'EMAIL', 'DELIVERED', 'Email delivered successfully via SMTP'),
    (1, 4, 'Ananya Verma (Friend)', 'ananya.verma@example.com', 'EMAIL', 'DELIVERED', 'Email delivered successfully via SMTP')
ON DUPLICATE KEY UPDATE delivery_status=VALUES(delivery_status);
