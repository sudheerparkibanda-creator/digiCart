package com.digiCart.cart_service.service;

import com.digiCart.cart_service.dto.PaymentLinkRequestEvent;
import com.digiCart.cart_service.dto.PaymentLinkResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentServiceClient {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String requestTopic;
    private final long timeoutMs;
    private final ConcurrentHashMap<String, CompletableFuture<PaymentLinkResponseEvent>> pendingRequests = new ConcurrentHashMap<>();

    public PaymentServiceClient(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper,
                                @Value("${app.kafka.topics.payment-link-request}") String requestTopic,
                                @Value("${app.kafka.request-timeout-ms:10000}") long timeoutMs) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.requestTopic = requestTopic;
        this.timeoutMs = timeoutMs;
    }

    public String ensurePaymentLink(String orderId) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<PaymentLinkResponseEvent> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);

        try {
            PaymentLinkRequestEvent requestEvent = new PaymentLinkRequestEvent();
            requestEvent.setRequestId(requestId);
            requestEvent.setOrderId(orderId);

            kafkaTemplate.send(requestTopic, orderId, objectMapper.writeValueAsString(requestEvent));

            PaymentLinkResponseEvent responseEvent = future.get(timeoutMs, TimeUnit.MILLISECONDS);
            if (responseEvent.getErrorCode() != null && !responseEvent.getErrorCode().isBlank()) {
                throw new IllegalStateException("Unable to generate payment link: " + responseEvent.getErrorMessage());
            }

            String paymentLink = responseEvent.getPaymentLink();
            if (paymentLink == null || paymentLink.isBlank()) {
                throw new IllegalStateException("Payment service did not return paymentLink");
            }
            return paymentLink;
        } catch (TimeoutException ex) {
            throw new IllegalStateException("Timed out while waiting for payment link response", ex);
        } catch (ExecutionException ex) {
            throw new IllegalStateException("Failed to receive payment link response", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for payment link response", ex);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to generate payment link", ex);
        } finally {
            pendingRequests.remove(requestId);
        }
    }

    void completeResponse(PaymentLinkResponseEvent responseEvent) {
        if (responseEvent == null || responseEvent.getRequestId() == null) {
            return;
        }
        CompletableFuture<PaymentLinkResponseEvent> future = pendingRequests.get(responseEvent.getRequestId());
        if (future != null) {
            future.complete(responseEvent);
        }
    }
}

