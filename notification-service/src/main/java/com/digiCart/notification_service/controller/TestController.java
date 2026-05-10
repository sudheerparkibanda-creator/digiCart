package com.digiCart.notification_service.controller;

import com.digiCart.notification_service.dto.OrderPlacedNotificationEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @PostMapping("/send-order-notification")
    public ResponseEntity<String> sendOrderNotification(@RequestBody OrderPlacedNotificationEvent event) {
        log.info("Entering sendOrderNotification with event: {}", event);
        try {
            // Just log the event for testing, don't actually send email
            System.out.println("Received notification event: " + event);
            return ResponseEntity.ok("Notification test successful - event received: " + event.getOrderId() + ", email: " + event.getUserEmail());
        } catch (Exception e) {
            System.out.println("Error processing event: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to process notification: " + e.getMessage());
        }
    }
}