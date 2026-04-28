package com.digiCart.cart_service.service;

import com.digiCart.cart_service.dto.SetNewDeliveryAddressRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AddressServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public AddressServiceClient(@LoadBalanced RestTemplate restTemplate,
                                @Value("${address-service.base-url:http://address-service}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public String createAddress(SetNewDeliveryAddressRequest request) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("fullName", request.getFullName());
        payload.put("mobileNumber", request.getMobileNumber());
        payload.put("addressLine1", request.getAddressLine1());
        payload.put("addressLine2", request.getAddressLine2());
        payload.put("landmark", request.getLandmark());
        payload.put("city", request.getCity());
        payload.put("district", request.getDistrict());
        payload.put("state", request.getState());
        payload.put("pinCode", request.getPinCode());
        payload.put("country", request.getCountry());
        payload.put("addressType", request.getAddressType());
        payload.put("alternatePhone", request.getAlternatePhone());

        try {
            Map<?, ?> response = restTemplate.postForObject(baseUrl + "/addresses", payload, Map.class);
            Object addressId = response == null ? null : response.get("addressId");
            if (addressId == null || addressId.toString().isBlank()) {
                throw new IllegalStateException("Address created but addressId is missing in response");
            }
            return addressId.toString();
        } catch (RestClientException ex) {
            throw new IllegalStateException("Unable to create address", ex);
        }
    }

    public boolean addressExists(String addressId) {
        try {
            Map<?, ?> response = restTemplate.getForObject(baseUrl + "/addresses/" + addressId, Map.class);
            return response != null && response.get("addressId") != null;
        } catch (RestClientException ex) {
            return false;
        }
    }
}

