package com.digiCart.cart_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
        System.out.println("OrderServiceClient.createOrderFromCart called with cart: " + cart.getCartId() + ", status: " + status);
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(cart.getUserId());
        request.setUserEmail(cart.getUserId());
        request.setAddressId(cart.getAddressId());
        request.setStatus(status);
        request.setTotal(cart.getTotal());

        List<CreateOrderItem> items = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            CreateOrderItem orderItem = new CreateOrderItem();
            orderItem.setEntryNumber(item.getEntryNumber());
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(item.getUnitPrice());
            orderItem.setTotalPrice(item.getTotalPrice());
            items.add(orderItem);
        }
        request.setItems(items);

        System.out.println("OrderServiceClient about to call restTemplate.postForObject with baseUrl: " + baseUrl + "/orders");
        try {
            HttpEntity<CreateOrderRequest> entity = new HttpEntity<>(request, createInternalHeaders());
            OrderResponse response = restTemplate.postForObject(baseUrl + "/orders", entity, OrderResponse.class);
            System.out.println("OrderServiceClient received response: " + response);
            if (response == null || response.getOrderId() == null) {
                throw new IllegalStateException("Order creation response missing orderId");
            }

            OrderData data = new OrderData();
            data.setOrderId(response.getOrderId());
            data.setStatus(response.getStatus() == null ? status : response.getStatus());
            data.setPaymentLink(response.getPaymentLink());
            return data;
        } catch (RestClientException ex) {
            throw new IllegalStateException("Unable to create order", ex);
        }
    }

    private HttpHeaders createInternalHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-Internal-Auth", "true");
        return headers;
    }

    public static class CreateOrderRequest {
        private String userId;
        private String userEmail;
        private String addressId;
        private String status;
        private Double total;
        private List<CreateOrderItem> items;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public String getAddressId() {
            return addressId;
        }

        public void setAddressId(String addressId) {
            this.addressId = addressId;
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

        public List<CreateOrderItem> getItems() {
            return items;
        }

        public void setItems(List<CreateOrderItem> items) {
            this.items = items;
        }
    }

    public static class CreateOrderItem {
        private Integer entryNumber;
        private String productId;
        private Integer quantity;
        private Double unitPrice;
        private Double totalPrice;

        public Integer getEntryNumber() {
            return entryNumber;
        }

        public void setEntryNumber(Integer entryNumber) {
            this.entryNumber = entryNumber;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(Double unitPrice) {
            this.unitPrice = unitPrice;
        }

        public Double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(Double totalPrice) {
            this.totalPrice = totalPrice;
        }
    }

    public static class OrderResponse {
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

