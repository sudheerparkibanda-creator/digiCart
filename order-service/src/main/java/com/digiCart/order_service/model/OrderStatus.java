package com.digiCart.order_service.model;

public enum OrderStatus {
    PaymentPending,
    PaymentCaptured,
    Placed,
    Confirmed,
    Shipped,
    Delivered,
    Cancelled
}

