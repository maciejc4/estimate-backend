package com.estimate.application.service;

import com.estimate.application.dto.*;
import com.estimate.domain.model.User;
import com.estimate.domain.repository.UserRepository;
import com.estimate.domain.usecase.LoginUserUseCase;
import com.estimate.domain.usecase.RegisterUserUseCase;
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
    
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final JwtTokenProvider tokenProvider;
    
    public Mono<AuthResponse> register(RegisterRequest request) {
        return registerUserUseCase.execute(
                request.getEmail(),
                request.getPassword(),
                request.getCompanyName(),
                request.getPhone()
        )
        .doOnNext(user -> log.info("User registered: {}", user.getEmail()))
        .map(user -> {
            String token = tokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
            return buildAuthResponse(user, token);
        });
    }
    
    public Mono<AuthResponse> login(LoginRequest request) {
        return loginUserUseCase.execute(request.getEmail(), request.getPassword())
                .doOnNext(user -> log.info("User logged in: {}", user.getEmail()))
                .map(user -> {
                    String token = tokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
                    return buildAuthResponse(user, token);
                });
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
