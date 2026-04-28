package com.digiCart.api_gateway.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-gateway UserDetailsService used only during the /auth/login flow to
 * validate credentials before issuing a JWT.
 *
 * <p>After a token is issued, subsequent requests are authenticated purely
 * by JWT signature/expiry validation — this service is NOT called on every
 * request (stateless design).
 *
 * <p><b>Extension point:</b> Replace the in-memory map with an HTTP call to
 * {@code user-service} (e.g. via RestTemplate / FeignClient) to validate
 * credentials against the real user store.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // username → hashed password
    private final Map<String, UserDetails> store = new ConcurrentHashMap<>();

    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        // ── Seed users (replace / extend with user-service HTTP call) ────────
        register("admin",  passwordEncoder.encode("admin123"),  "ADMIN", "USER");
        register("user",   passwordEncoder.encode("user123"),   "USER");
    }

    /** Programmatically add a user (e.g. called from /auth/register). */
    public void addUser(String username, String hashedPassword, String... roles) {
        if (store.containsKey(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        register(username, hashedPassword, roles);
    }

    public boolean existsByUsername(String username) {
        return store.containsKey(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails details = store.get(username);
        if (details == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return details;
    }

    private void register(String username, String hashedPassword, String... roles) {
        store.put(username,
                User.withUsername(username)
                        .password(hashedPassword)
                        .roles(roles)
                        .build());
    }
}

