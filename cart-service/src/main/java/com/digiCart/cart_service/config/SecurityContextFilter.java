package com.digiCart.cart_service.config;

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
 * Filter to extract the X-Auth-Username header (from API Gateway) and set up the SecurityContext
 * This allows SecurityUtil to extract authenticated user information
 */
@Component
public class SecurityContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

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
