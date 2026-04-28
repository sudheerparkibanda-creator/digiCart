package com.digiCart.cart_service.repository;

import com.digiCart.cart_service.model.Cart;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, String> {
	List<Cart> findByUserId(String userId);
}

