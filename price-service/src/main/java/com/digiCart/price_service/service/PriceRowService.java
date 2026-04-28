package com.digiCart.price_service.service;

import com.digiCart.price_service.model.PriceRow;
import com.digiCart.price_service.repository.PriceRowRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PriceRowService {

    private final PriceRowRepository priceRowRepository;

    public PriceRowService(PriceRowRepository priceRowRepository) {
        this.priceRowRepository = priceRowRepository;
    }

    public List<PriceRow> getAllPriceRows() {
        return priceRowRepository.findAll();
    }

    public Optional<PriceRow> getByProductCode(String productCode) {
        if (productCode == null || productCode.isBlank()) {
            return Optional.empty();
        }
        return priceRowRepository.findByProductCode(productCode);
    }

    public List<PriceRow> getByProductCodes(List<String> productCodes) {
        if (productCodes == null || productCodes.isEmpty()) {
            return Collections.emptyList();
        }
        return priceRowRepository.findByProductCodeIn(productCodes);
    }

    public Optional<PriceRow> removeByProductCode(String productCode) {
        Optional<PriceRow> existing = getByProductCode(productCode);
        existing.ifPresent(priceRowRepository::delete);
        return existing;
    }

    public UpsertResult upsert(PriceRow request) {
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

