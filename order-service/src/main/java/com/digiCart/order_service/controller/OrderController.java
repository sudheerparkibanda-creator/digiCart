package com.digiCart.order_service.controller;

import com.digiCart.order_service.dto.CreateOrderRequest;
import com.digiCart.order_service.dto.CapturePaymentRequest;
import com.digiCart.order_service.dto.OrderResponse;
import com.digiCart.order_service.dto.UpdatePaymentLinkRequest;
import com.digiCart.order_service.service.OrderService;
import java.net.URI;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            OrderResponse response = orderService.createOrder(request);
            return ResponseEntity.created(URI.create("/orders/" + response.getOrderId())).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        try {
            return ResponseEntity.ok(orderService.getOrder(orderId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/{orderId}/payment-link")
    public ResponseEntity<OrderResponse> setPaymentLinkIfMissing(@PathVariable String orderId,
                                                                 @RequestBody UpdatePaymentLinkRequest request) {
        try {
            return ResponseEntity.ok(orderService.setPaymentLinkIfMissing(orderId, request.getPaymentLink()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/{orderId}/payment-captured")
    public ResponseEntity<OrderResponse> markPaymentCaptured(@PathVariable String orderId,
                                                             @RequestBody CapturePaymentRequest request) {
        try {
            return ResponseEntity.ok(orderService.markPaymentCaptured(orderId, request.getPaymentId()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/{orderId}/order-placed-email-sent")
    public ResponseEntity<OrderResponse> markOrderPlacedEmailSent(@PathVariable String orderId) {
        try {
            return ResponseEntity.ok(orderService.markOrderPlacedEmailSent(orderId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/{orderId}/payment-success-email-sent")
    public ResponseEntity<OrderResponse> markPaymentSuccessEmailSent(@PathVariable String orderId) {
        try {
            return ResponseEntity.ok(orderService.markPaymentSuccessEmailSent(orderId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

