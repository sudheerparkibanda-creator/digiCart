package com.digiCart.cart_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomerServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public CustomerServiceClient(@LoadBalanced RestTemplate restTemplate,
                                 @Value("${user-service.base-url:http://user-service}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public void addAddressToCustomer(String customerId, String addressId) {
        try {
            restTemplate.postForEntity(baseUrl + "/internal/users/" + customerId + "/addresses/" + addressId, null, Void.class);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Unable to add address to customer", ex);
        }
    }

    public void setDefaultAddress(String customerId, String addressId) {
        try {
            restTemplate.put(baseUrl + "/internal/users/" + customerId + "/default-address/" + addressId, null);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Unable to set default address for customer", ex);
        }
    }
}

