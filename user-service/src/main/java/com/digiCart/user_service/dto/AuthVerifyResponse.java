package com.digiCart.user_service.dto;

public class AuthVerifyResponse {
    private boolean authenticated;
    private String username;
    private String role;

    public AuthVerifyResponse() {
    }

    public AuthVerifyResponse(boolean authenticated, String username, String role) {
        this.authenticated = authenticated;
        this.username = username;
        this.role = role;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

