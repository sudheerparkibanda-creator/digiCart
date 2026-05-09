package com.digiCart.order_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digiCart.order_service.model.OrderSequence;

import jakarta.persistence.LockModeType;

public interface OrderSequenceRepository extends JpaRepository<OrderSequence, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from OrderSequence s where s.name = :name")
    Optional<OrderSequence> findByNameForUpdate(@Param("name") String name);
}
