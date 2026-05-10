package com.digiCart.user_service.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digiCart.user_service.dto.ActivationRequest;
import com.digiCart.user_service.dto.AuthVerifyRequest;
import com.digiCart.user_service.dto.AuthVerifyResponse;
import com.digiCart.user_service.dto.RegisterUserRequest;
import com.digiCart.user_service.dto.RegisterUserResponse;
import com.digiCart.user_service.dto.UserRegistrationNotificationEvent;
import com.digiCart.user_service.kafka.NotificationProducer;
import com.digiCart.user_service.model.Role;
import com.digiCart.user_service.model.User;
import com.digiCart.user_service.repository.UserRepository;

@Service
public class UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationProducer notificationProducer;

    public UserAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                          NotificationProducer notificationProducer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationProducer = notificationProducer;
    }

    private static final Logger log = LoggerFactory.getLogger(UserAuthService.class);

    public AuthVerifyResponse verify(AuthVerifyRequest request) {
        log.info("Entering verify with request: {}", request);
        if (request.getUsername() == null || request.getPassword() == null) {
            return new AuthVerifyResponse(false, null, null);
        }

        System.out.println("Verifying user: " + request.getUsername());

        String loginIdentifier = request.getUsername();
        Optional<User> userOptional = findByLoginIdentifier(loginIdentifier);

        return userOptional
                .map(user -> {
                    // Check if user is active
                    if (!user.getActive()) {
                        System.out.println("User is not active: " + user.getUId());
                        return new AuthVerifyResponse(false, null, null);
                    }

                    String storedPassword = user.getPassword();
                    boolean passwordOk = passwordEncoder.matches(request.getPassword(), storedPassword)
                            || request.getPassword().equals(storedPassword);

                    System.out.println("User found: " + user.getUId() + ", passwordOk: " + passwordOk + ", role: " + user.getRole());

                    // Migrate legacy plain-text passwords on successful login.
                    if (passwordOk && !storedPassword.startsWith("$2")) {
                        user.setPassword(passwordEncoder.encode(request.getPassword()));
                        userRepository.save(user);
                    }

                    if (!passwordOk) {
                        return new AuthVerifyResponse(false, null, null);
                    }

                    return new AuthVerifyResponse(true, user.getUId(), user.getRole().name());
                })
                .orElseGet(() -> {
                    System.out.println("User not found: " + loginIdentifier);
                    return new AuthVerifyResponse(false, null, null);
                });
    }

    private Optional<User> findByLoginIdentifier(String loginIdentifier) {
        if (loginIdentifier == null) {
            return Optional.empty();
        }

        Optional<User> user = userRepository.findById(loginIdentifier);
        if (user.isPresent()) {
            return user;
        }

        return userRepository.findByName(loginIdentifier);
    }

    public RegisterUserResponse register(RegisterUserRequest request) {
        log.info("Entering register with request: {}", request);
        // Validate input
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        // Check if email already exists as user ID
        if (userRepository.existsById(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create inactive user with verification code
        User user = new User();
        user.setUId(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.Customer);
        user.setActive(false);
        
        // Generate verification code
        String verificationCode = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        user.setVerificationCode(verificationCode);

        User savedUser = userRepository.save(user);
        System.out.println("User created with email: " + savedUser.getUId() + ", verification code: " + verificationCode);

        // Publish Kafka event for notification service
        UserRegistrationNotificationEvent event = new UserRegistrationNotificationEvent(
                savedUser.getUId(),
                savedUser.getName(),
                verificationCode
        );
        notificationProducer.sendUserRegistrationEvent(event);

        return new RegisterUserResponse(savedUser.getUId(), savedUser.getName(), savedUser.getRole().name());
    }

    /**
     * Activate user account by verifying the activation code
     */
    public void activateUser(ActivationRequest request) {
        log.info("Entering activateUser with request: {}", request);
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getVerificationCode() == null || request.getVerificationCode().isBlank()) {
            throw new IllegalArgumentException("Verification code is required");
        }

        User user = userRepository.findById(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + request.getEmail()));

        // Check if user is already active
        if (user.getActive()) {
            throw new IllegalArgumentException("User account is already active");
        }

        // Verify the code
        if (!request.getVerificationCode().equals(user.getVerificationCode())) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        // Activate user and clear verification code
        user.setActive(true);
        user.setVerificationCode(null);
        userRepository.save(user);

        System.out.println("User activated: " + user.getUId());
    }

    /**
     * Get user by ID
     */
    public Optional<User> getUserById(String userId) {
        log.info("Entering getUserById with userId: {}", userId);
        return userRepository.findById(userId);
    }

    public void addAddressToUser(String userId, String addressId) {
        log.info("Entering addAddressToUser with userId: {}, addressId: {}", userId, addressId);
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (addressId == null || addressId.isBlank()) {
            throw new IllegalArgumentException("Address ID is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (!user.getAddressIds().contains(addressId)) {
            user.getAddressIds().add(addressId);
            userRepository.save(user);
        }
    }

    public void setDefaultAddressForUser(String userId, String addressId) {
        log.info("Entering setDefaultAddressForUser with userId: {}, addressId: {}", userId, addressId);
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (addressId == null || addressId.isBlank()) {
            throw new IllegalArgumentException("Address ID is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (!user.getAddressIds().contains(addressId)) {
            user.getAddressIds().add(addressId);
        }
        user.setDefaultAddressId(addressId);
        userRepository.save(user);
    }
}

