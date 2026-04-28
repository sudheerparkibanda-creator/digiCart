package com.digiCart.cart_service.service;

import com.digiCart.cart_service.dto.PaymentLinkResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentLinkResponseListener {

    private final ObjectMapper objectMapper;
    private final PaymentServiceClient paymentServiceClient;

    public PaymentLinkResponseListener(ObjectMapper objectMapper,
                                       PaymentServiceClient paymentServiceClient) {
        this.objectMapper = objectMapper;
        this.paymentServiceClient = paymentServiceClient;
    }

    @KafkaListener(topics = "${app.kafka.topics.payment-link-response}", groupId = "${spring.application.name}")
    public void onMessage(String payload) {
        try {
            PaymentLinkResponseEvent responseEvent = objectMapper.readValue(payload, PaymentLinkResponseEvent.class);
            paymentServiceClient.completeResponse(responseEvent);
        } catch (Exception ignored) {
            // Ignore malformed events to keep listener resilient.
        }
    }
}

