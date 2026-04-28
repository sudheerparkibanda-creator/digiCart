package com.digiCart.order_service.kafka;

import com.digiCart.order_service.dto.OrderPlacedNotificationEvent;
import com.digiCart.order_service.dto.PaymentSuccessNotificationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventProducer {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String orderPlacedTopic;
    private final String paymentSuccessTopic;

    public NotificationEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                     ObjectMapper objectMapper,
                                     @Value("${app.kafka.topics.notification-order-placed}") String orderPlacedTopic,
                                     @Value("${app.kafka.topics.notification-payment-success}") String paymentSuccessTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.orderPlacedTopic = orderPlacedTopic;
        this.paymentSuccessTopic = paymentSuccessTopic;
    }

    public void publishOrderPlaced(OrderPlacedNotificationEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(orderPlacedTopic, event.getOrderId(), payload);
            log.info("Published order-placed notification event for orderId={}", event.getOrderId());
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize OrderPlacedNotificationEvent for orderId={}", event.getOrderId(), ex);
        }
    }

    public void publishPaymentSuccess(PaymentSuccessNotificationEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(paymentSuccessTopic, event.getOrderId(), payload);
            log.info("Published payment-success notification event for orderId={}", event.getOrderId());
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize PaymentSuccessNotificationEvent for orderId={}", event.getOrderId(), ex);
        }
    }
}

