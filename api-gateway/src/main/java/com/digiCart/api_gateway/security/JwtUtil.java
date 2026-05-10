package com.digiCart.api_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private static final String ROLE_CLAIM = "role";

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(JwtProperties props) {
        this.key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
        this.expirationMs = props.getExpirationMs();
    }

    /** Generate a signed JWT for the given username and role. */
    public String generateToken(String username, String role) {
        log.info("Entering generateToken with username: {}, role: {}", username, role);
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(username)
                .claim(ROLE_CLAIM, role)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .signWith(key)
                .compact();
    }

    /** Extract the username (subject) from a token; throws if invalid. */
    public String extractUsername(String token) {
        log.info("Entering extractUsername with token: {}", token);
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        log.info("Entering extractRole with token: {}", token);
        return parseClaims(token).get(ROLE_CLAIM, String.class);
    }

    /** Returns true only if the token signature is valid and it has not expired. */
    public boolean validateToken(String token) {
        log.info("Entering validateToken with token: {}", token);
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

