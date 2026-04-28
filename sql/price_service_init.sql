-- DigiCart Price Service - Sample Data
-- Database: price_service_db
-- Create sample prices for products

USE price_service_db;

-- Insert sample prices (matching product codes)
INSERT IGNORE INTO price_rows (product_code, price, unit, creation_time)
VALUES
('PROD001', 99.99, 'USD', NOW()),
('PROD002', 24.99, 'USD', NOW()),
('PROD003', 14.99, 'USD', NOW()),
('PROD004', 49.99, 'USD', NOW()),
('PROD005', 29.99, 'USD', NOW());

-- Verify data insertion
SELECT 'Prices inserted:' as info, COUNT(*) as count FROM price_rows;
