package com.digiCart.notification_service.kafka;

import com.digiCart.notification_service.dto.OrderPlacedNotificationEvent;
import com.digiCart.notification_service.dto.PaymentSuccessNotificationEvent;
import com.digiCart.notification_service.dto.UserRegistrationNotificationEvent;
import com.digiCart.notification_service.service.NotificationEmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final NotificationEmailService notificationEmailService;

    public NotificationEventConsumer(ObjectMapper objectMapper,
                                     NotificationEmailService notificationEmailService) {
        this.objectMapper = objectMapper;
        this.notificationEmailService = notificationEmailService;
    }

    @KafkaListener(topics = "${app.kafka.topics.notification-order-placed}", groupId = "${spring.application.name}")
    public void onOrderPlaced(String payload) {
        try {
            OrderPlacedNotificationEvent event = objectMapper.readValue(payload, OrderPlacedNotificationEvent.class);
            log.info("Received order-placed notification event for orderId={}", event.getOrderId());
            notificationEmailService.sendOrderPlacedEmail(event);
        } catch (Exception ex) {
            log.error("Failed to process order-placed notification event: {}", payload, ex);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.notification-payment-success}", groupId = "${spring.application.name}")
    public void onPaymentSuccess(String payload) {
        try {
            PaymentSuccessNotificationEvent event = objectMapper.readValue(payload, PaymentSuccessNotificationEvent.class);
            log.info("Received payment-success notification event for orderId={}", event.getOrderId());
            notificationEmailService.sendPaymentSuccessEmail(event);
        } catch (Exception ex) {
            log.error("Failed to process payment-success notification event: {}", payload, ex);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.user-registration:user-registration}", groupId = "${spring.application.name}")
    public void onUserRegistration(String payload) {
        try {
            UserRegistrationNotificationEvent event = objectMapper.readValue(payload, UserRegistrationNotificationEvent.class);
            log.info("Received user-registration notification event for email={}", event.getEmail());
            notificationEmailService.sendUserRegistrationEmail(event);
        } catch (Exception ex) {
            log.error("Failed to process user-registration notification event: {}", payload, ex);
        }
    }
}

