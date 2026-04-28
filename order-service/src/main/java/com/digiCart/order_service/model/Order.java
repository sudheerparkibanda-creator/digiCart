package com.digiCart.order_service.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "order_id", nullable = false, updatable = false)
    private String orderId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "address_id", nullable = false)
    private String addressId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private OrderStatus status = OrderStatus.Placed;

    @Column(name = "total", nullable = false)
    private Double total;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "payment_link")
    private String paymentLink;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "payment_captured_time")
    private LocalDateTime paymentCapturedTime;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "order_placed_email_sent", nullable = false)
    private boolean orderPlacedEmailSent = false;

    @Column(name = "payment_success_email_sent", nullable = false)
    private boolean paymentSuccessEmailSent = false;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        if (this.orderId == null || this.orderId.isBlank()) {
            this.orderId = UUID.randomUUID().toString();
        }
        if (this.creationTime == null) {
            this.creationTime = LocalDateTime.now();
        }
        if (this.createdAt == null) {
            this.createdAt = this.creationTime;
        }
        assignEntryNumbers();
    }

    @PreUpdate
    private void preUpdate() {
        if (this.createdAt == null) {
            this.createdAt = this.creationTime;
        }
        assignEntryNumbers();
    }

    private void assignEntryNumbers() {
        int next = 0;
        for (OrderItem item : items) {
            if (item.getEntryNumber() == null) {
                item.setEntryNumber(next);
            }
            next = Math.max(next, item.getEntryNumber() + 1);
        }
    }

    public Order() {
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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
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

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public LocalDateTime getPaymentCapturedTime() {
        return paymentCapturedTime;
    }

    public void setPaymentCapturedTime(LocalDateTime paymentCapturedTime) {
        this.paymentCapturedTime = paymentCapturedTime;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
