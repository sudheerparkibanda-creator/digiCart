package com.digiCart.api_gateway.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.digiCart.api_gateway.client.UserAuthClient;
import com.digiCart.api_gateway.dto.ActivationRequest;
import com.digiCart.api_gateway.dto.AuthResponse;
import com.digiCart.api_gateway.dto.LoginRequest;
import com.digiCart.api_gateway.dto.RegisterRequest;
import com.digiCart.api_gateway.dto.UserAuthVerifyResponse;
import com.digiCart.api_gateway.dto.UserRegisterResponse;
import com.digiCart.api_gateway.security.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserAuthClient userAuthClient;
    private final JwtUtil jwtUtil;

    public AuthController(UserAuthClient userAuthClient,
                          JwtUtil jwtUtil) {
        this.userAuthClient = userAuthClient;
        this.jwtUtil = jwtUtil;
    }

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    /**
     * Authenticate with username + password and receive a JWT.
     *
     * <pre>
     * POST /auth/login
     * { "username": "admin", "password": "admin123" }
     * </pre>
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("Entering login with request: {}", request);
        UserAuthVerifyResponse verification = userAuthClient.verifyCredentials(request);
        if (verification == null || !verification.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }

        String token = jwtUtil.generateToken(verification.getUsername(), verification.getRole());
        return ResponseEntity.ok(new AuthResponse(token, verification.getUsername(), verification.getRole()));
    }

    /**
     * Register a new user.  The token is returned immediately so the client
     * can start making authenticated calls right away.
     *
     * <pre>
     * POST /auth/register
     * { "email": "alice@example.com", "name": "Alice", "password": "secret" }
     * </pre>
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        log.info("Entering register with request: {}", request);
        if (request.getEmail() == null || request.getEmail().isBlank()
                || request.getName() == null || request.getName().isBlank()
                || request.getPassword() == null || request.getPassword().length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email, name, and password are required; password must be >= 6 chars"));
        }

        UserRegisterResponse registerResponse;
        try {
            registerResponse = userAuthClient.registerUser(request);
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email already exists or registration failed"));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Registration successful. Please check your email to verify your account.", 
                        "email", request.getEmail()));
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activate(@RequestBody ActivationRequest request) {
        log.info("Entering activate with request: {}", request);
        if (request.getEmail() == null || request.getEmail().isBlank()
                || request.getVerificationCode() == null || request.getVerificationCode().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email and verification code are required"));
        }

        try {
            userAuthClient.activateUser(request);
            return ResponseEntity.ok(Map.of(
                    "message", "User account activated successfully. You can now login."));
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid email or activation code"));
            }
            return ResponseEntity.status(ex.getStatusCode())
                    .body(Map.of("error", "Activation failed"));
        }
    }
}

