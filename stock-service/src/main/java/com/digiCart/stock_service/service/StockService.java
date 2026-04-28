package com.digiCart.stock_service.service;

import com.digiCart.stock_service.model.Stock;
import com.digiCart.stock_service.repository.StockRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Optional<Stock> getByProductId(String productId) {
        if (productId == null || productId.isBlank()) {
            return Optional.empty();
        }
        Stock probe = new Stock();
        probe.setProductId(productId);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnorePaths("id", "availableQuantity", "consumedQuantity");
        return stockRepository.findOne(Example.of(probe, matcher));
    }

    public List<Stock> getByProductIds(List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        return stockRepository.findAll().stream()
                .filter(s -> productIds.contains(s.getProductId()))
                .toList();
    }

    public UpsertResult upsert(Stock request) {
        String productId = request.getProductId();
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("productId must not be blank");
        }
        if (request.getAvailableQuantity() == null || request.getAvailableQuantity() < 0) {
            throw new IllegalArgumentException("availableQuantity must be a non-negative number");
        }
        if (request.getConsumedQuantity() == null || request.getConsumedQuantity() < 0) {
            throw new IllegalArgumentException("consumedQuantity must be a non-negative number");
        }

        Optional<Stock> existing = getByProductId(productId);
        if (existing.isPresent()) {
            Stock stock = existing.get();
            stock.setAvailableQuantity(request.getAvailableQuantity());
            stock.setConsumedQuantity(request.getConsumedQuantity());
            return new UpsertResult(stockRepository.save(stock), false);
        }

        return new UpsertResult(stockRepository.save(request), true);
    }

    public Optional<Stock> removeByProductId(String productId) {
        Optional<Stock> existing = getByProductId(productId);
        existing.ifPresent(stockRepository::delete);
        return existing;
    }

    public record UpsertResult(Stock stock, boolean created) {
    }
}

