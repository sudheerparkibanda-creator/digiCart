package com.digiCart.product_service.service;

import com.digiCart.product_service.model.PriceRow;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class PriceServiceClient {

    private static final Logger log = LoggerFactory.getLogger(PriceServiceClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PriceServiceClient(@LoadBalanced RestTemplate restTemplate,
                              @Value("${price-service.base-url:http://price-service}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public PriceRow getByProductCode(String productCode) {
        log.info("Entering getByProductCode with productCode: {}", productCode);
        if (productCode == null || productCode.isBlank()) {
            return null;
        }
        String url = baseUrl + "/prices/" + productCode;
        try {
            return restTemplate.getForObject(url, PriceRow.class);
        } catch (RestClientException ex) {
            return null;
        }
    }

    public Map<String, PriceRow> getByProductCodes(List<String> productCodes) {
        log.info("Entering getByProductCodes with productCodes: {}", productCodes);
        if (productCodes == null || productCodes.isEmpty()) {
            return Collections.emptyMap();
        }

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/prices/by-product-codes")
                .queryParam("codes", productCodes.toArray())
                .toUriString();

        try {
            PriceRow[] rows = restTemplate.getForObject(url, PriceRow[].class);
            if (rows == null || rows.length == 0) {
                return Collections.emptyMap();
            }
            Map<String, PriceRow> result = new LinkedHashMap<>();
            for (PriceRow row : rows) {
                result.put(row.getProductCode(), row);
            }
            return result;
        } catch (RestClientException ex) {
            return Collections.emptyMap();
        }
    }
}

