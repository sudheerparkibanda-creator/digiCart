package com.digiCart.price_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_rows")
public class PriceRow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String productCode;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String unit;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @PrePersist
    private void prePersist() {
        if (this.creationTime == null) {
            this.creationTime = LocalDateTime.now();
        }
    }

    public PriceRow() {
    }

    public PriceRow(String productCode, Double price, String unit) {
        this.productCode = productCode;
        this.price = price;
        this.unit = unit;
    }

    public Long getId() {
        return id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }
}

