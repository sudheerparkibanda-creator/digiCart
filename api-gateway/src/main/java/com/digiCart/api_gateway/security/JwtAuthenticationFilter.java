package com.digiCart.api_gateway.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Intercepts every inbound request exactly once.
 * <p>
 * If a valid JWT is present in the {@code Authorization: Bearer …} header:
 * <ul>
 *   <li>Sets the SecurityContext so downstream security checks pass.</li>
 *   <li>Injects {@code X-Auth-Username} into the forwarded request so
 *       downstream micro-services know the authenticated caller without
 *       needing their own JWT validation.</li>
 * </ul>
 * Requests without a token are left unchanged and blocked by the
 * {@link SecurityConfig} filter-chain unless the path is explicitly permitted.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_USERNAME_HEADER = "X-Auth-Username";
    private static final String AUTH_ROLE_HEADER = "X-Auth-Role";

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /** Skip the filter entirely for the auth endpoints. */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/") || path.startsWith("/actuator/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            // No token → proceed; SecurityConfig will reject if the path needs auth
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        if (jwtUtil.validateToken(token)) {
            String username = jwtUtil.extractUsername(token);
            String rawRole = jwtUtil.extractRole(token);
            String normalizedRole = normalizeRole(rawRole);

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            authorities.add(new SimpleGrantedAuthority("ROLE_" + normalizedRole));

            // Set authentication in context (stateless — no session)
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    username, null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Inject identity headers so downstream services know the caller.
            MutableHttpServletRequest mutable = new MutableHttpServletRequest(request);
            mutable.putHeader(AUTH_USERNAME_HEADER, username);
            mutable.putHeader(AUTH_ROLE_HEADER, normalizedRole);
            chain.doFilter(mutable, response);
        } else {
            // Token present but invalid → 401 immediately
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
        }
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "CUSTOMER";
        }
        return role.toUpperCase(Locale.ROOT);
    }
}

