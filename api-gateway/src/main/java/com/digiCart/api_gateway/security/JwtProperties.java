package com.digiCart.api_gateway.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /** Plain-text secret (>= 32 chars).  Override in config-server api-gateway.yaml */
    private String secret = "digiCartDefaultSecretKeyChangeMe!!2026";

    /** Token validity in milliseconds (default: 24 h) */
    private long expirationMs = 86_400_000L;

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public long getExpirationMs() { return expirationMs; }
    public void setExpirationMs(long expirationMs) { this.expirationMs = expirationMs; }
}

