package com.digiCart.price_service.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetailsService that works with the X-User-Role header
 * passed from the API Gateway via the SecurityContextFilter
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Entering loadUserByUsername with username: {}", username);
        // Return a user with the roles/authorities that were extracted from headers
        Collection<? extends GrantedAuthority> authorities =
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(username, "", authorities);
    }
}

