package com.digiCart.user_service.service;

import com.digiCart.user_service.dto.AuthVerifyResponse;
import com.digiCart.user_service.model.Role;
import com.digiCart.user_service.security.Permission;
import com.digiCart.user_service.security.RolePermissionMapping;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service to check user permissions and roles.
 * This centralizes authorization logic throughout the application.
 */
@Service
public class AuthorizationService {

    private final UserAuthService userAuthService;

    public AuthorizationService(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    private static final Logger log = LoggerFactory.getLogger(AuthorizationService.class);

    /**
     * Check if a user with given ID has a specific permission
     */
    public boolean hasPermission(String userId, Permission permission) {
        log.info("Entering hasPermission with userId: {}, permission: {}", userId, permission);
        return userAuthService.getUserById(userId)
            .map(user -> RolePermissionMapping.hasPermission(user.getRole(), permission))
            .orElse(false);
    }

    /**
     * Check if a user has any of the given permissions
     */
    public boolean hasAnyPermission(String userId, Permission... permissions) {
        log.info("Entering hasAnyPermission with userId: {}, permissions: {}", userId, permissions);
        return userAuthService.getUserById(userId)
            .map(user -> RolePermissionMapping.hasAnyPermission(user.getRole(), permissions))
            .orElse(false);
    }

    /**
     * Check if a user has all the given permissions
     */
    public boolean hasAllPermissions(String userId, Permission... permissions) {
        log.info("Entering hasAllPermissions with userId: {}, permissions: {}", userId, permissions);
        return userAuthService.getUserById(userId)
            .map(user -> RolePermissionMapping.hasAllPermissions(user.getRole(), permissions))
            .orElse(false);
    }

    /**
     * Check if a user is an Admin
     */
    public boolean isAdmin(String userId) {
        log.info("Entering isAdmin with userId: {}", userId);
        return userAuthService.getUserById(userId)
            .map(user -> user.getRole() == Role.Admin)
            .orElse(false);
    }

    /**
     * Check if a user is a Customer
     */
    public boolean isCustomer(String userId) {
        return userAuthService.getUserById(userId)
            .map(user -> user.getRole() == Role.Customer)
            .orElse(false);
    }

    /**
     * Verify if a user has permission, throws exception if not
     */
    public void requirePermission(String userId, Permission permission) throws UnauthorizedException {
        if (!hasPermission(userId, permission)) {
            throw new UnauthorizedException(
                String.format("User %s does not have permission: %s", userId, permission.getValue())
            );
        }
    }

    /**
     * Verify if a user is Admin, throws exception if not
     */
    public void requireAdmin(String userId) throws UnauthorizedException {
        if (!isAdmin(userId)) {
            throw new UnauthorizedException(
                String.format("User %s is not an Admin", userId)
            );
        }
    }

    /**
     * Get the role of a user
     */
    public Role getUserRole(String userId) {
        return userAuthService.getUserById(userId)
            .map(user -> user.getRole())
            .orElse(null);
    }

    /**
     * Custom exception for authorization failures
     */
    public static class UnauthorizedException extends Exception {
        public UnauthorizedException(String message) {
            super(message);
        }

        public UnauthorizedException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

