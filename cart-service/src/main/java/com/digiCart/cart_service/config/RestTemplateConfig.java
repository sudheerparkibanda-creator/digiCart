package com.digiCart.cart_service.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    private final ClientHttpRequestInterceptor clientHttpRequestInterceptor;

    public RestTemplateConfig(@Qualifier("authenticationPropagationInterceptor") ClientHttpRequestInterceptor clientHttpRequestInterceptor) {
        this.clientHttpRequestInterceptor = clientHttpRequestInterceptor;
    }

    @Bean
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(clientHttpRequestInterceptor);
        return restTemplate;
    }
}