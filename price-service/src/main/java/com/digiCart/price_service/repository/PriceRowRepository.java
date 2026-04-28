package com.digiCart.price_service.repository;

import com.digiCart.price_service.model.PriceRow;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRowRepository extends JpaRepository<PriceRow, Long> {

    Optional<PriceRow> findByProductCode(String productCode);

    List<PriceRow> findByProductCodeIn(Collection<String> productCodes);
}

