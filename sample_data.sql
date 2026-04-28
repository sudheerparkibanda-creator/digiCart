-- DigiCart Sample Data
-- Run this script to populate your MySQL database with test data

-- Switch to digicart database (create if not exists)
CREATE DATABASE IF NOT EXISTS digicart;
USE digicart;

-- Sample Products
INSERT INTO products (code, description, creation_time) VALUES
('PROD001', 'Wireless Bluetooth Headphones', NOW()),
('PROD002', 'Smartphone Case - Black', NOW()),
('PROD003', 'USB-C Charging Cable', NOW()),
('PROD004', 'Laptop Stand', NOW()),
('PROD005', 'Wireless Mouse', NOW());

-- Product Features
INSERT INTO product_features (product_id, feature_key, feature_value) VALUES
(1, 'color', 'Black'),
(1, 'battery_life', '30 hours'),
(1, 'connectivity', 'Bluetooth 5.0'),
(1, 'weight', '250g'),
(2, 'color', 'Black'),
(2, 'material', 'TPU'),
(2, 'compatibility', 'iPhone 12-15'),
(3, 'length', '6 feet'),
(3, 'connector_type', 'USB-C'),
(3, 'data_transfer', 'USB 3.0'),
(4, 'material', 'Aluminum'),
(4, 'adjustable', 'Yes'),
(4, 'max_height', '6 inches'),
(5, 'color', 'White'),
(5, 'connectivity', '2.4GHz Wireless'),
(5, 'battery_life', '12 months');

-- Sample Prices
INSERT INTO price_rows (product_code, price, currency) VALUES
('PROD001', 99.99, 'USD'),
('PROD002', 24.99, 'USD'),
('PROD003', 14.99, 'USD'),
('PROD004', 49.99, 'USD'),
('PROD005', 29.99, 'USD');

-- Sample Stock
INSERT INTO stocks (product_id, quantity, location) VALUES
('PROD001', 150, 'Warehouse A'),
('PROD002', 200, 'Warehouse A'),
('PROD003', 300, 'Warehouse B'),
('PROD004', 75, 'Warehouse A'),
('PROD005', 120, 'Warehouse B');

-- Sample Users (for user-service database)
-- Passwords are bcrypt hashed. Use 'admin123' for admin, 'password123' for users
INSERT INTO users (username, password, role, enabled) VALUES
('john.doe@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', true),
('admin@digicart.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', true),
('jane.smith@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', true);

-- Sample Addresses
INSERT INTO addresses (customer_id, street, city, state, zip_code, country) VALUES
('user123', '123 Main Street', 'New York', 'NY', '10001', 'USA'),
('user123', '456 Oak Avenue', 'Los Angeles', 'CA', '90210', 'USA'),
('user456', '789 Pine Road', 'Chicago', 'IL', '60601', 'USA');

-- Sample Carts
INSERT INTO carts (customer_id, status, created_at) VALUES
('user123', 'ACTIVE', NOW()),
('user456', 'ACTIVE', NOW());

-- Sample Cart Items
INSERT INTO cart_items (cart_id, product_code, quantity, unit_price) VALUES
(1, 'PROD001', 2, 99.99),
(1, 'PROD002', 1, 24.99),
(2, 'PROD003', 3, 14.99);

-- Sample Orders
INSERT INTO orders (order_id, customer_id, cart_id, total_amount, currency, status, created_at) VALUES
('order123', 'user123', 1, 224.97, 'USD', 'PENDING_PAYMENT', NOW()),
('order456', 'user456', 2, 44.97, 'USD', 'PENDING_PAYMENT', NOW());

-- Sample Order Items
INSERT INTO order_items (order_id, product_code, quantity, unit_price, total_price) VALUES
('order123', 'PROD001', 2, 99.99, 199.98),
('order123', 'PROD002', 1, 24.99, 24.99),
('order456', 'PROD003', 3, 14.99, 44.97);

-- Sample Notifications (if needed)
-- INSERT INTO notifications (order_id, type, recipient, status, created_at) VALUES
-- ('order123', 'ORDER_PLACED', 'john.doe@example.com', 'PENDING', NOW());

-- Verify data insertion
SELECT 'Products inserted:' as info, COUNT(*) as count FROM products
UNION ALL
SELECT 'Prices inserted:', COUNT(*) FROM price_rows
UNION ALL
SELECT 'Stock inserted:', COUNT(*) FROM stocks
UNION ALL
SELECT 'Addresses inserted:', COUNT(*) FROM addresses
UNION ALL
SELECT 'Carts inserted:', COUNT(*) FROM carts
UNION ALL
SELECT 'Orders inserted:', COUNT(*) FROM orders;