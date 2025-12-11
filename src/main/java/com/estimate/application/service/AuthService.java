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
import reactor.core.publisher.Mono;

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
    
    public Mono<AuthResponse> register(RegisterRequest request) {
        return userRepository.existsByEmail(request.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Email already registered"));
                    }
                    
                    User user = User.builder()
                            .email(request.getEmail())
                            .passwordHash(passwordEncoder.encode(request.getPassword()))
                            .companyName(request.getCompanyName())
                            .phone(request.getPhone())
                            .role(User.Role.USER)
                            .build();
                    
                    return userRepository.save(user);
                })
                .doOnNext(user -> log.info("User registered: {}", user.getEmail()))
                .map(user -> buildAuthResponse(user, generateToken(user)));
    }
    
    public Mono<AuthResponse> login(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid email or password")))
                .flatMap(user -> validateAndAuthenticate(user, request.getPassword()))
                .doOnNext(user -> log.info("User logged in: {}", user.getEmail()))
                .map(user -> buildAuthResponse(user, generateToken(user)));
    }
    
    private Mono<User> validateAndAuthenticate(User user, String password) {
        if (user.isLocked()) {
            return Mono.error(new IllegalStateException("Account is locked. Please try again later."));
        }
        
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return handleFailedLogin(user)
                    .then(Mono.error(new IllegalArgumentException("Invalid email or password")));
        }
        
        return resetFailedAttempts(user);
    }
    
    private Mono<User> resetFailedAttempts(User user) {
        if (user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            return userRepository.save(user);
        }
        return Mono.just(user);
    }
    
    private Mono<User> handleFailedLogin(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        
        if (user.getFailedLoginAttempts() >= maxLoginAttempts) {
            user.setLockedUntil(Instant.now().plus(lockoutDurationMinutes, ChronoUnit.MINUTES));
            log.warn("Account locked for user: {}", user.getEmail());
        }
        
        return userRepository.save(user);
    }
    
    private String generateToken(User user) {
        return tokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
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
