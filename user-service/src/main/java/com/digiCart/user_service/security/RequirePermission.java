package com.digiCart.user_service.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enforce permission checks on controller methods or services.
 * Can be used with Spring AOP for aspect-based authorization.
 * 
 * Example:
 *   @RequirePermission(Permission.CREATE_PRODUCT)
 *   public Product addProduct(@RequestBody Product product) { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    Permission[] value();
    
    // If true, user must have ALL permissions. If false, any ONE permission is sufficient.
    boolean requireAll() default false;
}

