package com.digiCart.cart_service.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProductDetailsResponse {

    private String code;
    private String description;
    private Map<String, String> features = new LinkedHashMap<>();

    public ProductDetailsResponse() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, String> features) {
        this.features = features;
    }
}

