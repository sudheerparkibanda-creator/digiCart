-- DigiCart Address Service - Sample Data
-- Database: address_service_db
-- Create sample addresses for users

USE address_service_db;

-- Insert sample addresses
INSERT IGNORE INTO addresses (customer_id, street, city, state, zip_code, country, creation_time)
VALUES
('user001', '123 Main Street', 'New York', 'NY', '10001', 'USA', NOW()),
('user001', '456 Oak Avenue', 'Los Angeles', 'CA', '90210', 'USA', NOW()),
('user002', '789 Pine Road', 'Chicago', 'IL', '60601', 'USA', NOW());

-- Verify data insertion
SELECT 'Addresses inserted:' as info, COUNT(*) as count FROM addresses;
