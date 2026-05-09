package com.digiCart.user_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.digiCart.user_service.model.Role;
import com.digiCart.user_service.model.User;
import com.digiCart.user_service.repository.UserRepository;

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
			admin.setUId(admin.getUId() != null ? admin.getUId() : "admin@digicart.com");
			admin.setName("admin");
			admin.setPassword(passwordEncoder.encode("admin123"));
			admin.setRole(Role.Admin);
			admin.setActive(true);
			userRepository.save(admin);
			System.out.println("Admin user ensured: admin@digicart.com/admin123 with role " + admin.getRole());

			User user = userRepository.findByName("user").orElse(new User());
			user.setUId(user.getUId() != null ? user.getUId() : "user@digicart.com");
			user.setName("user");
			user.setPassword(passwordEncoder.encode("user123"));
			user.setRole(Role.Customer);
			user.setActive(true);
			userRepository.save(user);
			System.out.println("User ensured: user@digicart.com/user123 with role " + user.getRole());
		};
	}

}
