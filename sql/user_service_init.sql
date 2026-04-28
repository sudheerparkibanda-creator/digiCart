-- DigiCart User Service - Sample Data
-- Database: user_service_db
-- Create users and addresses for testing

USE user_service_db;

-- Create test users (bcrypt hashed passwords - plain text: admin123, password123, password123)
INSERT IGNORE INTO users (u_id, name, password, role, creation_time, verification_code)
VALUES
('admin001', 'admin@digicart.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Admin', NOW(), NULL),
('user001', 'john.doe@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Customer', NOW(), NULL),
('user002', 'jane.smith@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Customer', NOW(), NULL);

-- Verify data insertion
SELECT 'Users inserted:' as info, COUNT(*) as count FROM users;
