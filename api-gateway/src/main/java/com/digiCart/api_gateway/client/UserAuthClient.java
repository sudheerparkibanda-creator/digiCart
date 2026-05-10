package com.digiCart.api_gateway.client;

import com.digiCart.api_gateway.dto.ActivationRequest;
import com.digiCart.api_gateway.dto.LoginRequest;
import com.digiCart.api_gateway.dto.RegisterRequest;
import com.digiCart.api_gateway.dto.UserAuthVerifyResponse;
import com.digiCart.api_gateway.dto.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class UserAuthClient {

    private static final Logger log = LoggerFactory.getLogger(UserAuthClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public UserAuthClient(@Qualifier("nonLoadBalancedRestTemplate") RestTemplate restTemplate,
                          @Value("${auth.user-service.base-url:http://user-service}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public UserAuthVerifyResponse verifyCredentials(LoginRequest request) {
        log.info("Entering verifyCredentials with request: {}", request);
        System.out.println("UserAuthClient: Verifying credentials for " + request.getUsername() + ", baseUrl: " + baseUrl);
        try {
            ResponseEntity<UserAuthVerifyResponse> response = restTemplate.postForEntity(
                    baseUrl + "/internal/auth/verify",
                    request,
                    UserAuthVerifyResponse.class);
            System.out.println("UserAuthClient: Response status: " + response.getStatusCode() + ", body: " + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            System.out.println("UserAuthClient: Exception: " + e.getMessage());
            UserAuthVerifyResponse unauthorized = new UserAuthVerifyResponse();
            unauthorized.setAuthenticated(false);
            return unauthorized;
        }
    }

    public UserRegisterResponse registerUser(RegisterRequest request) {
        log.info("Entering registerUser with request: {}", request);
        ResponseEntity<UserRegisterResponse> response = restTemplate.postForEntity(
                baseUrl + "/internal/auth/register",
                request,
                UserRegisterResponse.class);
        return response.getBody();
    }

    public void activateUser(ActivationRequest request) {
        log.info("Entering activateUser with request: {}", request);
        restTemplate.postForEntity(
                baseUrl + "/internal/auth/activate",
                request,
                Void.class);
    }
}

