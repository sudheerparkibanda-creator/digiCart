-- DigiCart Order Service - Sample Data
-- Database: order_service_db
-- Create sample orders and order items

USE order_service_db;

-- Insert sample orders
INSERT IGNORE INTO orders (order_id, customer_id, cart_id, total_amount, currency, status, created_at)
VALUES
('order001', 'user001', 1, 224.97, 'USD', 'PENDING_PAYMENT', NOW()),
('order002', 'user002', 2, 44.97, 'USD', 'PENDING_PAYMENT', NOW());

-- Insert sample order items
INSERT IGNORE INTO order_items (order_id, product_code, quantity, unit_price, total_price, created_at)
VALUES
('order001', 'PROD001', 2, 99.99, 199.98, NOW()),
('order001', 'PROD002', 1, 24.99, 24.99, NOW()),
('order002', 'PROD003', 3, 14.99, 44.97, NOW());

-- Verify data insertion
SELECT 'Orders inserted:' as info, COUNT(*) as count FROM orders
UNION ALL
SELECT 'Order items inserted:', COUNT(*) FROM order_items;
