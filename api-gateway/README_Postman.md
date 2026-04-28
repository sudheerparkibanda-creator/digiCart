# DigiCart Postman Collection

This repository contains a complete Postman collection for testing the DigiCart microservices architecture.

## Files

- `DigiCart_Postman_Collection.postman_collection.json` - The main Postman collection with all API endpoints
- `DigiCart_Postman_Environment.postman_environment.json` - Environment variables for testing

## Setup Instructions

1. **Import the Collection:**
   - Open Postman
   - Click "Import" button
   - Select "File" tab
   - Choose `DigiCart_Postman_Collection.postman_collection.json`

2. **Import the Environment:**
   - In Postman, click "Import" again
   - Select "File" tab
   - Choose `DigiCart_Postman_Environment.postman_environment.json`
   - Select the "DigiCart Environment" from the environment dropdown

3. **Start Services:**
   - Ensure all microservices are running:
     - Config Server (port 8888)
     - Service Registry (port 8761)
     - API Gateway (port 8080)
     - User Service (port 8081)
     - Address Service (port 8082)
     - Product Service (port 8083)
     - Price Service (port 8084)
     - Stock Service (port 8085)
     - Cart Service (port 8086)
     - Order Service (port 8087)
     - Payment Service (port 8088)
     - Notification Service (port 8089)

4. **Database Setup:**
   - Ensure MySQL is running with the required databases
   - Or use Docker Compose if available

## Authentication Flow

### Register User
- **Endpoint:** `POST {{base_url}}/auth/register`
- **Body:**
  ```json
  {
    "username": "{{username}}",
    "password": "{{password}}"
  }
  ```
- **No authentication required** (public endpoint)
- **Automatically sets token** in environment variable

### Login User
- **Endpoint:** `POST {{base_url}}/auth/login`
- **Body:**
  ```json
  {
    "username": "{{username}}",
    "password": "{{password}}"
  }
  ```
- **No authentication required** (public endpoint)
- **Automatically sets token** in environment variable

### Using Token in Other Requests
- All other endpoints require JWT token in Authorization header
- Token is automatically included via `{{token}}` variable
- Format: `Bearer {{token}}`

## API Endpoints Overview

### Authentication
- `POST /auth/register` - Register new user (public)
- `POST /auth/login` - Login user (public)

### Address Service
- `GET /addresses` - Get all addresses (public)
- `GET /addresses/{id}` - Get address by ID (public)
- `POST /addresses` - Add address (authenticated)
- `PUT /addresses/{id}` - Update address (authenticated)
- `DELETE /addresses/{id}` - Delete address (authenticated)

### Product Service
- `GET /products` - Get all products (public)
- `GET /products/{code}` - Get product by code (public)
- `POST /products` - Add product (ADMIN only)
- `PUT /products` - Update product (ADMIN only)
- `DELETE /products/{code}` - Delete product (ADMIN only)

### Stock Service
- `GET /stocks` - Get all stocks (public)
- `GET /stocks/{productId}` - Get stock by product ID (public)
- `GET /stocks/by-product-ids` - Batch get stocks (public)
- `POST /stocks` - Add stock (ADMIN only)
- `PUT /stocks` - Update stock (ADMIN only)
- `DELETE /stocks/{productId}` - Delete stock (ADMIN only)

### Price Service
- `GET /prices` - Get all prices (public)
- `GET /prices/{productCode}` - Get price by product code (public)
- `GET /prices/by-product-codes` - Batch get prices (public)
- `POST /prices` - Add price (ADMIN only)
- `PUT /prices` - Update price (ADMIN only)
- `DELETE /prices/{productCode}` - Delete price (ADMIN only)

### Cart Service
- `POST /{customerId}/carts` - Create cart (authenticated)
- `GET /{customerId}/carts` - Get all carts (authenticated)
- `PUT /{customerId}/carts/{cartId}/delivery-address/new` - Set new delivery address (authenticated)
- `PUT /{customerId}/carts/{cartId}/delivery-address` - Set existing delivery address (authenticated)
- `POST /{customerId}/carts/{cartId}/add` - Add to cart (authenticated)
- `POST /{customerId}/carts/{cartId}/place-order` - Place order (authenticated)
- Cart entry management endpoints (authenticated)

### Order Service
- `POST /orders` - Create order (authenticated)
- `GET /orders/{orderId}` - Get order (authenticated)
- Order status update endpoints (authenticated)

### Payment Service
- `POST /payments/orders/{orderId}/payment-link` - Ensure payment link (authenticated)

## Environment Variables

| Variable | Default Value | Description |
|----------|---------------|-------------|
| `base_url` | `http://localhost:8080` | API Gateway URL |
| `token` | (empty) | JWT token (auto-set by login/register) |
| `username` | `testuser` | Test username |
| `password` | `password123` | Test password |
| `customer_id` | `1` | Customer ID for cart operations |
| `cart_id` | `1` | Cart ID for operations |
| `order_id` | `1` | Order ID for operations |
| `product_code` | `PROD001` | Product code for testing |
| `address_id` | `1` | Address ID for testing |

## Testing Workflow

1. **Register/Login** to get JWT token
2. **Browse Products** (public endpoints)
3. **Manage Addresses** (authenticated)
4. **Create Cart** and add products
5. **Set Delivery Address**
6. **Place Order**
7. **Handle Payment** (if integrated)

## Notes

- All requests go through the API Gateway (port 8080)
- JWT tokens are valid for 24 hours
- ADMIN role required for product/stock/price management
- Register endpoint does NOT require a session token (it's public)
- Token is automatically extracted and set in environment after successful login/register