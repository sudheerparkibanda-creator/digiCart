package com.digiCart.order_service.service;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class PaymentServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PaymentServiceClient(RestTemplateBuilder restTemplateBuilder,
                                @Value("${payment-service.base-url:http://payment-service}") String baseUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.baseUrl = baseUrl;
    }

    public String ensurePaymentLink(String orderId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-Auth", "true");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(new LinkedHashMap<>(), headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    baseUrl + "/payments/orders/" + orderId + "/payment-link",
                    org.springframework.http.HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }
            );
            Map<?, ?> body = response.getBody();
            if (body == null || body.get("paymentLink") == null) {
                throw new IllegalStateException("Payment service response missing paymentLink");
            }
            return body.get("paymentLink").toString();
        } catch (RestClientException ex) {
            throw new IllegalStateException("Unable to generate payment link", ex);
        }
    }
}
