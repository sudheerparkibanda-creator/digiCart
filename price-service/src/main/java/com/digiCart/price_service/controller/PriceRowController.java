package com.digiCart.price_service.controller;

import com.digiCart.price_service.model.PriceRow;
import com.digiCart.price_service.service.PriceRowService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/prices")
public class PriceRowController {

    private static final Logger log = LoggerFactory.getLogger(PriceRowController.class);

    private final PriceRowService priceRowService;

    public PriceRowController(PriceRowService priceRowService) {
        this.priceRowService = priceRowService;
    }

    @GetMapping
    public List<PriceRow> getAllPriceRows() {
        log.info("Entering getAllPriceRows");
        return priceRowService.getAllPriceRows();
    }

    @GetMapping("/{productCode}")
    public ResponseEntity<PriceRow> getByProductCode(@PathVariable String productCode) {
        log.info("Entering getByProductCode with productCode: {}", productCode);
        return priceRowService.getByProductCode(productCode)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-product-codes")
    public List<PriceRow> getByProductCodes(@RequestParam List<String> codes) {
        log.info("Entering getByProductCodes with codes: {}", codes);
        return priceRowService.getByProductCodes(codes);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addPriceRow(@RequestBody PriceRow request) {
        log.info("Entering addPriceRow with request: {}", request);
        return saveOrUpdate(request);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePriceRow(@RequestBody PriceRow request) {
        log.info("Entering updatePriceRow with request: {}", request);
        return saveOrUpdate(request);
    }

    @DeleteMapping("/{productCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removePriceRow(@PathVariable String productCode) {
        log.info("Entering removePriceRow with productCode: {}", productCode);
        return priceRowService.removeByProductCode(productCode)
                .map(row -> ResponseEntity.noContent().<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<PriceRow> saveOrUpdate(PriceRow request) {
        log.info("Entering saveOrUpdate with request: {}", request);
        try {
            PriceRowService.UpsertResult result = priceRowService.upsert(request);
            if (result.created()) {
                return ResponseEntity.created(URI.create("/prices/" + result.priceRow().getProductCode()))
                        .body(result.priceRow());
            }
            return ResponseEntity.ok(result.priceRow());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}

