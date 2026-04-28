package com.digiCart.order_service.dto;

public class UpdatePaymentLinkRequest {

    private String paymentLink;

    public UpdatePaymentLinkRequest() {
    }

    public String getPaymentLink() {
        return paymentLink;
    }

    public void setPaymentLink(String paymentLink) {
        this.paymentLink = paymentLink;
    }
}

