package com.digiCart.payment_service.service;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RazorpayClient {

    private static final Logger log = LoggerFactory.getLogger(RazorpayClient.class);

    private final RestTemplate restTemplate;
    private final String apiBaseUrl;
    private final String keyId;
    private final String keySecret;

    public RazorpayClient(RestTemplateBuilder restTemplateBuilder,
                          @Value("${razorpay.api-base-url:https://api.razorpay.com/v1}") String apiBaseUrl,
                          @Value("${razorpay.key-id}") String keyId,
                          @Value("${razorpay.key-secret}") String keySecret) {
        this.restTemplate = restTemplateBuilder.build();
        this.apiBaseUrl = apiBaseUrl;
        this.keyId = keyId;
        this.keySecret = keySecret;
    }

    public String createPaymentLink(String orderId, Double amount) {
        log.info("Entering createPaymentLink with orderId: {}, amount: {}", orderId, amount);
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("amount", Math.round(amount * 100));
        payload.put("currency", "INR");
        payload.put("accept_partial", false);
        payload.put("description", "Payment for order " + orderId);
        payload.put("reference_id", orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(keyId, keySecret);

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            Map<?, ?> response = restTemplate.postForObject(apiBaseUrl + "/payment_links", request, Map.class);
            Object shortUrl = response == null ? null : response.get("short_url");
            if (shortUrl == null || shortUrl.toString().isBlank()) {
                throw new IllegalStateException("Razorpay response does not include short_url");
            }
            return shortUrl.toString();
        } catch (RestClientException ex) {
            throw new IllegalStateException("Unable to create Razorpay payment link", ex);
        }
    }
}

