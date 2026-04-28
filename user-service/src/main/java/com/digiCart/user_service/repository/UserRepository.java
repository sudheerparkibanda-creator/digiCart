package com.digiCart.user_service.repository;

import com.digiCart.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
	Optional<User> findByName(String name);

	boolean existsByName(String name);
}

