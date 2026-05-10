package com.digiCart.order_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.digiCart.order_service.dto.CreateOrderItemRequest;
import com.digiCart.order_service.dto.CreateOrderRequest;
import com.digiCart.order_service.dto.OrderItemResponse;
import com.digiCart.order_service.dto.OrderPlacedNotificationEvent;
import com.digiCart.order_service.dto.OrderResponse;
import com.digiCart.order_service.dto.PaymentSuccessNotificationEvent;
import com.digiCart.order_service.kafka.NotificationEventProducer;
import com.digiCart.order_service.model.Order;
import com.digiCart.order_service.model.OrderItem;
import com.digiCart.order_service.model.OrderStatus;
import com.digiCart.order_service.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final NotificationEventProducer notificationEventProducer;
    private final OrderIdGeneratorService orderIdGeneratorService;
    private final PaymentServiceClient paymentServiceClient;

    public OrderService(OrderRepository orderRepository,
                        NotificationEventProducer notificationEventProducer,
                        OrderIdGeneratorService orderIdGeneratorService,
                        PaymentServiceClient paymentServiceClient) {
        this.orderRepository = orderRepository;
        this.notificationEventProducer = notificationEventProducer;
        this.orderIdGeneratorService = orderIdGeneratorService;
        this.paymentServiceClient = paymentServiceClient;
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        validateCreateOrderRequest(request);

        Order order = new Order();
        order.setOrderId(orderIdGeneratorService.nextOrderId());
        order.setUserId(request.getUserId());
        order.setAddressId(request.getAddressId());
        order.setUserEmail(request.getUserEmail());
        order.setStatus(parseStatus(request.getStatus()));
        order.setTotal(request.getTotal());

        List<OrderItem> items = new ArrayList<>();
        for (CreateOrderItemRequest itemRequest : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setEntryNumber(itemRequest.getEntryNumber());
            item.setProductId(itemRequest.getProductId());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setTotalPrice(itemRequest.getTotalPrice());
            items.add(item);
        }
        order.setItems(items);

        Order saved = orderRepository.save(order);
        String paymentLink = paymentServiceClient.ensurePaymentLink(saved.getOrderId());
        if (paymentLink != null && !paymentLink.isBlank()) {
            saved.setPaymentLink(paymentLink);
            saved = orderRepository.save(saved);
        }

        if (saved.getUserEmail() != null && !saved.getUserEmail().isBlank()) {
            OrderPlacedNotificationEvent event = new OrderPlacedNotificationEvent();
            event.setOrderId(saved.getOrderId());
            event.setUserId(saved.getUserId());
            event.setUserEmail(saved.getUserEmail());
            event.setPaymentLink(saved.getPaymentLink());
            notificationEventProducer.publishOrderPlaced(event);
        }

        return toResponse(saved);
    }

    public OrderResponse getOrder(String orderId) {
        return toResponse(getById(orderId));
    }

    public OrderResponse setPaymentLinkIfMissing(String orderId, String paymentLink) {
        if (paymentLink == null || paymentLink.isBlank()) {
            throw new IllegalArgumentException("paymentLink is required");
        }

        Order order = getById(orderId);
        if (order.getPaymentLink() == null || order.getPaymentLink().isBlank()) {
            order.setPaymentLink(paymentLink);
            order = orderRepository.save(order);
        }
        return toResponse(order);
    }

    public OrderResponse markPaymentCaptured(String orderId, String paymentId) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("paymentId is required");
        }

        Order order = getById(orderId);
        if (order.getStatus() == OrderStatus.PaymentCaptured) {
            return toResponse(order);
        }

        if (order.getStatus() != OrderStatus.PaymentPending && order.getStatus() != OrderStatus.Placed) {
            throw new IllegalStateException("Order is not eligible for payment capture update");
        }

        order.setStatus(OrderStatus.PaymentCaptured);
        if (order.getPaymentId() == null || order.getPaymentId().isBlank()) {
            order.setPaymentId(paymentId);
        }
        if (order.getPaymentCapturedTime() == null) {
            order.setPaymentCapturedTime(LocalDateTime.now());
        }
        Order saved = orderRepository.save(order);

        if (saved.getUserEmail() != null && !saved.getUserEmail().isBlank()) {
            PaymentSuccessNotificationEvent event = new PaymentSuccessNotificationEvent();
            event.setOrderId(saved.getOrderId());
            event.setUserId(saved.getUserId());
            event.setUserEmail(saved.getUserEmail());
            event.setPaymentId(saved.getPaymentId());
            notificationEventProducer.publishPaymentSuccess(event);
        }

        return toResponse(saved);
    }

    public OrderResponse markOrderPlacedEmailSent(String orderId) {
        Order order = getById(orderId);
        order.setOrderPlacedEmailSent(true);
        return toResponse(orderRepository.save(order));
    }

    public OrderResponse markPaymentSuccessEmailSent(String orderId) {
        Order order = getById(orderId);
        order.setPaymentSuccessEmailSent(true);
        return toResponse(orderRepository.save(order));
    }

    private Order getById(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId is required");
        }
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found for id: " + orderId));
    }

    private void validateCreateOrderRequest(CreateOrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.getUserId() == null || request.getUserId().isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }
        if (request.getAddressId() == null || request.getAddressId().isBlank()) {
            throw new IllegalArgumentException("addressId is required");
        }
        if (request.getTotal() == null || request.getTotal() <= 0) {
            throw new IllegalArgumentException("total must be greater than zero");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("items are required");
        }
    }

    private OrderStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return OrderStatus.Placed;
        }
        try {
            return OrderStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported status: " + status);
        }
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setUserId(order.getUserId());
        response.setAddressId(order.getAddressId());
        response.setStatus(order.getStatus() == null ? null : order.getStatus().name());
        response.setTotal(order.getTotal());
        response.setPaymentLink(order.getPaymentLink());
        response.setCreationTime(order.getCreationTime());
        response.setOrderPlacedEmailSent(order.isOrderPlacedEmailSent());
        response.setPaymentSuccessEmailSent(order.isPaymentSuccessEmailSent());

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setEntryNumber(item.getEntryNumber());
            itemResponse.setProductId(item.getProductId());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setUnitPrice(item.getUnitPrice());
            itemResponse.setTotalPrice(item.getTotalPrice());
            itemResponses.add(itemResponse);
        }
        response.setItems(itemResponses);
        return response;
    }
}

