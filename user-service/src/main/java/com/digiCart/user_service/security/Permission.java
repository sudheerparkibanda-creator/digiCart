package com.digiCart.user_service.security;

/**
 * Defines all permissions available in the e-commerce system.
 * Permissions are mapped to roles in the RolePermissionMapping.
 */
public enum Permission {
    // Product Management
    CREATE_PRODUCT("CREATE_PRODUCT"),
    UPDATE_PRODUCT("UPDATE_PRODUCT"),
    DELETE_PRODUCT("DELETE_PRODUCT"),
    VIEW_PRODUCT("VIEW_PRODUCT"),
    
    // Price Management
    CREATE_PRICE("CREATE_PRICE"),
    UPDATE_PRICE("UPDATE_PRICE"),
    DELETE_PRICE("DELETE_PRICE"),
    VIEW_PRICE("VIEW_PRICE"),
    
    // Stock Management
    CREATE_STOCK("CREATE_STOCK"),
    UPDATE_STOCK("UPDATE_STOCK"),
    DELETE_STOCK("DELETE_STOCK"),
    VIEW_STOCK("VIEW_STOCK"),
    
    // User Management
    VIEW_ALL_USERS("VIEW_ALL_USERS"),
    UPDATE_USER("UPDATE_USER"),
    DELETE_USER("DELETE_USER"),
    
    // Order Management
    CREATE_ORDER("CREATE_ORDER"),
    VIEW_ORDER("VIEW_ORDER"),
    VIEW_ALL_ORDERS("VIEW_ALL_ORDERS"),
    UPDATE_ORDER_STATUS("UPDATE_ORDER_STATUS"),
    
    // Cart Management
    CREATE_CART("CREATE_CART"),
    UPDATE_CART("UPDATE_CART"),
    VIEW_CART("VIEW_CART"),
    
    // Address Management
    CREATE_ADDRESS("CREATE_ADDRESS"),
    UPDATE_ADDRESS("UPDATE_ADDRESS"),
    DELETE_ADDRESS("DELETE_ADDRESS"),
    
    // Payment Management
    PROCESS_PAYMENT("PROCESS_PAYMENT"),
    VIEW_PAYMENT("VIEW_PAYMENT"),
    VIEW_ALL_PAYMENTS("VIEW_ALL_PAYMENTS"),
    
    // Analytics & Reports
    VIEW_ANALYTICS("VIEW_ANALYTICS"),
    VIEW_SALES_REPORT("VIEW_SALES_REPORT");

    private final String value;

    Permission(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

