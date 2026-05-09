package com.digiCart.order_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_sequences")
public class OrderSequence {

    @Id
    @Column(name = "name", nullable = false, updatable = false)
    private String name;

    @Column(name = "next_val", nullable = false)
    private Long nextVal;

    public OrderSequence() {
    }

    public OrderSequence(String name, Long nextVal) {
        this.name = name;
        this.nextVal = nextVal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNextVal() {
        return nextVal;
    }

    public void setNextVal(Long nextVal) {
        this.nextVal = nextVal;
    }
}
