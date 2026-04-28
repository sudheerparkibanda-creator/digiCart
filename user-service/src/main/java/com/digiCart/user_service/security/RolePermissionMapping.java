package com.digiCart.user_service.security;

import com.digiCart.user_service.model.Role;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Maps roles to their allowed permissions.
 * This is the central place to define what each role can do.
 */
public class RolePermissionMapping {

    private static final Map<Role, Set<Permission>> rolePermissions = new HashMap<>();

    static {
        // Admin can do everything
        rolePermissions.put(Role.Admin, EnumSet.allOf(Permission.class));

        // Customer has limited permissions
        Set<Permission> customerPermissions = EnumSet.of(
            // Product & Price viewing
            Permission.VIEW_PRODUCT,
            Permission.VIEW_PRICE,
            Permission.VIEW_STOCK,
            
            // Order management - their own orders
            Permission.CREATE_ORDER,
            Permission.VIEW_ORDER,
            
            // Cart management
            Permission.CREATE_CART,
            Permission.UPDATE_CART,
            Permission.VIEW_CART,
            
            // Address management - their own addresses
            Permission.CREATE_ADDRESS,
            Permission.UPDATE_ADDRESS,
            Permission.DELETE_ADDRESS,
            
            // Payment - their own payments
            Permission.PROCESS_PAYMENT,
            Permission.VIEW_PAYMENT
        );
        rolePermissions.put(Role.Customer, customerPermissions);
    }

    /**
     * Get all permissions for a given role
     */
    public static Set<Permission> getPermissions(Role role) {
        return rolePermissions.getOrDefault(role, EnumSet.noneOf(Permission.class));
    }

    /**
     * Check if a role has a specific permission
     */
    public static boolean hasPermission(Role role, Permission permission) {
        return getPermissions(role).contains(permission);
    }

    /**
     * Check if a role has any of the given permissions
     */
    public static boolean hasAnyPermission(Role role, Permission... permissions) {
        Set<Permission> rolePerms = getPermissions(role);
        for (Permission permission : permissions) {
            if (rolePerms.contains(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a role has all the given permissions
     */
    public static boolean hasAllPermissions(Role role, Permission... permissions) {
        Set<Permission> rolePerms = getPermissions(role);
        for (Permission permission : permissions) {
            if (!rolePerms.contains(permission)) {
                return false;
            }
        }
        return true;
    }
}

