package com.digiCart.address_service.repository;

import com.digiCart.address_service.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, String> {
}

