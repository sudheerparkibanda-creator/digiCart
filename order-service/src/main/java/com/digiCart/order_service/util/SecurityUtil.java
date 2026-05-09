package com.digiCart.order_service.util;

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
     * Verify that the authenticated user matches the given userId.
     * @param userId the user ID to verify against
     * @throws SecurityException if the userId does not match the authenticated user
     */
    public void verifyUserMatch(String userId) {
        String authenticatedUser = getAuthenticatedUsername();
        if (authenticatedUser == null) {
            throw new SecurityException("User not authenticated");
        }
        if (!userId.equals(authenticatedUser)) {
            throw new SecurityException("Access denied: user ID does not match authenticated user");
        }
    }
}
