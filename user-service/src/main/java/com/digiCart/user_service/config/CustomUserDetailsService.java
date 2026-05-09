package com.digiCart.user_service.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // This is a placeholder - in a real app, you'd load from database
        // For now, return a dummy user - the SecurityContextFilter takes precedence
        return User.withUsername(username)
                .password("")
                .roles("USER")
                .build();
    }
}