package com.digiCart.product_service.controller;

import com.digiCart.product_service.model.Product;
import com.digiCart.product_service.service.ProductService;
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
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @GetMapping
    public List<Product> getAllProducts() {
        log.info("Entering getAllProducts");
        return productService.getAllProducts();
    }

    @GetMapping("/{code}")
    public ResponseEntity<Product> getProductByID(@PathVariable String code) {
        log.info("Entering getProductByID with code: {}", code);
        return productService.getByCode(code)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addProduct(@RequestBody Product request) {
        log.info("Entering addProduct with request: {}", request);
        return saveOrUpdate(request);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@RequestBody Product request) {
        log.info("Entering updateProduct with request: {}", request);
        return saveOrUpdate(request);
    }

    @DeleteMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeProduct(@PathVariable String code) {
        log.info("Entering removeProduct with code: {}", code);
        return productService.removeByCode(code)
                .map(product -> ResponseEntity.noContent().<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<Product> saveOrUpdate(Product request) {
        try {
            ProductService.UpsertResult result = productService.upsert(request);
            if (result.created()) {
                return ResponseEntity.created(URI.create("/products/" + result.product().getCode()))
                        .body(result.product());
            }
            return ResponseEntity.ok(result.product());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
