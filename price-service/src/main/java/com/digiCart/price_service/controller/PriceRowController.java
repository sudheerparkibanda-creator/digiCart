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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prices")
public class PriceRowController {

    private final PriceRowService priceRowService;

    public PriceRowController(PriceRowService priceRowService) {
        this.priceRowService = priceRowService;
    }

    @GetMapping
    public List<PriceRow> getAllPriceRows() {
        return priceRowService.getAllPriceRows();
    }

    @GetMapping("/{productCode}")
    public ResponseEntity<PriceRow> getByProductCode(@PathVariable String productCode) {
        return priceRowService.getByProductCode(productCode)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-product-codes")
    public List<PriceRow> getByProductCodes(@RequestParam List<String> codes) {
        return priceRowService.getByProductCodes(codes);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addPriceRow(@RequestBody PriceRow request) {
        return saveOrUpdate(request);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePriceRow(@RequestBody PriceRow request) {
        return saveOrUpdate(request);
    }

    @DeleteMapping("/{productCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removePriceRow(@PathVariable String productCode) {
        return priceRowService.removeByProductCode(productCode)
                .map(row -> ResponseEntity.noContent().<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<PriceRow> saveOrUpdate(PriceRow request) {
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

