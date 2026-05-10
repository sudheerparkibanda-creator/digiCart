package com.digiCart.payment_service.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class OrderServiceClient {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public OrderServiceClient(RestTemplateBuilder restTemplateBuilder,
                              @Value("${order-service.base-url:http://order-service:8087}") String baseUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.baseUrl = baseUrl;
    }

    public OrderData getOrder(String orderId) {
        log.info("Entering getOrder with orderId: {}", orderId);
        HttpHeaders headers = createInternalHeaders();
        try {
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    baseUrl + "/orders/" + orderId,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<>() {}
            );
            Map<?, ?> body = response.getBody();
            if (body == null || body.get("orderId") == null) {
                throw new IllegalStateException("Order not found for id: " + orderId);
            }
            return toOrderData(body);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Unable to fetch order", ex);
        }
    }

    public OrderData setPaymentLinkIfMissing(String orderId, String paymentLink) {
        log.info("Entering setPaymentLinkIfMissing with orderId: {}, paymentLink: {}", orderId, paymentLink);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("paymentLink", paymentLink);

        HttpHeaders headers = createInternalHeaders();
        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    baseUrl + "/orders/" + orderId + "/payment-link",
                    HttpMethod.PATCH,
                    request,
                    new ParameterizedTypeReference<>() {}
            );
            if (response.getBody() == null) {
                throw new IllegalStateException("Order update response is empty");
            }
            return toOrderData(response.getBody());
        } catch (RestClientException ex) {
            throw new IllegalStateException("Unable to update order payment link", ex);
        }
    }

    public OrderData markPaymentCaptured(String orderId, String paymentId) {
        log.info("Entering markPaymentCaptured with orderId: {}, paymentId: {}", orderId, paymentId);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("paymentId", paymentId);

        HttpHeaders headers = createInternalHeaders();
        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    baseUrl + "/orders/" + orderId + "/payment-captured",
                    HttpMethod.PATCH,
                    request,
                    new ParameterizedTypeReference<>() {}
            );
            if (response.getBody() == null) {
                throw new IllegalStateException("Order payment capture response is empty");
            }
            return toOrderData(response.getBody());
        } catch (RestClientException ex) {
            throw new IllegalStateException("Unable to update order payment status", ex);
        }
    }

    private OrderData toOrderData(Map<?, ?> response) {
        OrderData orderData = new OrderData();
        orderData.setOrderId(response.get("orderId") == null ? null : response.get("orderId").toString());
        orderData.setStatus(response.get("status") == null ? null : response.get("status").toString());
        orderData.setPaymentLink(response.get("paymentLink") == null ? null : response.get("paymentLink").toString());

        Object total = response.get("total");
        if (total instanceof Number number) {
            orderData.setTotal(number.doubleValue());
        }
        return orderData;
    }

    private HttpHeaders createInternalHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-Auth", "true");
        return headers;
    }

    public static class OrderData {
        private String orderId;
        private String status;
        private Double total;
        private String paymentLink;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Double getTotal() {
            return total;
        }

        public void setTotal(Double total) {
            this.total = total;
        }

        public String getPaymentLink() {
            return paymentLink;
        }

        public void setPaymentLink(String paymentLink) {
            this.paymentLink = paymentLink;
        }
    }
}

