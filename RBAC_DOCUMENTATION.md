# Role-Based Access Control (RBAC) System

## Overview
This e-commerce system implements a comprehensive Role-Based Access Control (RBAC) system with two main roles: **Admin** and **Customer**.

---

## Roles and Responsibilities

### 1. ADMIN ROLE
Admins have full control over the system and can manage all aspects of the e-commerce platform.

#### Admin Permissions:
- ✅ **Product Management**
  - CREATE new products
  - UPDATE existing products
  - DELETE products
  - VIEW all products

- ✅ **Price Management**
  - CREATE prices for products
  - UPDATE product prices
  - DELETE prices
  - VIEW all prices

- ✅ **Stock Management**
  - CREATE stock entries
  - UPDATE stock quantities
  - DELETE stock records
  - VIEW all stock information

- ✅ **User Management**
  - VIEW all users in the system
  - UPDATE user information
  - DELETE users

- ✅ **Order Management**
  - VIEW all orders
  - UPDATE order status
  - PROCESS returns and refunds

- ✅ **Payment Management**
  - VIEW all payments
  - PROCESS payments
  - HANDLE disputes and refunds

- ✅ **Analytics & Reports**
  - VIEW sales analytics
  - VIEW sales reports
  - GENERATE business insights

---

### 2. CUSTOMER ROLE
Customers have limited permissions focused on their own shopping experience.

#### Customer Permissions:
- ✅ **Product Browsing**
  - VIEW all products
  - VIEW product details
  - VIEW product prices
  - VIEW product availability/stock

- ✅ **Shopping Cart**
  - CREATE shopping cart
  - UPDATE cart items (add/remove/modify quantities)
  - VIEW own cart

- ✅ **Order Management**
  - CREATE orders (place purchases)
  - VIEW own orders only
  - ❌ CANNOT view other customers' orders

- ✅ **Address Management**
  - CREATE delivery addresses
  - UPDATE own addresses
  - DELETE own addresses
  - ❌ CANNOT manage other users' addresses

- ✅ **Payment**
  - PROCESS payment for own orders
  - VIEW own payment history
  - ❌ CANNOT access other users' payments

- ❌ **DENIED Permissions**
  - ❌ CANNOT create, update, or delete products
  - ❌ CANNOT manage prices
  - ❌ CANNOT manage stock
  - ❌ CANNOT view other users' data
  - ❌ CANNOT view system analytics

---

## Implementation Details

### 1. Security Infrastructure

#### Permission Enum
Located at: `user-service/src/main/java/com/digiCart/user_service/security/Permission.java`

Defines all available permissions in the system:
```java
Permission.CREATE_PRODUCT
Permission.UPDATE_PRODUCT
Permission.DELETE_PRODUCT
Permission.VIEW_PRODUCT
Permission.CREATE_PRICE
Permission.UPDATE_PRICE
// ... and more
```

#### RolePermissionMapping
Located at: `user-service/src/main/java/com/digiCart/user_service/security/RolePermissionMapping.java`

Maps roles to permissions and provides utility methods:
```java
// Check if a role has permission
boolean hasPermission(Role role, Permission permission)

// Check if role has any of the permissions
boolean hasAnyPermission(Role role, Permission... permissions)

// Check if role has all permissions
boolean hasAllPermissions(Role role, Permission... permissions)
```

#### AuthorizationService
Located at: `user-service/src/main/java/com/digiCart/user_service/service/AuthorizationService.java`

Provides user-level authorization checks:
```java
// Check user permissions
boolean hasPermission(String userId, Permission permission)
boolean hasAnyPermission(String userId, Permission... permissions)
boolean hasAllPermissions(String userId, Permission... permissions)

// Check user role
boolean isAdmin(String userId)
boolean isCustomer(String userId)

// Require permission (throws exception if not authorized)
void requirePermission(String userId, Permission permission)
void requireAdmin(String userId)
```

### 2. Enforcement Mechanisms

#### HTTP Header-Based Role Passing
Each request should include the user's role in the `X-User-Role` header:
```
X-User-Role: Admin
X-User-Role: Customer
```

#### API Gateway Integration
The API Gateway (or authentication middleware) should:
1. Authenticate the user
2. Extract the user's role from the database
3. Add the `X-User-Role` header to the request before routing to microservices

#### Microservice Validation
Each microservice validates the role in the request header:

**Product Service** (`/products` endpoint)
```java
@PostMapping
public ResponseEntity<?> addProduct(
    @RequestBody Product request,
    @RequestHeader(value = "X-User-Role") String userRole) {
    
    if (!"Admin".equalsIgnoreCase(userRole)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(Map.of("error", "Only Admin users can create products"));
    }
    // Create product...
}
```

Same pattern applied to:
- **Price Service** (`/prices` endpoint)
- **Stock Service** (`/stocks` endpoint)

---

## API Usage Examples

### For ADMIN Users

#### Create a Product
```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -H "X-User-Role: Admin" \
  -d '{
    "code": "PROD001",
    "name": "Laptop",
    "description": "High-performance laptop"
  }'
```

#### Update Product Price
```bash
curl -X PUT http://localhost:8080/prices \
  -H "Content-Type: application/json" \
  -H "X-User-Role: Admin" \
  -d '{
    "productCode": "PROD001",
    "price": 999.99,
    "currency": "USD"
  }'
```

#### Update Stock
```bash
curl -X PUT http://localhost:8080/stocks \
  -H "Content-Type: application/json" \
  -H "X-User-Role: Admin" \
  -d '{
    "productId": "PROD001",
    "quantity": 100
  }'
```

### For CUSTOMER Users

#### View All Products (Allowed)
```bash
curl -X GET http://localhost:8080/products \
  -H "X-User-Role: Customer"
```

#### Attempt to Create Product (Denied)
```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -H "X-User-Role: Customer" \
  -d '{...}'

# Response: 403 Forbidden
# "Only Admin users can create products"
```

#### Place an Order
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -H "X-User-Role: Customer" \
  -H "X-User-Id: user-123" \
  -d '{...}'
```

---

## Flow Diagram

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ (login)
       ▼
┌──────────────────┐
│ Authentication   │
│ (User Service)   │
└──────┬───────────┘
       │ (return user + role)
       ▼
┌──────────────────┐
│  API Gateway     │ ◄─── Adds X-User-Role header
└──────┬───────────┘
       │ (route request with role header)
       ▼
┌──────────────────────────────────────────┐
│  Microservice (Product/Price/Stock)      │
│  ┌─────────────────────────────────────┐ │
│  │ Check X-User-Role header            │ │
│  │ If Admin: Allow operation           │ │
│  │ Else: Return 403 Forbidden          │ │
│  └─────────────────────────────────────┘ │
└──────────────────────────────────────────┘
```

---

## Security Best Practices

1. **Never Trust Client Role Claims**
   - Always validate roles on the server side
   - Extract role from authenticated user session, not from client headers alone

2. **Use JWT Tokens** (Recommended)
   - Embed role in JWT token
   - Validate token signature and role claims

3. **Token in Headers**
   - Use `Authorization: Bearer <token>` instead of passing plain role
   - Extract and validate role from token at API Gateway

4. **Audit Logging**
   - Log all permission denials
   - Track admin actions for compliance

5. **Database Level**
   - Store role with user in database
   - Never modify role without proper audit trail

---

## Adding New Permissions

To add new permissions to the system:

### Step 1: Add Permission to Enum
```java
// In Permission.java
public enum Permission {
    // ... existing permissions
    NEW_PERMISSION("NEW_PERMISSION");
    
    // ... rest of code
}
```

### Step 2: Update RolePermissionMapping
```java
// In RolePermissionMapping.java
static {
    Set<Permission> adminPermissions = EnumSet.allOf(Permission.class);
    
    Set<Permission> customerPermissions = EnumSet.of(
        // ... existing permissions
        Permission.NEW_PERMISSION  // if customer should have it
    );
    
    rolePermissions.put(Role.Admin, adminPermissions);
    rolePermissions.put(Role.Customer, customerPermissions);
}
```

### Step 3: Update AuthorizationService (Optional)
```java
// Add convenience method if needed
public boolean canDoNewPermission(String userId) {
    return hasPermission(userId, Permission.NEW_PERMISSION);
}
```

---

## Troubleshooting

### Issue: 403 Forbidden for Admin Action
- Check if `X-User-Role: Admin` header is present
- Verify the role header matches exactly (case-sensitive in some implementations)
- Check user account role in database

### Issue: Customer Can Access Admin Functions
- Ensure API Gateway validates and adds role header
- Check role validation logic in microservice
- Verify role-checking code before operation execution

### Issue: Role Header Missing
- Configure API Gateway to extract and pass role
- Ensure authentication middleware runs before role validation

---

## Future Enhancements

1. **Fine-grained Permissions**
   - Add more specific permissions (e.g., `DELETE_OWN_ORDER` vs `DELETE_ANY_ORDER`)

2. **Dynamic Roles**
   - Support custom roles created at runtime
   - Permission inheritance for role hierarchies

3. **Time-based Permissions**
   - Temporarily grant elevated permissions
   - Session-based role elevation

4. **Resource-level Permissions**
   - Control access based on specific resource ownership
   - (e.g., customer can edit their own profile but not others')

---

## Contact & Support

For questions or issues with the RBAC system, contact the development team.

