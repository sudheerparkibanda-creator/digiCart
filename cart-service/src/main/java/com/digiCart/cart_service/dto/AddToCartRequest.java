package com.digiCart.cart_service.dto;

public class AddToCartRequest {

    private String productId;
    private Integer quantity;

    public AddToCartRequest() {
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
}

