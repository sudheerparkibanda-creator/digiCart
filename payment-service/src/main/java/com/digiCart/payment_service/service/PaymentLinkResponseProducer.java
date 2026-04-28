package com.digiCart.payment_service.service;

import com.digiCart.payment_service.dto.PaymentLinkResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentLinkResponseProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String responseTopic;

    public PaymentLinkResponseProducer(KafkaTemplate<String, String> kafkaTemplate,
                                       ObjectMapper objectMapper,
                                       @Value("${app.kafka.topics.payment-link-response}") String responseTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.responseTopic = responseTopic;
    }

    public void publish(PaymentLinkResponseEvent responseEvent) {
        try {
            String payload = objectMapper.writeValueAsString(responseEvent);
            kafkaTemplate.send(responseTopic, responseEvent.getOrderId(), payload);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to publish payment link response", ex);
        }
    }
}

