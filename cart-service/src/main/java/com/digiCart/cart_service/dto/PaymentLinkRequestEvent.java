package com.digiCart.cart_service.dto;

public class PaymentLinkRequestEvent {

    private String requestId;
    private String orderId;

    public PaymentLinkRequestEvent() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}

