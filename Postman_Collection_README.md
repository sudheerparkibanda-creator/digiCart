# DigiCart Microservices - Postman Collection

Complete API collection for testing the DigiCart e-commerce microservices architecture.

## 📋 Prerequisites

1. **All services running**: Ensure all 12 microservices are running:
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

2. **Database**: MySQL running on localhost:3306 with credentials `root/root`

3. **Zipkin**: Running on localhost:9411 for distributed tracing

## 🚀 Import Collection

1. Open Postman
2. Click **Import** button
3. Select **File**
4. Choose `DigiCart_Postman_Collection.json`
5. Click **Import**

## 🔧 Environment Variables

The collection includes these variables (update as needed):

| Variable | Default Value | Description |
|----------|---------------|-------------|
| `baseUrl` | `http://localhost:8080` | API Gateway URL |
| `userId` | `user123` | Customer ID for cart operations |
| `cartId` | `cart123` | Cart ID for operations |
| `orderId` | `order123` | Order ID for operations |
| `productCode` | `PROD001` | Product code for operations |
| `addressId` | `addr123` | Address ID for operations |
| `jwtToken` | `` | JWT token for admin operations |

## 📚 API Endpoints Overview

### 🔐 Authentication
- **POST** `/auth/login` - Login and get JWT token (returns token for authenticated requests)
- **POST** `/users/internal/auth/register` - Register new user
- **POST** `/users/internal/auth/verify` - Verify user credentials

**Login Credentials:**
- **Admin:** `admin@digicart.com` / `admin123`
- **User:** `john.doe@example.com` / `password123`

**Using JWT Tokens:**
After login, the JWT token is automatically saved to the `jwtToken` variable. Include it in admin requests:
```
Authorization: Bearer {{jwtToken}}
```

### 📦 Products
- **GET** `/products` - Get all products
- **GET** `/products/{code}` - Get product by code
- **POST** `/products` - Create product (Admin only)
- **PUT** `/products` - Update product (Admin only)
- **DELETE** `/products/{code}` - Delete product (Admin only)

### 💰 Prices
- **GET** `/prices` - Get all prices
- **GET** `/prices/{productCode}` - Get price by product code
- **GET** `/prices/by-product-codes` - Get prices for multiple products
- **POST** `/prices` - Create price (Admin only)
- **PUT** `/prices` - Update price (Admin only)
- **DELETE** `/prices/{productCode}` - Delete price (Admin only)

### 📊 Stock
- **GET** `/stocks` - Get all stock
- **GET** `/stocks/{productId}` - Get stock by product ID
- **GET** `/stocks/by-product-ids` - Get stock for multiple products
- **POST** `/stocks` - Create stock (Admin only)
- **PUT** `/stocks` - Update stock (Admin only)
- **DELETE** `/stocks/{productId}` - Delete stock (Admin only)

### 📍 Addresses
- **GET** `/addresses` - Get all addresses
- **GET** `/addresses/{addressId}` - Get address by ID
- **POST** `/addresses` - Create address
- **PUT** `/addresses/{addressId}` - Update address
- **DELETE** `/addresses/{addressId}` - Delete address

### 🛒 Cart
- **POST** `/{customerId}/carts` - Create cart
- **GET** `/{customerId}/carts` - Get all carts for customer
- **POST** `/{customerId}/carts/{cartId}/add` - Add item to cart
- **PUT** `/{customerId}/carts/{cartId}/delivery-address/new` - Set new delivery address
- **PUT** `/{customerId}/carts/{cartId}/delivery-address` - Set existing delivery address
- **POST** `/{customerId}/carts/{cartId}/place-order` - Place order

### 📋 Orders
- **POST** `/orders` - Create order
- **GET** `/orders/{orderId}` - Get order by ID
- **PATCH** `/orders/{orderId}/payment-link` - Update payment link
- **PATCH** `/orders/{orderId}/payment-captured` - Mark payment captured
- **PATCH** `/orders/{orderId}/order-placed-email-sent` - Mark email sent

### 💳 Payments
- **POST** `/payments/orders/{orderId}/payment-link` - Ensure payment link
- **POST** `/payments/webhooks/razorpay` - Razorpay webhook

### 🔍 Service Discovery & Monitoring
- **GET** `http://localhost:8761` - Eureka Dashboard
- **GET** `http://localhost:9411` - Zipkin Dashboard
- **GET** `/actuator/gateway/routes` - API Gateway routes

## 🧪 Sample Test Flow

1. **Register a user** (Authentication → Register User)
2. **Create products** (Products → Create Product)
3. **Set prices** (Prices → Create Price)
4. **Add stock** (Stock → Create Stock)
5. **Create address** (Addresses → Create Address)
6. **Create cart** (Cart → Create Cart)
7. **Add items to cart** (Cart → Add Item to Cart)
8. **Set delivery address** (Cart → Set Delivery Address)
9. **Place order** (Cart → Place Order)
10. **Check order status** (Orders → Get Order by ID)

## 🔑 Authentication Notes

- Admin operations require JWT token in `Authorization: Bearer {{jwtToken}}` header
- Regular user operations don't require authentication
- Update the `jwtToken` variable with a valid admin token for admin operations

## 📊 Monitoring

- **Eureka Dashboard**: View registered services at `http://localhost:8761`
- **Zipkin Dashboard**: View distributed traces at `http://localhost:9411`
- **API Gateway Routes**: Check routing configuration at `/actuator/gateway/routes`

## 🐛 Troubleshooting

1. **Connection refused**: Ensure all services are running
2. **404 errors**: Check service registration in Eureka
3. **401 Unauthorized**: Provide valid JWT token for admin operations
4. **500 errors**: Check service logs for detailed error information

## 📝 Notes

- All requests go through the API Gateway (port 8080)
- Gateway routes requests to appropriate microservices using Eureka service discovery
- Sample data is provided in request bodies
- Update variable values as needed for your testing scenarios
- **Login endpoint**: Use `/auth/login` to get JWT tokens for admin operations
- **Admin credentials**: `admin@digicart.com` / `admin123`