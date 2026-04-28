# Spring Security @PreAuthorize Refactoring Summary

## Overview
Successfully refactored three microservices to use Spring Security's `@PreAuthorize` annotation for role-based access control instead of manual if-else checks in controller methods.

## Services Updated

### 1. **Price Service** (price-service)
**Files Modified:**
- `pom.xml` - Added Spring Security dependency
- `src/main/java/com/digiCart/price_service/config/SecurityConfig.java` (NEW)
- `src/main/java/com/digiCart/price_service/config/CustomUserDetailsService.java` (NEW)
- `src/main/java/com/digiCart/price_service/config/SecurityContextFilter.java` (NEW)
- `src/main/java/com/digiCart/price_service/controller/PriceRowController.java` (UPDATED)

**Changes in PriceRowController:**
- Removed manual if-else role checks from `addPriceRow()`, `updatePriceRow()`, `removePriceRow()`
- Removed `@RequestHeader(value = "X-User-Role")` parameter
- Added `@PreAuthorize("hasRole('Admin')")` annotation to protected methods
- Cleaner, more declarative code

### 2. **Product Service** (product-service)
**Files Modified:**
- `pom.xml` - Added Spring Security dependency
- `src/main/java/com/digiCart/product_service/config/SecurityConfig.java` (NEW)
- `src/main/java/com/digiCart/product_service/config/CustomUserDetailsService.java` (NEW)
- `src/main/java/com/digiCart/product_service/config/SecurityContextFilter.java` (NEW)
- `src/main/java/com/digiCart/product_service/controller/ProductController.java` (UPDATED)

**Changes in ProductController:**
- Removed manual if-else role checks from `addProduct()`, `updateProduct()`, `removeProduct()`
- Removed `@RequestHeader(value = "X-User-Role")` parameter
- Added `@PreAuthorize("hasRole('Admin')")` annotation to protected methods

### 3. **Stock Service** (stock-service)
**Files Modified:**
- `pom.xml` - Added Spring Security dependency
- `src/main/java/com/digiCart/stock_service/config/SecurityConfig.java` (NEW)
- `src/main/java/com/digiCart/stock_service/config/CustomUserDetailsService.java` (NEW)
- `src/main/java/com/digiCart/stock_service/config/SecurityContextFilter.java` (NEW)
- `src/main/java/com/digiCart/stock_service/controller/StockController.java` (UPDATED)

**Changes in StockController:**
- Removed manual if-else role checks from `addStock()`, `updateStock()`, `removeStock()`
- Removed `@RequestHeader(value = "X-User-Role")` parameter
- Added `@PreAuthorize("hasRole('Admin')")` annotation to protected methods

## Architecture

### How It Works

1. **API Gateway** (`api-gateway`)
   - Validates JWT tokens
   - Extracts username and role from token
   - Injects headers into forwarded request:
     - `X-Auth-Username`: The authenticated user
     - `X-Auth-Role`: The user's role (normalized to uppercase)

2. **Downstream Services** (price-service, product-service, stock-service)
   - `SecurityContextFilter` intercepts all requests
   - Extracts role from `X-Auth-Role` header
   - Creates `UsernamePasswordAuthenticationToken` with role as authority
   - Populates `SecurityContext`

3. **Controller Methods**
   - `@PreAuthorize("hasRole('Admin')")` checks if user has ADMIN role
   - Spring Security framework handles authorization
   - Clean, declarative approach

### New Configuration Classes

#### SecurityConfig
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // Enables @PreAuthorize
public class SecurityConfig { ... }
```

#### SecurityContextFilter
- Reads `X-Auth-Role` and `X-Auth-Username` headers from API Gateway
- Sets up authentication in SecurityContext
- Allows `@PreAuthorize` annotations to work

#### CustomUserDetailsService
- Implements UserDetailsService interface
- Used by Spring Security (though headers take precedence in this architecture)

## Benefits

✅ **Declarative Authorization** - Uses annotations instead of imperative code
✅ **Reduced Boilerplate** - No more if-else role checking
✅ **Better Readability** - Clearer intent of which roles can access what
✅ **Consistent Spring Way** - Uses standard Spring Security patterns
✅ **Easier Testing** - Easier to mock/test with Spring Security test utilities
✅ **Centralized Security Logic** - All authorization in one place (SecurityContextFilter)

## Before vs After

### Before (Manual If-Else)
```java
@PostMapping
public ResponseEntity<?> addProduct(
        @RequestBody Product request,
        @RequestHeader(value = "X-User-Role", required = false) String userRole) {
    
    if (!"Admin".equalsIgnoreCase(userRole)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Only Admin users can create products"));
    }
    
    return saveOrUpdate(request);
}
```

### After (Using @PreAuthorize)
```java
@PostMapping
@PreAuthorize("hasRole('Admin')")
public ResponseEntity<?> addProduct(@RequestBody Product request) {
    return saveOrUpdate(request);
}
```

## Dependencies Added

All three services now include:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

## Header Mapping

The flow of headers through the system:

1. **API Gateway receives JWT**
2. **Extracts role** → "Admin" → "ADMIN" (normalized)
3. **Adds header** → `X-Auth-Role: ADMIN`
4. **Downstream filter reads** → "ADMIN"
5. **Creates authority** → "ROLE_ADMIN"
6. **@PreAuthorize checks** → `hasRole('Admin')` matches `ROLE_ADMIN` ✓

## Testing the Implementation

To test, make requests with valid JWT token:

```bash
# Login to get token
POST /auth/login
{ "username": "admin", "password": "admin123" }

# Response includes token
{ "token": "eyJhbGc...", "username": "admin", "role": "Admin" }

# Use token to create product
POST /products
Authorization: Bearer eyJhbGc...
{ "code": "PROD-001", ... }
```

## Security Notes

- Only `X-Auth-Role` header from API Gateway is trusted
- Services assume API Gateway has already validated JWT
- No additional token validation needed in downstream services
- All changes maintain existing security posture

## Deployment Notes

After deploying these changes, ensure:
1. All three services rebuild their Docker images
2. API Gateway is already injecting `X-Auth-Role` headers (✓ confirmed in existing code)
3. No configuration changes needed in API Gateway
4. Services will automatically start using Spring Security with @PreAuthorize

## Future Enhancements

Possible future improvements:
- Add more granular role checks (e.g., `@PreAuthorize("hasRole('Admin') or hasRole('Manager')")`)
- Implement custom permission annotations
- Add auditing of authorization decisions
- Use SpEL expressions for complex authorization logic

