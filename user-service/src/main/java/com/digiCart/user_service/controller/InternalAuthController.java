package com.digiCart.user_service.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digiCart.user_service.dto.ActivationRequest;
import com.digiCart.user_service.dto.AuthVerifyRequest;
import com.digiCart.user_service.dto.AuthVerifyResponse;
import com.digiCart.user_service.dto.RegisterUserRequest;
import com.digiCart.user_service.dto.RegisterUserResponse;
import com.digiCart.user_service.service.UserAuthService;

@RestController
@RequestMapping("/internal/auth")
public class InternalAuthController {

    private final UserAuthService userAuthService;

    public InternalAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/verify")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthVerifyResponse> verify(@RequestBody AuthVerifyRequest request) {
        AuthVerifyResponse response = userAuthService.verify(request);
        if (!response.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> register(@RequestBody RegisterUserRequest request) {
        try {
            RegisterUserResponse response = userAuthService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "User registered successfully. Please check your email for verification code.",
                            "uId", response.getUserId(),
                            "name", response.getName(),
                            "role", response.getRole()
                    ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/activate")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> activate(@RequestBody ActivationRequest request) {
        try {
            userAuthService.activateUser(request);
            return ResponseEntity.ok(Map.of("message", "User account activated successfully. You can now login."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}

