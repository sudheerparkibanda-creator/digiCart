package com.digiCart.order_service.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderResponse {

    private String orderId;
    private String userId;
    private String addressId;
    private String status;
    private Double total;
    private String paymentLink;
    private LocalDateTime creationTime;
    private boolean orderPlacedEmailSent;
    private boolean paymentSuccessEmailSent;
    private List<OrderItemResponse> items = new ArrayList<>();

    public OrderResponse() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPaymentLink() {
        return paymentLink;
    }

    public void setPaymentLink(String paymentLink) {
        this.paymentLink = paymentLink;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public boolean isOrderPlacedEmailSent() {
        return orderPlacedEmailSent;
    }

    public void setOrderPlacedEmailSent(boolean orderPlacedEmailSent) {
        this.orderPlacedEmailSent = orderPlacedEmailSent;
    }

    public boolean isPaymentSuccessEmailSent() {
        return paymentSuccessEmailSent;
    }

    public void setPaymentSuccessEmailSent(boolean paymentSuccessEmailSent) {
        this.paymentSuccessEmailSent = paymentSuccessEmailSent;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }
}

