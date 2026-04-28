package com.digiCart.stock_service.repository;

import com.digiCart.stock_service.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
}

