package com.digiCart.cart_service.dto;

import com.digiCart.cart_service.model.CartStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CartResponse {

    private String cartId;
    private String userId;
    private CartStatus status;
    private String addressId;
    private Double total;
    private LocalDateTime creationTime;
    private List<CartItemResponse> items = new ArrayList<>();

    public CartResponse() {
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CartStatus getStatus() {
        return status;
    }

    public void setStatus(CartStatus status) {
        this.status = status;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }
}

