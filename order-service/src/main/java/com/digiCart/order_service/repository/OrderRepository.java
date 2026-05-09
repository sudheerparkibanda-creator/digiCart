package com.digiCart.order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digiCart.order_service.model.Order;

public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("select max(o.orderId) from Order o")
    String findMaxOrderId();
}

