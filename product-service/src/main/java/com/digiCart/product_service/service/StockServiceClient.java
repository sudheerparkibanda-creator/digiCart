package com.digiCart.product_service.service;

import com.digiCart.product_service.model.StockInfo;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class StockServiceClient {

    private static final Logger log = LoggerFactory.getLogger(StockServiceClient.class);

    private static final String PRODUCT_ID_FIELD = "productId";

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public StockServiceClient(@LoadBalanced RestTemplate restTemplate,
                              @Value("${stock-service.base-url:http://stock-service}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public StockInfo getByProductId(String productId) {
        log.info("Entering getByProductId with productId: {}", productId);
        if (productId == null || productId.isBlank()) {
            return null;
        }
        try {
            Map<?, ?> raw = restTemplate.getForObject(baseUrl + "/stocks/" + productId, Map.class);
            return toStockInfo(raw);
        } catch (RestClientException ex) {
            return null;
        }
    }

    public Map<String, StockInfo> getByProductIds(List<String> productIds) {
        log.info("Entering getByProductIds with productIds: {}", productIds);
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/stocks/by-product-ids")
                .queryParam("ids", productIds.toArray())
                .toUriString();
        try {
            List<Map<String, Object>> rows = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();

            if (rows == null || rows.isEmpty()) {
                return Collections.emptyMap();
            }
            Map<String, StockInfo> result = new LinkedHashMap<>();
            for (Map<String, Object> row : rows) {
                StockInfo info = toStockInfo(row);
                if (info != null && info.getProductId() != null) {
                    result.put(info.getProductId(), info);
                }
            }
            return result;
        } catch (RestClientException ex) {
            return Collections.emptyMap();
        }
    }

    private StockInfo toStockInfo(Map<?, ?> raw) {
        if (raw == null) return null;
        Object pid = raw.get(PRODUCT_ID_FIELD);
        Object avail = raw.get("availableQuantity");
        Object consumed = raw.get("consumedQuantity");
        if (pid == null) return null;

        int available = avail instanceof Number ? ((Number) avail).intValue() : 0;
        int consumedVal = consumed instanceof Number ? ((Number) consumed).intValue() : 0;

        StockInfo info = new StockInfo();
        info.setProductId(pid.toString());
        info.setAvailableQuantity(available - consumedVal);
        return info;
    }
}

