package com.digiCart.payment_service.service;

import com.digiCart.payment_service.dto.EnsurePaymentLinkResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class PaymentLinkService {

    private final OrderServiceClient orderServiceClient;
    private final RazorpayClient razorpayClient;
    private final ObjectMapper objectMapper;

    public PaymentLinkService(OrderServiceClient orderServiceClient,
                              RazorpayClient razorpayClient,
                              ObjectMapper objectMapper) {
        this.orderServiceClient = orderServiceClient;
        this.razorpayClient = razorpayClient;
        this.objectMapper = objectMapper;
    }

    public EnsurePaymentLinkResponse ensurePaymentLink(String orderId) {
        OrderServiceClient.OrderData order = orderServiceClient.getOrder(orderId);

        if (order.getPaymentLink() != null && !order.getPaymentLink().isBlank()) {
            EnsurePaymentLinkResponse response = new EnsurePaymentLinkResponse();
            response.setOrderId(order.getOrderId());
            response.setPaymentLink(order.getPaymentLink());
            response.setStatus(order.getStatus());
            response.setCreated(Boolean.FALSE);
            return response;
        }

        String paymentLink = razorpayClient.createPaymentLink(order.getOrderId(), order.getTotal());
        OrderServiceClient.OrderData updatedOrder = orderServiceClient.setPaymentLinkIfMissing(order.getOrderId(), paymentLink);

        EnsurePaymentLinkResponse response = new EnsurePaymentLinkResponse();
        response.setOrderId(updatedOrder.getOrderId());
        response.setPaymentLink(updatedOrder.getPaymentLink());
        response.setStatus(updatedOrder.getStatus());
        response.setCreated(Boolean.TRUE);
        return response;
    }

    public void processWebhook(String rawPayload) {
        Map<String, Object> event = parsePayload(rawPayload);
        String eventType = asString(event.get("event"));

        if (!"payment_link.paid".equals(eventType) && !"payment.captured".equals(eventType)) {
            return;
        }

        String orderId = extractOrderId(event);
        String paymentId = extractPaymentId(event);

        if (orderId == null || orderId.isBlank() || paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("Webhook payload does not include required order/payment identifiers");
        }

        orderServiceClient.markPaymentCaptured(orderId, paymentId);
    }

    private Map<String, Object> parsePayload(String rawPayload) {
        try {
            return objectMapper.readValue(rawPayload, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid webhook payload", ex);
        }
    }

    private String extractOrderId(Map<String, Object> event) {
        Map<String, Object> payload = asMap(event.get("payload"));
        Map<String, Object> paymentLink = asEntity(payload, "payment_link");
        String referenceId = asString(paymentLink.get("reference_id"));
        if (referenceId != null && !referenceId.isBlank()) {
            return referenceId;
        }

        Map<String, Object> payment = asEntity(payload, "payment");
        Map<String, Object> notes = asMap(payment.get("notes"));
        String fromNotes = asString(notes.get("order_id"));
        if (fromNotes == null || fromNotes.isBlank()) {
            fromNotes = asString(notes.get("orderId"));
        }
        return fromNotes;
    }

    private String extractPaymentId(Map<String, Object> event) {
        Map<String, Object> payload = asMap(event.get("payload"));
        Map<String, Object> payment = asEntity(payload, "payment");
        return asString(payment.get("id"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    private Map<String, Object> asEntity(Map<String, Object> payload, String key) {
        Map<String, Object> wrapper = asMap(payload.get(key));
        return asMap(wrapper.get("entity"));
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }
}

