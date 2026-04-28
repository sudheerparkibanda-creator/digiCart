-- DigiCart Stock Service - Sample Data
-- Database: stock_service_db
-- Create sample stock entries for products

USE stock_service_db;

-- Insert sample stock (product_id should match product code from product-service)
INSERT IGNORE INTO stocks (product_id, available_quantity, consumed_quantity, creation_time)
VALUES
('PROD001', 150, 0, NOW()),
('PROD002', 200, 0, NOW()),
('PROD003', 300, 0, NOW()),
('PROD004', 75, 0, NOW()),
('PROD005', 120, 0, NOW());

-- Verify data insertion
SELECT 'Stock entries inserted:' as info, COUNT(*) as count FROM stocks;
