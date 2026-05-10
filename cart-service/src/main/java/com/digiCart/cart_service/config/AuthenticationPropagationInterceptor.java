package com.digiCart.cart_service.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Interceptor for RestTemplate to propagate authentication headers
 * to downstream microservices during inter-service calls.
 * 
 * This ensures that when cart-service calls order-service (or other services),
 * the authentication context from the incoming request is forwarded.
 */
@Component
public class AuthenticationPropagationInterceptor implements org.springframework.http.client.ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                       ClientHttpRequestExecution execution) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            request.getHeaders().set("X-Auth-Username", username);
            
            // Extract the first non-USER role
            String role = extractPrimaryRole(auth);
            if (role != null) {
                request.getHeaders().set("X-Auth-Role", role);
            }
        }
        
        return execution.execute(request, body);
    }

    private String extractPrimaryRole(Authentication auth) {
        for (GrantedAuthority authority : auth.getAuthorities()) {
            String role = authority.getAuthority();
            if (role != null && !role.equals("ROLE_USER")) {
                // Return role without ROLE_ prefix for consistency
                return role.startsWith("ROLE_") ? role.substring(5) : role;
            }
        }
        return null;
    }
}
