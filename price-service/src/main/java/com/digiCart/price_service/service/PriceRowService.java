package com.digiCart.price_service.service;

import com.digiCart.price_service.model.PriceRow;
import com.digiCart.price_service.repository.PriceRowRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PriceRowService {

    private static final Logger log = LoggerFactory.getLogger(PriceRowService.class);

    private final PriceRowRepository priceRowRepository;

    public PriceRowService(PriceRowRepository priceRowRepository) {
        this.priceRowRepository = priceRowRepository;
    }

    public List<PriceRow> getAllPriceRows() {
        log.info("Entering getAllPriceRows");
        return priceRowRepository.findAll();
    }

    public Optional<PriceRow> getByProductCode(String productCode) {
        log.info("Entering getByProductCode with productCode: {}", productCode);
        if (productCode == null || productCode.isBlank()) {
            return Optional.empty();
        }
        return priceRowRepository.findByProductCode(productCode);
    }

    public List<PriceRow> getByProductCodes(List<String> productCodes) {
        log.info("Entering getByProductCodes with productCodes: {}", productCodes);
        if (productCodes == null || productCodes.isEmpty()) {
            return Collections.emptyList();
        }
        return priceRowRepository.findByProductCodeIn(productCodes);
    }

    public Optional<PriceRow> removeByProductCode(String productCode) {
        log.info("Entering removeByProductCode with productCode: {}", productCode);
        Optional<PriceRow> existing = getByProductCode(productCode);
        existing.ifPresent(priceRowRepository::delete);
        return existing;
    }

    public UpsertResult upsert(PriceRow request) {
        log.info("Entering upsert with request: {}", request);
        String productCode = request.getProductCode();
        if (productCode == null || productCode.isBlank()) {
            throw new IllegalArgumentException("Product code must not be blank");
        }
        if (request.getPrice() == null) {
            throw new IllegalArgumentException("Price must not be null");
        }
        if (request.getUnit() == null || request.getUnit().isBlank()) {
            throw new IllegalArgumentException("Unit must not be blank");
        }

        Optional<PriceRow> existing = priceRowRepository.findByProductCode(productCode);
        if (existing.isPresent()) {
            PriceRow row = existing.get();
            row.setPrice(request.getPrice());
            row.setUnit(request.getUnit());
            PriceRow saved = priceRowRepository.save(row);
            return new UpsertResult(saved, false);
        }

        PriceRow saved = priceRowRepository.save(request);
        return new UpsertResult(saved, true);
    }

    public record UpsertResult(PriceRow priceRow, boolean created) {
    }
}

