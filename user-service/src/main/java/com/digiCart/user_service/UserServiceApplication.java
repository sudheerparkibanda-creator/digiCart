package com.digiCart.user_service;

import com.digiCart.user_service.model.Role;
import com.digiCart.user_service.model.User;
import com.digiCart.user_service.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			System.out.println("Initializing database...");
			User admin = userRepository.findByName("admin").orElse(new User());
			admin.setUId(admin.getUId() != null ? admin.getUId() : UUID.randomUUID().toString());
			admin.setName("admin");
			admin.setPassword(passwordEncoder.encode("admin123"));
			admin.setRole(Role.Admin);
			userRepository.save(admin);
			System.out.println("Admin user ensured: admin/admin123 with role " + admin.getRole());

			User user = userRepository.findByName("user").orElse(new User());
			user.setUId(user.getUId() != null ? user.getUId() : UUID.randomUUID().toString());
			user.setName("user");
			user.setPassword(passwordEncoder.encode("user123"));
			user.setRole(Role.Customer);
			userRepository.save(user);
			System.out.println("User ensured: user/user123 with role " + user.getRole());
		};
	}

}
