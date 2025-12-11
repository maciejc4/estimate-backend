package com.estimate.application.service;

import com.estimate.application.dto.*;
import com.estimate.domain.model.User;
import com.estimate.domain.repository.UserRepository;
import com.estimate.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    
    @Value("${app.security.max-login-attempts}")
    private int maxLoginAttempts;
    
    @Value("${app.security.lockout-duration-minutes}")
    private int lockoutDurationMinutes;
    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .companyName(request.getCompanyName())
                .phone(request.getPhone())
                .role(User.Role.USER)
                .build();
        
        user = userRepository.save(user);
        log.info("User registered: {}", user.getEmail());
        
        String token = tokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        
        return buildAuthResponse(user, token);
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        
        if (user.isLocked()) {
            throw new IllegalStateException("Account is locked. Please try again later.");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedLogin(user);
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        // Reset failed attempts on successful login
        if (user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
        }
        
        log.info("User logged in: {}", user.getEmail());
        
        String token = tokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        
        return buildAuthResponse(user, token);
    }
    
    public AuthResponse loginDemo() {
        String demoEmail = "demo@estimate.app";
        
        User user = userRepository.findByEmail(demoEmail)
                .orElseGet(() -> {
                    User demoUser = User.builder()
                            .email(demoEmail)
                            .passwordHash(passwordEncoder.encode("demo1234"))
                            .companyName("Demo Company")
                            .phone("+48 123 456 789")
                            .role(User.Role.USER)
                            .build();
                    return userRepository.save(demoUser);
                });
        
        log.info("Demo user logged in");
        
        String token = tokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        
        return buildAuthResponse(user, token);
    }
    
    private void handleFailedLogin(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        
        if (user.getFailedLoginAttempts() >= maxLoginAttempts) {
            user.setLockedUntil(Instant.now().plus(lockoutDurationMinutes, ChronoUnit.MINUTES));
            log.warn("Account locked for user: {}", user.getEmail());
        }
        
        userRepository.save(user);
    }
    
    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .companyName(user.getCompanyName())
                .phone(user.getPhone())
                .build();
    }
}
