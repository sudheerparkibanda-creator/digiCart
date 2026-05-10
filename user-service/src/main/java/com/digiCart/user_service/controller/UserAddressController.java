package com.digiCart.user_service.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digiCart.user_service.service.UserAuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/users")
public class UserAddressController {

    private final UserAuthService userAuthService;

    public UserAddressController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    private static final Logger log = LoggerFactory.getLogger(UserAddressController.class);

    /**
     * Add address to user's address list.
     * Called by cart-service via RestTemplate when adding an item with a delivery address.
     * 
     * @param customerId user ID (email)
     * @param addressId address ID to add
     * @return success response with 200 OK
     */
    @PostMapping("/{customerId}/addresses/{addressId}")
    public ResponseEntity<?> addAddressToCustomer(@PathVariable String customerId,
                                                   @PathVariable String addressId) {
        log.info("Entering addAddressToCustomer with customerId: {}, addressId: {}", customerId, addressId);
        try {
            userAuthService.addAddressToUser(customerId, addressId);
            return ResponseEntity.ok(Map.of("message", "Address added to customer successfully"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add address to customer"));
        }
    }

    /**
     * Set default address for user.
     * 
     * @param customerId user ID (email)
     * @param addressId address ID to set as default
     * @return success response with 200 OK
     */
    @PutMapping("/{customerId}/default-address/{addressId}")
    public ResponseEntity<?> setDefaultAddress(@PathVariable String customerId,
                                               @PathVariable String addressId) {
        log.info("Entering setDefaultAddress with customerId: {}, addressId: {}", customerId, addressId);
        try {
            userAuthService.setDefaultAddressForUser(customerId, addressId);
            return ResponseEntity.ok(Map.of("message", "Default address set successfully"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to set default address"));
        }
    }
}
