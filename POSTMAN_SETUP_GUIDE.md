# DigiCart Setup & Testing Guide

## 📋 Prerequisites

All these services must be running before proceeding:

```bash
# Start all services (from project root)
docker-compose up -d

# Verify all services are healthy
docker-compose ps
```

**Expected Status:**
- MySQL: Healthy ✓
- Kafka: Healthy ✓
- Zipkin: Healthy ✓
- Config Server: Healthy ✓
- Service Registry: Healthy ✓

## 🔧 Step 1: Setup Postman Environment

### Import Environment Variables

1. Open **Postman**
2. Click **Environments** (top right)
3. Click **Import** button
4. Select: `postman_environment.json`
5. Click **Import**

### Environment Variables Provided

| Variable | Value | Purpose |
|----------|-------|---------|
| `baseUrl` | http://localhost:8080 | API Gateway |
| `configServerUrl` | http://localhost:8888 | Config Server |
| `eurekaUrl` | http://localhost:8761 | Service Registry |
| `zipkinUrl` | http://localhost:9411 | Distributed Tracing |
| `adminUsername` | admin@digicart.com | Admin login |
| `adminPassword` | admin123 | Admin password |
| `customerUsername` | john.doe@example.com | Customer login |
| `customerPassword` | password123 | Customer password |
| `jwtToken` | (auto-filled) | JWT bearer token |

## 🔧 Step 2: Import Postman Collection

1. Click **Collections** (left sidebar)
2. Click **Import** button
3. Select: `DigiCart_Postman_Collection.json`
4. Click **Import**

The collection now has:
- ✅ Correct API endpoint paths
- ✅ Authentication headers for admin requests
- ✅ Inter-request tests for JWT token capture
- ✅ Pre-request scripts for data setup
- ✅ Proper test flow order

## 🔧 Step 3: Populate Sample Data

### Option A: Using PowerShell Script (Recommended)

```powershell
cd c:\Users\sudhe\OneDrive\Documents\advancedProject
.\setup_sample_data.ps1
```

**What it does:**
- Creates users in user_service_db
- Creates products in product_service_db
- Creates prices in price_service_db
- Creates stock in stock_service_db
- Creates addresses in address_service_db
- Creates carts in cart_service_db
- Creates orders in order_service_db

### Option B: Manual SQL Execution

```bash
# Run each SQL script individually
docker exec -i advanced-project-mysql-1 mysql -u root -proot < sql/user_service_init.sql
docker exec -i advanced-project-mysql-1 mysql -u root -proot < sql/product_service_init.sql
docker exec -i advanced-project-mysql-1 mysql -u root -proot < sql/price_service_init.sql
docker exec -i advanced-project-mysql-1 mysql -u root -proot < sql/stock_service_init.sql
docker exec -i advanced-project-mysql-1 mysql -u root -proot < sql/address_service_init.sql
docker exec -i advanced-project-mysql-1 mysql -u root -proot < sql/cart_service_init.sql
docker exec -i advanced-project-mysql-1 mysql -u root -proot < sql/order_service_init.sql
```

## 📊 Sample Test Data

### Users
| User | Email | Password | Role |
|------|-------|----------|------|
| Admin | admin@digicart.com | admin123 | Admin |
| John Doe | john.doe@example.com | password123 | Customer |
| Jane Smith | jane.smith@example.com | password123 | Customer |

### Products
| Code | Description | Price | Stock |
|------|-------------|-------|-------|
| PROD001 | Wireless Bluetooth Headphones | $99.99 | 150 |
| PROD002 | Smartphone Case - Black | $24.99 | 200 |
| PROD003 | USB-C Charging Cable | $14.99 | 300 |
| PROD004 | Laptop Stand | $49.99 | 75 |
| PROD005 | Wireless Mouse | $29.99 | 120 |

## 🚀 Testing Workflow

### Complete E-Commerce Flow

1. **Authentication**
   - POST `/auth/login` (Admin) → Get JWT token
   - POST `/auth/register` (New Customer) → Register without JWT

2. **Browse Products**
   - GET `/products` → View all products
   - GET `/products/{code}` → View product details
   - GET `/prices` → View all prices
   - GET `/stocks` → View all stock levels

3. **Shopping**
   - POST `/carts/{customerId}` → Create cart
   - POST `/carts/{customerId}/{cartId}/add` → Add items
   - PUT `/carts/{customerId}/{cartId}/delivery-address` → Set address
   - POST `/carts/{customerId}/{cartId}/place-order` → Place order

4. **Order Management**
   - GET `/orders/{orderId}` → Check order status
   - PATCH `/orders/{orderId}/payment-link` → Create payment
   - GET `/payments` → Check payment status

5. **Admin Operations (Requires JWT)**
   - POST `/products` → Create product
   - PUT `/products` → Update product
   - POST `/prices` → Create price
   - PUT `/prices` → Update price
   - POST `/stocks` → Create stock
   - PUT `/stocks` → Update stock

## ⚠️ Important Notes

### JWT Token Requirement

**Registration Does NOT Require JWT:**
- Endpoint: `POST /auth/register`
- Public path: Anyone can register
- Response contains user credentials for login

**Admin Operations Require JWT:**
- Header: `Authorization: Bearer {{jwtToken}}`
- Only admin@digicart.com can create/update products, prices, stock
- Customer operations don't require JWT

### Database Structure

Services use **separate databases**:
- `user_service_db` - Users and authentication
- `product_service_db` - Products and features
- `price_service_db` - Product prices
- `stock_service_db` - Inventory management
- `cart_service_db` - Shopping carts
- `address_service_db` - Customer addresses
- `order_service_db` - Orders and transactions
- `payment_service_db` - (Not used, Kafka-based)
- `notification_service_db` - (Not used, Kafka-based)

### External Services (No Database)
- `payment-service` - Event-driven via Kafka
- `notification-service` - Email notifications via Kafka events

## 🔍 Monitoring & Debugging

### Eureka Service Registry
- URL: `http://localhost:8761`
- View registered services
- Check service health status

### Zipkin Distributed Tracing
- URL: `http://localhost:9411`
- Monitor request traces across services
- Check service dependencies

### API Gateway Routes
- URL: `http://localhost:8080/actuator/gateway/routes`
- Verify routing configuration

## ❌ Troubleshooting

### Issue: 401 Unauthorized for Admin Operations
**Solution:** 
- Run login request first
- Verify JWT token is populated
- Add `Authorization: Bearer {{jwtToken}}` header

### Issue: 404 Not Found
**Solution:**
- Check service registration in Eureka
- Verify all services are running
- Check correct API Gateway port (8080)

### Issue: Connection Refused to MySQL
**Solution:**
- Verify MySQL container is running: `docker ps`
- Check MySQL health: `docker-compose ps mysql`
- Restart if needed: `docker restart advanced-project-mysql-1`

### Issue: Cart Operations Fail
**Solution:**
- Verify customer ID matches created user
- Check cart was created successfully
- Verify products exist before adding to cart

## 📝 Key Credentials

### Admin Access
```
Username: admin@digicart.com
Password: admin123
Role: Admin
JWT Required: YES for product/price/stock operations
```

### Customer Access
```
Username: john.doe@example.com
Password: password123
Role: Customer
JWT Required: NO for shopping
```

### Registration
```
Endpoint: POST /auth/register
JWT Required: NO
Default Role: Customer
```

## 🔐 Security Considerations

⚠️ **This setup is for LOCAL TESTING ONLY**

Before production deployment:
- [ ] Change default passwords
- [ ] Move JWT secret to environment variables
- [ ] Enable HTTPS/TLS for all endpoints
- [ ] Add rate limiting to registration
- [ ] Implement email verification
- [ ] Add CSRF protection
- [ ] Configure proper CORS policies
- [ ] Implement API key for Razorpay
- [ ] Use environment-specific configs

## 🎯 Next Steps

1. ✅ Import environment variables
2. ✅ Import Postman collection
3. ✅ Run setup_sample_data.ps1
4. ✅ Select environment in Postman
5. ✅ Run "Authentication → Login" request
6. ✅ Run complete e-commerce flow
7. ✅ Check traces in Zipkin
8. ✅ Monitor services in Eureka

---

**Happy Testing! 🚀**
