package com.digiCart.user_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityContextFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SecurityContextFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("Entering doFilterInternal with request URI: {}", request.getRequestURI());

        String username = request.getHeader("X-Auth-Username");
        String role = request.getHeader("X-Auth-Role");

        if (username != null && role != null) {
            // Normalize role to uppercase and add ROLE_ prefix if not present
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role.toUpperCase();
            }

            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    List.of(new SimpleGrantedAuthority(role))
                );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}