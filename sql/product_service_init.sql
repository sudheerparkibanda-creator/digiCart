-- DigiCart Product Service - Sample Data
-- Database: product_service_db
-- Create sample products with features

USE product_service_db;

-- Create sample products
INSERT IGNORE INTO products (code, description, creation_time)
VALUES
('PROD001', 'Wireless Bluetooth Headphones', NOW()),
('PROD002', 'Smartphone Case - Black', NOW()),
('PROD003', 'USB-C Charging Cable', NOW()),
('PROD004', 'Laptop Stand', NOW()),
('PROD005', 'Wireless Mouse', NOW());

-- Add product features
INSERT IGNORE INTO product_features (product_id, feature_key, feature_value)
SELECT id, feature_key, feature_value FROM (
  SELECT 
    CASE WHEN code = 'PROD001' THEN 1 ELSE 0 END as id,
    'color' as feature_key, 'Black' as feature_value
  FROM products WHERE code = 'PROD001'
  UNION ALL
  SELECT 
    CASE WHEN code = 'PROD001' THEN 1 ELSE 0 END,
    'battery_life', '30 hours'
  FROM products WHERE code = 'PROD001'
  UNION ALL
  SELECT 
    CASE WHEN code = 'PROD001' THEN 1 ELSE 0 END,
    'connectivity', 'Bluetooth 5.0'
  FROM products WHERE code = 'PROD001'
  UNION ALL
  SELECT 
    CASE WHEN code = 'PROD002' THEN 2 ELSE 0 END,
    'material', 'TPU'
  FROM products WHERE code = 'PROD002'
  UNION ALL
  SELECT 
    CASE WHEN code = 'PROD002' THEN 2 ELSE 0 END,
    'compatibility', 'iPhone 12-15'
  FROM products WHERE code = 'PROD002'
) features 
WHERE id > 0
ON DUPLICATE KEY UPDATE feature_value=VALUES(feature_value);

-- Verify data insertion
SELECT 'Products inserted:' as info, COUNT(*) as count FROM products
UNION ALL
SELECT 'Product features inserted:', COUNT(*) FROM product_features;
