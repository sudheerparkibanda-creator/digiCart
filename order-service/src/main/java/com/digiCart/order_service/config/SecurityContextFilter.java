package com.digiCart.order_service.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter to extract the X-Auth-Username header (from API Gateway) and set up the SecurityContext
 * This allows SecurityUtil to extract authenticated user information
 */
@Component
public class SecurityContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

        String internalAuth = request.getHeader("X-Internal-Auth");
        if (internalAuth != null && !internalAuth.isBlank()) {
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_INTERNAL"));
            var authentication = new UsernamePasswordAuthenticationToken(
                "internal-service",
                null,
                authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
            return;
        }

        String username = request.getHeader("X-Auth-Username");
        String userRole = request.getHeader("X-Auth-Role");

        if (username != null && !username.isBlank()) {
            String roleWithPrefix = userRole != null && !userRole.isBlank() && userRole.startsWith("ROLE_") 
                ? userRole 
                : "ROLE_" + (userRole != null ? userRole : "USER");
            var authorities = Collections.singletonList(new SimpleGrantedAuthority(roleWithPrefix));
            var authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
