-- DigiCart Cart Service - Sample Data
-- Database: cart_service_db
-- Create sample carts and cart items

USE cart_service_db;

-- Insert sample carts
INSERT IGNORE INTO carts (customer_id, status, created_at, updated_at)
VALUES
('user001', 'ACTIVE', NOW(), NOW()),
('user002', 'ACTIVE', NOW(), NOW());

-- Insert sample cart items (referencing product codes from product-service)
-- Note: cart_items references products by product_code, not by numeric ID
INSERT IGNORE INTO cart_items (cart_id, product_code, quantity, unit_price, created_at)
SELECT 
  COALESCE((SELECT id FROM carts WHERE customer_id = 'user001' LIMIT 1), 1) as cart_id,
  'PROD001' as product_code,
  2 as quantity,
  99.99 as unit_price,
  NOW() as created_at;

INSERT IGNORE INTO cart_items (cart_id, product_code, quantity, unit_price, created_at)
SELECT 
  COALESCE((SELECT id FROM carts WHERE customer_id = 'user001' LIMIT 1), 1) as cart_id,
  'PROD002' as product_code,
  1 as quantity,
  24.99 as unit_price,
  NOW() as created_at;

INSERT IGNORE INTO cart_items (cart_id, product_code, quantity, unit_price, created_at)
SELECT 
  COALESCE((SELECT id FROM carts WHERE customer_id = 'user002' LIMIT 1), 2) as cart_id,
  'PROD003' as product_code,
  3 as quantity,
  14.99 as unit_price,
  NOW() as created_at;

-- Verify data insertion
SELECT 'Carts inserted:' as info, COUNT(*) as count FROM carts
UNION ALL
SELECT 'Cart items inserted:', COUNT(*) FROM cart_items;
