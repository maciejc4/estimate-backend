package com.estimate.domain.usecase.impl;

import com.estimate.domain.model.User;
import com.estimate.domain.repository.UserRepository;
import com.estimate.domain.usecase.LoginUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class LoginUserUseCaseImpl implements LoginUserUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${app.security.max-login-attempts}")
    private int maxLoginAttempts;
    
    @Value("${app.security.lockout-duration-minutes}")
    private int lockoutDurationMinutes;
    
    @Override
    public Mono<User> execute(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid email or password")))
                .flatMap(user -> {
                    if (user.isLocked()) {
                        return Mono.error(new IllegalStateException("Account is locked. Please try again later."));
                    }
                    
                    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                        return handleFailedLogin(user)
                                .then(Mono.error(new IllegalArgumentException("Invalid email or password")));
                    }
                    
                    if (user.getFailedLoginAttempts() > 0) {
                        user.setFailedLoginAttempts(0);
                        user.setLockedUntil(null);
                        return userRepository.save(user);
                    }
                    
                    return Mono.just(user);
                });
    }
    
    private Mono<User> handleFailedLogin(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        
        if (user.getFailedLoginAttempts() >= maxLoginAttempts) {
            user.setLockedUntil(Instant.now().plus(lockoutDurationMinutes, ChronoUnit.MINUTES));
        }
        
        return userRepository.save(user);
    }
}
