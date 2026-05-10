package com.digiCart.user_service.controller;

import com.digiCart.user_service.service.UserAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final UserAuthService userAuthService;

    public InternalUserController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    private static final Logger log = LoggerFactory.getLogger(InternalUserController.class);

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> getUserById(@PathVariable String userId) {
        log.info("Entering getUserById with userId: {}", userId);
        return userAuthService.getUserById(userId)
                .map(user -> ResponseEntity.ok(Map.of(
                        "userId", user.getUId(),
                        "username", user.getName(),
                        "email", user.getName()
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> addAddressToUser(@PathVariable String userId,
                                                 @PathVariable String addressId) {
        log.info("Entering addAddressToUser with userId: {}, addressId: {}", userId, addressId);
        userAuthService.addAddressToUser(userId, addressId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/default-address/{addressId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> setDefaultAddressForUser(@PathVariable String userId,
                                                         @PathVariable String addressId) {
        log.info("Entering setDefaultAddressForUser with userId: {}, addressId: {}", userId, addressId);
        userAuthService.setDefaultAddressForUser(userId, addressId);
        return ResponseEntity.ok().build();
    }
}
