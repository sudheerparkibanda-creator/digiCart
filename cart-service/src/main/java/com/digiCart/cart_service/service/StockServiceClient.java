package com.digiCart.cart_service.service;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class StockServiceClient {

    private static final Logger log = LoggerFactory.getLogger(StockServiceClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public StockServiceClient(@LoadBalanced RestTemplate restTemplate,
                              @Value("${stock-service.base-url:http://stock-service}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public StockData getByProductId(String productId) {
        log.info("Entering getByProductId with productId: {}", productId);
        if (productId == null || productId.isBlank()) {
            return null;
        }
        try {
            Map<?, ?> raw = restTemplate.getForObject(baseUrl + "/stocks/" + productId, Map.class);
            return toStockData(raw);
        } catch (RestClientException ex) {
            return null;
        }
    }

    private StockData toStockData(Map<?, ?> raw) {
        if (raw == null) {
            return null;
        }
        Object productId = raw.get("productId");
        if (productId == null) {
            return null;
        }

        int available = raw.get("availableQuantity") instanceof Number
                ? ((Number) raw.get("availableQuantity")).intValue()
                : 0;
        int consumed = raw.get("consumedQuantity") instanceof Number
                ? ((Number) raw.get("consumedQuantity")).intValue()
                : 0;

        StockData data = new StockData();
        data.setProductId(productId.toString());
        data.setAvailableQuantity(Math.max(available - consumed, 0));
        return data;
    }

    public static class StockData {
        private String productId;
        private Integer availableQuantity;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public Integer getAvailableQuantity() {
            return availableQuantity;
        }

        public void setAvailableQuantity(Integer availableQuantity) {
            this.availableQuantity = availableQuantity;
        }
    }
}

