package com.digiCart.cart_service.service;

import com.digiCart.cart_service.dto.ProductDetailsResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ProductServiceClient {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ProductServiceClient(@LoadBalanced RestTemplate restTemplate,
                                @Value("${product-service.base-url:http://product-service}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public ProductDetailsResponse getByCode(String code) {
        log.info("Entering getByCode with code: {}", code);
        if (code == null || code.isBlank()) {
            return null;
        }
        try {
            Map<?, ?> raw = restTemplate.getForObject(baseUrl + "/products/" + code, Map.class);
            return toProduct(raw);
        } catch (RestClientException ex) {
            return null;
        }
    }

    private ProductDetailsResponse toProduct(Map<?, ?> raw) {
        if (raw == null) {
            return null;
        }

        ProductDetailsResponse response = new ProductDetailsResponse();
        Object code = raw.get("code");
        Object description = raw.get("description");
        Object features = raw.get("features");

        response.setCode(code == null ? null : code.toString());
        response.setDescription(description == null ? null : description.toString());

        if (features instanceof Map<?, ?> featureMap) {
            Map<String, String> normalized = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : featureMap.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    normalized.put(entry.getKey().toString(), entry.getValue().toString());
                }
            }
            response.setFeatures(normalized);
        }
        return response;
    }
}


