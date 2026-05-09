package com.digiCart.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digiCart.user_service.model.User;

public interface UserRepository extends JpaRepository<User, String> {
	Optional<User> findByName(String name);

	boolean existsByName(String name);
}

