package com.digiCart.user_service.security;

import com.digiCart.user_service.service.AuthorizationService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AOP aspect to enforce permission checks based on @RequirePermission annotation.
 * This intercepts method calls and validates user permissions before execution.
 */
@Aspect
@Component
public class PermissionCheckAspect {

    private final AuthorizationService authorizationService;

    public PermissionCheckAspect(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        // Get user ID from request header
        String userId = getUserIdFromRequest();

        if (userId == null) {
            throw new AuthorizationService.UnauthorizedException("User ID not found in request headers");
        }

        Permission[] permissions = requirePermission.value();
        boolean requireAll = requirePermission.requireAll();

        if (requireAll) {
            authorizationService.requirePermission(userId, permissions[0]);
            for (Permission permission : permissions) {
                if (!authorizationService.hasPermission(userId, permission)) {
                    throw new AuthorizationService.UnauthorizedException(
                        String.format("User %s missing required permission: %s", userId, permission.getValue())
                    );
                }
            }
        } else {
            if (!authorizationService.hasAnyPermission(userId, permissions)) {
                throw new AuthorizationService.UnauthorizedException(
                    String.format("User %s does not have any of the required permissions", userId)
                );
            }
        }
    }

    private String getUserIdFromRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest().getHeader("X-User-Id");
            }
        } catch (Exception e) {
            // Ignore - user ID might not be available in all contexts
        }
        return null;
    }
}

