package com.digiCart.payment_service.controller;

import com.digiCart.payment_service.dto.EnsurePaymentLinkResponse;
import com.digiCart.payment_service.service.PaymentLinkService;
import com.digiCart.payment_service.service.RazorpayWebhookVerifier;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentLinkService paymentLinkService;
    private final RazorpayWebhookVerifier razorpayWebhookVerifier;

    public PaymentController(PaymentLinkService paymentLinkService,
                             RazorpayWebhookVerifier razorpayWebhookVerifier) {
        this.paymentLinkService = paymentLinkService;
        this.razorpayWebhookVerifier = razorpayWebhookVerifier;
    }

    @PostMapping("/orders/{orderId}/payment-link")
    public ResponseEntity<EnsurePaymentLinkResponse> ensurePaymentLink(@PathVariable String orderId) {
        try {
            return ResponseEntity.ok(paymentLinkService.ensurePaymentLink(orderId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/webhooks/razorpay")
    public ResponseEntity<Void> handleRazorpayWebhook(@RequestHeader(name = "X-Razorpay-Signature", required = false)
                                                      String signature,
                                                      @RequestBody String rawPayload) {
        if (!razorpayWebhookVerifier.isValidSignature(rawPayload, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            paymentLinkService.processWebhook(rawPayload);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}

