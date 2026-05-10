package com.digiCart.product_service.service;

import com.digiCart.product_service.model.PriceRow;
import com.digiCart.product_service.model.Product;
import com.digiCart.product_service.model.StockInfo;
import com.digiCart.product_service.repository.ProductRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final PriceServiceClient priceServiceClient;
    private final StockServiceClient stockServiceClient;

    public ProductService(ProductRepository productRepository,
                          PriceServiceClient priceServiceClient,
                          StockServiceClient stockServiceClient) {
        this.productRepository = productRepository;
        this.priceServiceClient = priceServiceClient;
        this.stockServiceClient = stockServiceClient;
    }

    public List<Product> getAllProducts() {
        log.info("Entering getAllProducts");
        List<Product> products = productRepository.findAll();
        List<String> codes = products.stream()
                .map(Product::getCode)
                .filter(code -> code != null && !code.isBlank())
                .collect(Collectors.toList());

        Map<String, PriceRow> priceRowsByCode = priceServiceClient.getByProductCodes(codes);
        Map<String, StockInfo> stockByCode = stockServiceClient.getByProductIds(codes);

        for (Product product : products) {
            product.setPriceRow(priceRowsByCode.get(product.getCode()));
            product.setStockInfo(stockByCode.get(product.getCode()));
        }
        return products;
    }

    public Optional<Product> getByCode(String code) {
        log.info("Entering getByCode with code: {}", code);
        Optional<Product> product = findPersistedByCode(code);
        product.ifPresent(found -> {
            found.setPriceRow(priceServiceClient.getByProductCode(found.getCode()));
            found.setStockInfo(stockServiceClient.getByProductId(found.getCode()));
        });
        return product;
    }

    public Optional<Product> removeByCode(String code) {
        Optional<Product> existing = findPersistedByCode(code);
        existing.ifPresent(productRepository::delete);
        return existing;
    }

    public UpsertResult upsert(Product request) {
        String code = request.getCode();
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Product code must not be blank");
        }

        Map<String, String> sanitizedFeatures = request.getFeatures() == null
                ? new HashMap<>()
                : request.getFeatures();

        Optional<Product> existing = findPersistedByCode(code);
        if (existing.isPresent()) {
            Product product = existing.get();
            product.setDescription(request.getDescription());
            product.setFeatures(sanitizedFeatures);
            Product saved = productRepository.save(product);
            return new UpsertResult(saved, false);
        }

        request.setFeatures(sanitizedFeatures);
        Product saved = productRepository.save(request);
        return new UpsertResult(saved, true);
    }

    private Optional<Product> findPersistedByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }

        Product probe = new Product();
        probe.setCode(code);
        probe.setFeatures(null);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnorePaths("id", "description", "features");

        return productRepository.findOne(Example.of(probe, matcher));
    }

    public record UpsertResult(Product product, boolean created) {
    }
}
