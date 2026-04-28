package com.digiCart.payment_service.service;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public OrderServiceClient(RestTemplateBuilder restTemplateBuilder,
                              @Value("${order-service.base-url:http://localhost:8087}") String baseUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.baseUrl = baseUrl;
    }

    public OrderData getOrder(String orderId) {
        try {
            Map<?, ?> response = restTemplate.getForObject(baseUrl + "/orders/" + orderId, Map.class);
            if (response == null || response.get("orderId") == null) {
                throw new IllegalStateException("Order not found for id: " + orderId);
            }
            return toOrderData(response);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Unable to fetch order", ex);
        }
    }

    public OrderData setPaymentLinkIfMissing(String orderId, String paymentLink) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("paymentLink", paymentLink);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/orders/" + orderId + "/payment-link",
                    HttpMethod.PATCH,
                    request,
                    Map.class
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
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("paymentId", paymentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/orders/" + orderId + "/payment-captured",
                    HttpMethod.PATCH,
                    request,
                    Map.class
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

