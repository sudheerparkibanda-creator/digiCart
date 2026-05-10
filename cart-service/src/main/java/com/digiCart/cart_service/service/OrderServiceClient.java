package com.digiCart.cart_service.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.digiCart.cart_service.model.Cart;
import com.digiCart.cart_service.model.CartItem;

@Component
public class OrderServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String userServiceBaseUrl;

    public OrderServiceClient(@LoadBalanced RestTemplate restTemplate,
                              @Value("${order-service.base-url:http://order-service}") String baseUrl,
                              @Value("${user-service.base-url:http://user-service}") String userServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.userServiceBaseUrl = userServiceBaseUrl;
    }

    public OrderData createOrderFromCart(Cart cart, String status) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("userId", cart.getUserId());
        payload.put("userEmail", cart.getUserId());
        payload.put("addressId", cart.getAddressId());
        payload.put("status", status);
        payload.put("total", cart.getTotal());

        List<Map<String, Object>> items = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            Map<String, Object> itemMap = new LinkedHashMap<>();
            itemMap.put("entryNumber", item.getEntryNumber());
            itemMap.put("productId", item.getProductId());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("unitPrice", item.getUnitPrice());
            itemMap.put("totalPrice", item.getTotalPrice());
            items.add(itemMap);
        }
        payload.put("items", items);

        try {
            Map<?, ?> response = restTemplate.postForObject(baseUrl + "/orders", payload, Map.class);
            if (response == null || response.get("orderId") == null) {
                throw new IllegalStateException("Order creation response missing orderId");
            }

            OrderData data = new OrderData();
            data.setOrderId(response.get("orderId").toString());
            Object responseStatus = response.get("status");
            data.setStatus(responseStatus == null ? status : responseStatus.toString());
            Object paymentLink = response.get("paymentLink");
            if (paymentLink != null) {
                data.setPaymentLink(paymentLink.toString());
            }
            return data;
        } catch (RestClientException ex) {
            throw new IllegalStateException("Unable to create order", ex);
        }
    }

    public static class OrderData {
        private String orderId;
        private String status;
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

        public String getPaymentLink() {
            return paymentLink;
        }

        public void setPaymentLink(String paymentLink) {
            this.paymentLink = paymentLink;
        }
    }
}

