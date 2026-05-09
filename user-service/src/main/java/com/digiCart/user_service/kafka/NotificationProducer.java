package com.digiCart.user_service.kafka;

import com.digiCart.user_service.dto.UserRegistrationNotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.user-registration:user-registration}")
    private String userRegistrationTopic;

    public NotificationProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserRegistrationEvent(UserRegistrationNotificationEvent event) {
        try {
            log.info("Publishing user registration event for email: {}", event.getEmail());
            kafkaTemplate.send(userRegistrationTopic, event.getEmail(), event);
            log.info("User registration event published successfully for email: {}", event.getEmail());
        } catch (Exception ex) {
            log.error("Failed to publish user registration event for email: {}", event.getEmail(), ex);
            throw new RuntimeException("Failed to send notification event", ex);
        }
    }
}
