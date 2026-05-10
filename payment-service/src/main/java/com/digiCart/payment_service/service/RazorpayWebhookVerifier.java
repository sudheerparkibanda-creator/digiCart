package com.digiCart.payment_service.service;

import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RazorpayWebhookVerifier {

    private static final Logger log = LoggerFactory.getLogger(RazorpayWebhookVerifier.class);

    private final String webhookSecret;

    public RazorpayWebhookVerifier(@Value("${razorpay.webhook-secret}") String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public boolean isValidSignature(String rawPayload, String signatureHeader) {
        log.info("Entering isValidSignature with rawPayload length: {}, signatureHeader: {}", rawPayload != null ? rawPayload.length() : 0, signatureHeader);
        if (rawPayload == null || signatureHeader == null || signatureHeader.isBlank()) {
            return false;
        }
        try {
            String computed = hmacSha256(rawPayload, webhookSecret);
            return secureEquals(computed, signatureHeader);
        } catch (Exception ex) {
            return false;
        }
    }

    private String hmacSha256(String payload, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder(digest.length * 2);
        for (byte b : digest) {
            String part = Integer.toHexString(b & 0xff);
            if (part.length() == 1) {
                hex.append('0');
            }
            hex.append(part);
        }
        return hex.toString();
    }

    private boolean secureEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            diff |= a.charAt(i) ^ b.charAt(i);
        }
        return diff == 0;
    }
}

