package com.digiCart.order_service.dto;

public class CapturePaymentRequest {

    private String paymentId;

    public CapturePaymentRequest() {
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}

