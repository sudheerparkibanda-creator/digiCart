package com.digiCart.user_service.controller;

import com.digiCart.user_service.dto.AuthVerifyRequest;
import com.digiCart.user_service.dto.AuthVerifyResponse;
import com.digiCart.user_service.dto.RegisterUserRequest;
import com.digiCart.user_service.dto.RegisterUserResponse;
import com.digiCart.user_service.service.UserAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/internal/auth")
public class InternalAuthController {

    private final UserAuthService userAuthService;

    public InternalAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthVerifyResponse> verify(@RequestBody AuthVerifyRequest request) {
        AuthVerifyResponse response = userAuthService.verify(request);
        if (!response.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserRequest request) {
        try {
            RegisterUserResponse response = userAuthService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}

