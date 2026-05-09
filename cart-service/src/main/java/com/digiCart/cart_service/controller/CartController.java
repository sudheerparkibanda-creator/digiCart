package com.digiCart.cart_service.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digiCart.cart_service.dto.AddToCartRequest;
import com.digiCart.cart_service.dto.CartResponse;
import com.digiCart.cart_service.dto.EntryQuantityRequest;
import com.digiCart.cart_service.dto.PlaceOrderResponse;
import com.digiCart.cart_service.dto.SetExistingDeliveryAddressRequest;
import com.digiCart.cart_service.dto.SetNewDeliveryAddressRequest;
import com.digiCart.cart_service.service.CartService;
import com.digiCart.cart_service.util.SecurityUtil;

@RestController
@RequestMapping("/{customerId}/carts")
public class CartController {

    private final CartService cartService;
    private final SecurityUtil securityUtil;

    public CartController(CartService cartService, SecurityUtil securityUtil) {
        this.cartService = cartService;
        this.securityUtil = securityUtil;
    }

    @PostMapping
    public ResponseEntity<CartResponse> createCart(@PathVariable String customerId) {
        try {
            securityUtil.verifyUserMatch(customerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(cartService.createCart(customerId));
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<CartResponse>> getAllCartsForCustomer(@PathVariable String customerId) {
        try {
            securityUtil.verifyUserMatch(customerId);
            return ResponseEntity.ok(cartService.getAllCartsForCustomer(customerId));
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{cartId}/delivery-address/new")
    public ResponseEntity<CartResponse> setDeliveryAddressFromNewAddress(@PathVariable String customerId,
                                                                          @PathVariable String cartId,
                                                                          @RequestBody SetNewDeliveryAddressRequest request) {
        try {
            securityUtil.verifyUserMatch(customerId);
            return ResponseEntity.ok(cartService.setDeliveryAddressFromNewAddress(customerId, cartId, request));
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{cartId}/delivery-address")
    public ResponseEntity<CartResponse> setDeliveryAddressFromExistingAddress(@PathVariable String customerId,
                                                                               @PathVariable String cartId,
                                                                               @RequestBody SetExistingDeliveryAddressRequest request) {
        try {
            securityUtil.verifyUserMatch(customerId);
            return ResponseEntity.ok(cartService.setDeliveryAddressFromExistingAddress(customerId, cartId, request));
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/{cartId}/add")
    public ResponseEntity<CartResponse> addToCart(@PathVariable String customerId,
                                                  @PathVariable String cartId,
                                                  @RequestBody AddToCartRequest request) {
        try {
            securityUtil.verifyUserMatch(customerId);
            return ResponseEntity.ok(cartService.addToCart(customerId, cartId, request));
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/{cartId}/place-order")
    public ResponseEntity<PlaceOrderResponse> placeOrder(@PathVariable String customerId,
                                                         @PathVariable String cartId) {
        try {
            securityUtil.verifyUserMatch(customerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(cartService.placeOrder(customerId, cartId));
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/{cartId}/entries/{entryNumber}/add-quantity")
    public ResponseEntity<CartResponse> addEntryQuantity(@PathVariable String customerId,
                                                         @PathVariable String cartId,
                                                         @PathVariable Integer entryNumber,
                                                         @RequestBody EntryQuantityRequest request) {
        try {
            securityUtil.verifyUserMatch(customerId);
            return ResponseEntity.ok(cartService.addEntryQuantity(customerId, cartId, entryNumber, request));
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/{cartId}/entries/{entryNumber}/remove-quantity")
    public ResponseEntity<CartResponse> removeEntryQuantity(@PathVariable String customerId,
                                                            @PathVariable String cartId,
                                                            @PathVariable Integer entryNumber,
                                                            @RequestBody EntryQuantityRequest request) {
        try {
            securityUtil.verifyUserMatch(customerId);
            return ResponseEntity.ok(cartService.removeEntryQuantity(customerId, cartId, entryNumber, request));
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{cartId}/entries/{entryNumber}")
    public ResponseEntity<CartResponse> removeEntry(@PathVariable String customerId,
                                                    @PathVariable String cartId,
                                                    @PathVariable Integer entryNumber) {
        try {
            securityUtil.verifyUserMatch(customerId);
            return ResponseEntity.ok(cartService.removeEntryByEntryNumber(customerId, cartId, entryNumber));
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCart(@PathVariable String customerId,
                                           @PathVariable String cartId) {
        try {
            securityUtil.verifyUserMatch(customerId);
            cartService.deleteCart(customerId, cartId);
            return ResponseEntity.noContent().build();
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

