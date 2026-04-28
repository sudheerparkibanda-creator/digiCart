# Quick Reference: @PreAuthorize Implementation

## File Structure Created

```
price-service/
├── pom.xml (UPDATED - added spring-boot-starter-security)
└── src/main/java/com/digiCart/price_service/
    ├── config/
    │   ├── SecurityConfig.java (NEW)
    │   ├── CustomUserDetailsService.java (NEW)
    │   └── SecurityContextFilter.java (NEW)
    └── controller/
        └── PriceRowController.java (UPDATED - @PreAuthorize added)

product-service/
├── pom.xml (UPDATED - added spring-boot-starter-security)
└── src/main/java/com/digiCart/product_service/
    ├── config/
    │   ├── SecurityConfig.java (NEW)
    │   ├── CustomUserDetailsService.java (NEW)
    │   └── SecurityContextFilter.java (NEW)
    └── controller/
        └── ProductController.java (UPDATED - @PreAuthorize added)

stock-service/
├── pom.xml (UPDATED - added spring-boot-starter-security)
└── src/main/java/com/digiCart/stock_service/
    ├── config/
    │   ├── SecurityConfig.java (NEW)
    │   ├── CustomUserDetailsService.java (NEW)
    │   └── SecurityContextFilter.java (NEW)
    └── controller/
        └── StockController.java (UPDATED - @PreAuthorize added)
```

## Protected Methods

### Price Service - PriceRowController
- `POST /prices` - `@PreAuthorize("hasRole('Admin')")`
- `PUT /prices` - `@PreAuthorize("hasRole('Admin')")`
- `DELETE /prices/{productCode}` - `@PreAuthorize("hasRole('Admin')")`

### Product Service - ProductController
- `POST /products` - `@PreAuthorize("hasRole('Admin')")`
- `PUT /products` - `@PreAuthorize("hasRole('Admin')")`
- `DELETE /products/{code}` - `@PreAuthorize("hasRole('Admin')")`

### Stock Service - StockController
- `POST /stocks` - `@PreAuthorize("hasRole('Admin')")`
- `PUT /stocks` - `@PreAuthorize("hasRole('Admin')")`
- `DELETE /stocks/{productId}` - `@PreAuthorize("hasRole('Admin')")`

## How to Extend Authorization

### Add multiple roles:
```java
@PreAuthorize("hasRole('Admin') or hasRole('Manager')")
public ResponseEntity<?> updatePrice(...) { ... }
```

### Add permission checks:
```java
@PreAuthorize("hasAuthority('PERMISSION_EDIT_PRODUCT')")
public ResponseEntity<?> updateProduct(...) { ... }
```

### Use SpEL expressions:
```java
@PreAuthorize("@securityService.isOwner(#id)")
public ResponseEntity<?> getResource(@PathVariable Long id) { ... }
```

## Key Components

### 1. SecurityConfig
- Enables `@PreAuthorize` via `@EnableMethodSecurity(prePostEnabled = true)`
- Configures HTTP security (CSRF disabled, requests authenticated)
- Permits `/actuator/**` endpoints

### 2. SecurityContextFilter
- Runs on every request
- Reads `X-Auth-Role` header from API Gateway
- Populates Spring `SecurityContext` with authenticated user

### 3. CustomUserDetailsService
- Implements Spring's `UserDetailsService`
- Part of standard Spring Security setup
- Not actively used in this header-based flow but available if needed

## Header Flow

```
Client Request with JWT
        ↓
    API Gateway
        ├─ Validates JWT
        ├─ Extracts role (e.g., "Admin")
        ├─ Normalizes to "ADMIN"
        └─ Injects header: X-Auth-Role: ADMIN
        ↓
  Downstream Service
        ├─ SecurityContextFilter intercepts
        ├─ Reads X-Auth-Role header
        ├─ Creates GrantedAuthority: ROLE_ADMIN
        └─ Sets in SecurityContext
        ↓
  Controller Method
        ├─ @PreAuthorize checks authority
        ├─ Matches ROLE_ADMIN ✓
        └─ Proceeds with request
        ↓
    Response
```

## Dependencies

Maven (pom.xml):
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

## Configuration Properties (Optional)

Add to `application.properties` if needed:

```properties
# Enable debug logging for security
logging.level.org.springframework.security=DEBUG

# Disable security for testing (NOT recommended for production)
spring.security.require-ssl=false
```

## Testing with cURL

```bash
# Get token
TOKEN=$(curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r '.token')

# Use token to create product (must have Admin role)
curl -X POST http://localhost:8080/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"code":"PROD-001","name":"Test","price":100.0}'

# Response: 201 Created (if Admin) or 403 Forbidden (if not Admin)
```

## Common @PreAuthorize Expressions

```java
// Check for single role
@PreAuthorize("hasRole('Admin')")

// Check for multiple roles (OR)
@PreAuthorize("hasRole('Admin') or hasRole('Manager')")

// Check for multiple roles (AND)
@PreAuthorize("hasRole('Admin') and hasRole('SuperUser')")

// Check for permission
@PreAuthorize("hasAuthority('VIEW_PRODUCTS')")

// Check user name
@PreAuthorize("authentication.name == 'admin'")

// Custom bean method
@PreAuthorize("@securityService.canEdit(#productId)")
```

## Troubleshooting

### Issue: 403 Forbidden on admin-only endpoints
- Check if `X-Auth-Role` header is present in request
- Verify role is uppercase in header (should be "ADMIN", "CUSTOMER", etc.)
- Ensure JWT token is valid and not expired

### Issue: @PreAuthorize not being evaluated
- Verify `@EnableMethodSecurity(prePostEnabled = true)` is in SecurityConfig
- Check that `spring-boot-starter-security` dependency is added
- Ensure SecurityContextFilter is picking up the role header

### Issue: Header not passed from API Gateway
- Confirm API Gateway's JwtAuthenticationFilter has `X-Auth-Role` injection
- Check that MutableHttpServletRequest is being used to add headers
- Verify the downstream service is behind the API Gateway routing

## Files Checklist

✓ Price Service
  ✓ pom.xml - Spring Security dependency
  ✓ SecurityConfig.java
  ✓ CustomUserDetailsService.java
  ✓ SecurityContextFilter.java
  ✓ PriceRowController.java - @PreAuthorize added

✓ Product Service
  ✓ pom.xml - Spring Security dependency
  ✓ SecurityConfig.java
  ✓ CustomUserDetailsService.java
  ✓ SecurityContextFilter.java
  ✓ ProductController.java - @PreAuthorize added

✓ Stock Service
  ✓ pom.xml - Spring Security dependency
  ✓ SecurityConfig.java
  ✓ CustomUserDetailsService.java
  ✓ SecurityContextFilter.java
  ✓ StockController.java - @PreAuthorize added

