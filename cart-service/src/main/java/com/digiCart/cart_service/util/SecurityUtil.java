package com.digiCart.cart_service.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility for extracting and validating security context information.
 */
@Component
public class SecurityUtil {

    /**
     * Get the authenticated username from the security context.
     * @return the authenticated username or null if not authenticated
     */
    public String getAuthenticatedUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return null;
    }

    /**
     * Verify that the given customerId matches the authenticated user.
     * @param customerId the customer ID from the request path
     * @throws SecurityException if the customerId does not match the authenticated user
     */
    public void verifyUserMatch(String customerId) {
        String authenticatedUser = getAuthenticatedUsername();
        if (authenticatedUser == null) {
            throw new SecurityException("User not authenticated");
        }
        if (!customerId.equals(authenticatedUser)) {
            throw new SecurityException("Access denied: customer ID does not match authenticated user");
        }
    }
}
