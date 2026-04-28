package com.digiCart.cart_service.dto;

public class CartItemResponse {

    private Integer entryNumber;
    private String productId;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
    private Integer availableStock;
    private ProductDetailsResponse product;

    public CartItemResponse() {
    }

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

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public ProductDetailsResponse getProduct() {
        return product;
    }

    public void setProduct(ProductDetailsResponse product) {
        this.product = product;
    }
}

