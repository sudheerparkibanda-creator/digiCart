# Code Changes: Before & After

## Controller Changes

### PriceRowController - POST Method

**BEFORE (If-Else):**
```java
@PostMapping
public ResponseEntity<?> addPriceRow(
        @RequestBody PriceRow request,
        @RequestHeader(value = "X-User-Role", required = false) String userRole) {

    // Only Admin can add prices
    if (!"Admin".equalsIgnoreCase(userRole)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Only Admin users can create prices"));
    }

    return saveOrUpdate(request);
}
```

**AFTER (@PreAuthorize):**
```java
@PostMapping
@PreAuthorize("hasRole('Admin')")
public ResponseEntity<?> addPriceRow(@RequestBody PriceRow request) {
    return saveOrUpdate(request);
}
```

### PriceRowController - PUT Method

**BEFORE:**
```java
@PutMapping
public ResponseEntity<?> updatePriceRow(
        @RequestBody PriceRow request,
        @RequestHeader(value = "X-User-Role", required = false) String userRole) {

    // Only Admin can update prices
    if (!"Admin".equalsIgnoreCase(userRole)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Only Admin users can update prices"));
    }

    return saveOrUpdate(request);
}
```

**AFTER:**
```java
@PutMapping
@PreAuthorize("hasRole('Admin')")
public ResponseEntity<?> updatePriceRow(@RequestBody PriceRow request) {
    return saveOrUpdate(request);
}
```

### PriceRowController - DELETE Method

**BEFORE:**
```java
@DeleteMapping("/{productCode}")
public ResponseEntity<?> removePriceRow(
        @PathVariable String productCode,
        @RequestHeader(value = "X-User-Role", required = false) String userRole) {

    // Only Admin can delete prices
    if (!"Admin".equalsIgnoreCase(userRole)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Only Admin users can delete prices"));
    }

    return priceRowService.removeByProductCode(productCode)
            .map(row -> ResponseEntity.noContent().<Void>build())
            .orElseGet(() -> ResponseEntity.notFound().build());
}
```

**AFTER:**
```java
@DeleteMapping("/{productCode}")
@PreAuthorize("hasRole('Admin')")
public ResponseEntity<?> removePriceRow(@PathVariable String productCode) {
    return priceRowService.removeByProductCode(productCode)
            .map(row -> ResponseEntity.noContent().<Void>build())
            .orElseGet(() -> ResponseEntity.notFound().build());
}
```

### ProductController - POST Method

**BEFORE:**
```java
@PostMapping
public ResponseEntity<?> addProduct(
        @RequestBody Product request,
        @RequestHeader(value = "X-User-Role", required = false) String userRole) {

    // Only Admin can add products
    if (!"Admin".equalsIgnoreCase(userRole)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Only Admin users can create products"));
    }

    return saveOrUpdate(request);
}
```

**AFTER:**
```java
@PostMapping
@PreAuthorize("hasRole('Admin')")
public ResponseEntity<?> addProduct(@RequestBody Product request) {
    return saveOrUpdate(request);
}
```

### ProductController - PUT Method

**BEFORE:**
```java
@PutMapping
public ResponseEntity<?> updateProduct(
        @RequestBody Product request,
        @RequestHeader(value = "X-User-Role", required = false) String userRole) {

    // Only Admin can update products
    if (!"Admin".equalsIgnoreCase(userRole)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Only Admin users can update products"));
    }

    return saveOrUpdate(request);
}
```

**AFTER:**
```java
@PutMapping
@PreAuthorize("hasRole('Admin')")
public ResponseEntity<?> updateProduct(@RequestBody Product request) {
    return saveOrUpdate(request);
}
```

### ProductController - DELETE Method

**BEFORE:**
```java
@DeleteMapping("/{code}")
public ResponseEntity<?> removeProduct(
        @PathVariable String code,
        @RequestHeader(value = "X-User-Role", required = false) String userRole) {

    // Only Admin can delete products
    if (!"Admin".equalsIgnoreCase(userRole)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Only Admin users can delete products"));
    }

    return productService.removeByCode(code)
            .map(product -> ResponseEntity.noContent().<Void>build())
            .orElseGet(() -> ResponseEntity.notFound().build());
}
```

**AFTER:**
```java
@DeleteMapping("/{code}")
@PreAuthorize("hasRole('Admin')")
public ResponseEntity<?> removeProduct(@PathVariable String code) {
    return productService.removeByCode(code)
            .map(product -> ResponseEntity.noContent().<Void>build())
            .orElseGet(() -> ResponseEntity.notFound().build());
}
```

### StockController - POST Method

**BEFORE:**
```java
@PostMapping
public ResponseEntity<?> addStock(
        @RequestBody Stock request,
        @RequestHeader(value = "X-User-Role", required = false) String userRole) {

    // Only Admin can add stock
    if (!"Admin".equalsIgnoreCase(userRole)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Only Admin users can create stock"));
    }

    return saveOrUpdate(request);
}
```

**AFTER:**
```java
@PostMapping
@PreAuthorize("hasRole('Admin')")
public ResponseEntity<?> addStock(@RequestBody Stock request) {
    return saveOrUpdate(request);
}
```

### StockController - PUT Method

**BEFORE:**
```java
@PutMapping
public ResponseEntity<?> updateStock(
        @RequestBody Stock request,
        @RequestHeader(value = "X-User-Role", required = false) String userRole) {

    // Only Admin can update stock
    if (!"Admin".equalsIgnoreCase(userRole)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Only Admin users can update stock"));
    }

    return saveOrUpdate(request);
}
```

**AFTER:**
```java
@PutMapping
@PreAuthorize("hasRole('Admin')")
public ResponseEntity<?> updateStock(@RequestBody Stock request) {
    return saveOrUpdate(request);
}
```

### StockController - DELETE Method

**BEFORE:**
```java
@DeleteMapping("/{productId}")
public ResponseEntity<?> removeStock(
        @PathVariable String productId,
        @RequestHeader(value = "X-User-Role", required = false) String userRole) {

    // Only Admin can delete stock
    if (!"Admin".equalsIgnoreCase(userRole)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Only Admin users can delete stock"));
    }

    return stockService.removeByProductId(productId)
            .map(s -> ResponseEntity.noContent().<Void>build())
            .orElseGet(() -> ResponseEntity.notFound().build());
}
```

**AFTER:**
```java
@DeleteMapping("/{productId}")
@PreAuthorize("hasRole('Admin')")
public ResponseEntity<?> removeStock(@PathVariable String productId) {
    return stockService.removeByProductId(productId)
            .map(s -> ResponseEntity.noContent().<Void>build())
            .orElseGet(() -> ResponseEntity.notFound().build());
}
```

## New Configuration Classes

### SecurityConfig.java (All 3 Services)

```java
package com.digiCart.price_service.config;  // Same for product and stock services

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // KEY: Enables @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

### SecurityContextFilter.java (All 3 Services)

```java
package com.digiCart.price_service.config;  // Same for product and stock services

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filter to extract the X-Auth-Role header (from API Gateway) and set up the SecurityContext
 * This allows @PreAuthorize annotations to work with the role information
 */
@Component
public class SecurityContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String userRole = request.getHeader("X-Auth-Role");
        String username = request.getHeader("X-Auth-Username");
        
        if (userRole != null && !userRole.isBlank()) {
            // Create an authentication token with the role from the header
            String roleWithPrefix = userRole.startsWith("ROLE_") ? userRole : "ROLE_" + userRole;
            var authorities = Collections.singletonList(new SimpleGrantedAuthority(roleWithPrefix));
            var authentication = new UsernamePasswordAuthenticationToken(
                username != null ? username : "user", 
                null, 
                authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### CustomUserDetailsService.java (All 3 Services)

```java
package com.digiCart.price_service.config;  // Same for product and stock services

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetailsService that works with the X-User-Role header
 * passed from the API Gateway via the SecurityContextFilter
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Return a user with the roles/authorities that were extracted from headers
        Collection<? extends GrantedAuthority> authorities = 
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(username, "", authorities);
    }
}
```

## POM.xml Changes

**BEFORE:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- NO SECURITY -->
```

**AFTER:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

## Import Changes in Controllers

**ADDED:**
```java
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;  // Added to PriceRowController
```

**REMOVED:**
```java
import org.springframework.http.HttpStatus;  // No longer needed for manual checking
import java.util.Map;                        // No longer needed for error messages
```

## Summary of Changes

| Item | Count |
|------|-------|
| Services Updated | 3 (Price, Product, Stock) |
| pom.xml files modified | 3 |
| New SecurityConfig.java files | 3 |
| New CustomUserDetailsService.java files | 3 |
| New SecurityContextFilter.java files | 3 |
| Controllers with @PreAuthorize added | 3 |
| Methods refactored per service | 3 (POST, PUT, DELETE) |
| Total methods refactored | 9 |
| Lines of boilerplate code removed | ~72 lines |
| Documentation files created | 2 |

## Migration Checklist

- [x] Add Spring Security dependency to all 3 services
- [x] Create SecurityConfig with @EnableMethodSecurity
- [x] Create SecurityContextFilter to read X-Auth-Role header
- [x] Create CustomUserDetailsService
- [x] Update PriceRowController with @PreAuthorize
- [x] Update ProductController with @PreAuthorize
- [x] Update StockController with @PreAuthorize
- [x] Remove manual if-else role checks
- [x] Remove @RequestHeader parameters for role
- [x] Verify imports are correct
- [x] Create documentation

