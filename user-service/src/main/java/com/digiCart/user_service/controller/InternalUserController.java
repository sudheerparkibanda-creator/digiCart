package com.digiCart.user_service.controller;

import com.digiCart.user_service.service.UserAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final UserAuthService userAuthService;

    public InternalUserController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, String>> getUserById(@PathVariable String userId) {
        return userAuthService.getUserById(userId)
                .map(user -> ResponseEntity.ok(Map.of(
                        "userId", user.getUId(),
                        "username", user.getName(),
                        "email", user.getName()
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
