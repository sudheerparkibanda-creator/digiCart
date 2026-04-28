package com.digiCart.user_service.service;

import com.digiCart.user_service.dto.AuthVerifyRequest;
import com.digiCart.user_service.dto.AuthVerifyResponse;
import com.digiCart.user_service.dto.RegisterUserRequest;
import com.digiCart.user_service.dto.RegisterUserResponse;
import com.digiCart.user_service.model.Role;
import com.digiCart.user_service.model.User;
import com.digiCart.user_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthVerifyResponse verify(AuthVerifyRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return new AuthVerifyResponse(false, null, null);
        }

        System.out.println("Verifying user: " + request.getUsername());

        return userRepository.findByName(request.getUsername())
                .map(user -> {
                    String storedPassword = user.getPassword();
                    boolean passwordOk = passwordEncoder.matches(request.getPassword(), storedPassword)
                            || request.getPassword().equals(storedPassword);

                    System.out.println("User found: " + user.getName() + ", passwordOk: " + passwordOk + ", role: " + user.getRole());

                    // Migrate legacy plain-text passwords on successful login.
                    if (passwordOk && !storedPassword.startsWith("$2")) {
                        user.setPassword(passwordEncoder.encode(request.getPassword()));
                        userRepository.save(user);
                    }

                    if (!passwordOk) {
                        return new AuthVerifyResponse(false, null, null);
                    }

                    return new AuthVerifyResponse(true, user.getName(), user.getRole().name());
                })
                .orElseGet(() -> {
                    System.out.println("User not found: " + request.getUsername());
                    return new AuthVerifyResponse(false, null, null);
                });
    }

    public RegisterUserResponse register(RegisterUserRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (userRepository.existsByName(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUId(UUID.randomUUID().toString());
        user.setName(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.Customer);

        User saved = userRepository.save(user);
        return new RegisterUserResponse(saved.getUId(), saved.getName(), saved.getRole().name());
    }

    /**
     * Get user by ID
     */
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }
}

