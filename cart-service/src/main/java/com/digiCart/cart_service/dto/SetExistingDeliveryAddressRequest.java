package com.digiCart.cart_service.dto;

public class SetExistingDeliveryAddressRequest {

    private String addressId;
    private Boolean setAsDefaultInCustomer = Boolean.FALSE;

    public SetExistingDeliveryAddressRequest() {
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public Boolean getSetAsDefaultInCustomer() {
        return setAsDefaultInCustomer;
    }

    public void setSetAsDefaultInCustomer(Boolean setAsDefaultInCustomer) {
        this.setAsDefaultInCustomer = setAsDefaultInCustomer;
    }
}

