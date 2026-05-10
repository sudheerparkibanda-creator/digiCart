package com.digiCart.stock_service.controller;

import com.digiCart.stock_service.model.Stock;
import com.digiCart.stock_service.service.StockService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    private static final Logger log = LoggerFactory.getLogger(StockController.class);

    @GetMapping
    public List<Stock> getAllStocks() {
        log.info("Entering getAllStocks");
        return stockService.getAllStocks();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Stock> getByProductId(@PathVariable String productId) {
        log.info("Entering getByProductId with productId: {}", productId);
        return stockService.getByProductId(productId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-product-ids")
    public List<Stock> getByProductIds(@RequestParam List<String> ids) {
        log.info("Entering getByProductIds with ids: {}", ids);
        return stockService.getByProductIds(ids);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addStock(@RequestBody Stock request) {
        log.info("Entering addStock with request: {}", request);
        return saveOrUpdate(request);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStock(@RequestBody Stock request) {
        log.info("Entering updateStock with request: {}", request);
        return saveOrUpdate(request);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeStock(@PathVariable String productId) {
        log.info("Entering removeStock with productId: {}", productId);
        return stockService.removeByProductId(productId)
                .map(s -> ResponseEntity.noContent().<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<Stock> saveOrUpdate(Stock request) {
        try {
            StockService.UpsertResult result = stockService.upsert(request);
            if (result.created()) {
                return ResponseEntity.created(URI.create("/stocks/" + result.stock().getProductId()))
                        .body(result.stock());
            }
            return ResponseEntity.ok(result.stock());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}

