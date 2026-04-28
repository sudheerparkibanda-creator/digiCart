package com.digiCart.notification_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderServiceClient {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public OrderServiceClient(RestTemplateBuilder builder,
                              @Value("${order-service.base-url:http://localhost:8087}") String baseUrl) {
        this.restTemplate = builder.build();
        this.baseUrl = baseUrl;
    }

    public void markOrderPlacedEmailSent(String orderId) {
        try {
            restTemplate.exchange(
                    baseUrl + "/orders/" + orderId + "/order-placed-email-sent",
                    HttpMethod.PATCH,
                    null,
                    Void.class
            );
            log.info("Marked order-placed email sent for orderId={}", orderId);
        } catch (RestClientException ex) {
            log.error("Failed to mark order-placed email sent for orderId={}", orderId, ex);
        }
    }

    public void markPaymentSuccessEmailSent(String orderId) {
        try {
            restTemplate.exchange(
                    baseUrl + "/orders/" + orderId + "/payment-success-email-sent",
                    HttpMethod.PATCH,
                    null,
                    Void.class
            );
            log.info("Marked payment-success email sent for orderId={}", orderId);
        } catch (RestClientException ex) {
            log.error("Failed to mark payment-success email sent for orderId={}", orderId, ex);
        }
    }
}

