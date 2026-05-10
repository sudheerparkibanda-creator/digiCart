package com.digiCart.payment_service.service;

import com.digiCart.payment_service.dto.EnsurePaymentLinkResponse;
import com.digiCart.payment_service.dto.PaymentLinkRequestEvent;
import com.digiCart.payment_service.dto.PaymentLinkResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PaymentLinkRequestListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentLinkRequestListener.class);

    private final ObjectMapper objectMapper;
    private final PaymentLinkService paymentLinkService;
    private final PaymentLinkResponseProducer responseProducer;

    public PaymentLinkRequestListener(ObjectMapper objectMapper,
                                      PaymentLinkService paymentLinkService,
                                      PaymentLinkResponseProducer responseProducer) {
        this.objectMapper = objectMapper;
        this.paymentLinkService = paymentLinkService;
        this.responseProducer = responseProducer;
    }

    @KafkaListener(topics = "${app.kafka.topics.payment-link-request}", groupId = "${spring.application.name}")
    public void onMessage(String payload) {
        log.info("Entering onMessage with payload: {}", payload);
        PaymentLinkResponseEvent responseEvent = new PaymentLinkResponseEvent();
        try {
            PaymentLinkRequestEvent requestEvent = objectMapper.readValue(payload, PaymentLinkRequestEvent.class);
            responseEvent.setRequestId(requestEvent.getRequestId());
            responseEvent.setOrderId(requestEvent.getOrderId());

            EnsurePaymentLinkResponse ensured = paymentLinkService.ensurePaymentLink(requestEvent.getOrderId());
            responseEvent.setPaymentLink(ensured.getPaymentLink());
            responseEvent.setStatus(ensured.getStatus());
            responseEvent.setCreated(ensured.getCreated());
        } catch (Exception ex) {
            responseEvent.setErrorCode("PAYMENT_LINK_ERROR");
            responseEvent.setErrorMessage(ex.getMessage());
        }

        responseProducer.publish(responseEvent);
    }
}

