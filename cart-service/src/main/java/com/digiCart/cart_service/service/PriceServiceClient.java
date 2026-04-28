package com.digiCart.cart_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class PriceServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PriceServiceClient(@LoadBalanced RestTemplate restTemplate,
                              @Value("${price-service.base-url:http://price-service}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public PriceData getByProductCode(String productCode) {
        if (productCode == null || productCode.isBlank()) {
            return null;
        }
        try {
            return restTemplate.getForObject(baseUrl + "/prices/" + productCode, PriceData.class);
        } catch (RestClientException ex) {
            return null;
        }
    }

    public static class PriceData {
        private String productCode;
        private Double price;
        private String unit;

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
}

