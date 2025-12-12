package com.estimate.application.usecase.auth;

import com.estimate.domain.exception.AccountLockedException;
import com.estimate.domain.exception.InvalidCredentialsException;
import com.estimate.domain.model.User;
import com.estimate.domain.port.in.auth.AuthResult;
import com.estimate.domain.port.in.auth.LoginUserCommand;
import com.estimate.domain.port.in.auth.LoginUserUseCase;
import com.estimate.domain.port.out.JwtProviderPort;
import com.estimate.domain.port.out.PasswordEncoderPort;
import com.estimate.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUserService implements LoginUserUseCase {
    
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtProviderPort jwtProvider;
    
    @Value("${app.security.max-login-attempts}")
    private int maxLoginAttempts;
    
    @Value("${app.security.lockout-duration-minutes}")
    private int lockoutDurationMinutes;
    
    @Override
    public Mono<AuthResult> login(LoginUserCommand command) {
        return userRepository.findByEmail(command.getEmail())
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()))
                .flatMap(user -> validateAndAuthenticate(user, command.getPassword()))
                .doOnNext(user -> log.info("User logged in: {}", user.getEmail()))
                .map(this::buildAuthResult);
    }
    
    private Mono<User> validateAndAuthenticate(User user, String password) {
        if (user.isLocked()) {
            return Mono.error(new AccountLockedException(user.getLockedUntil()));
        }
        
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return handleFailedLogin(user)
                    .then(Mono.error(new InvalidCredentialsException()));
        }
        
        return resetFailedAttempts(user);
    }
    
    private Mono<User> resetFailedAttempts(User user) {
        if (user.getFailedLoginAttempts() > 0) {
            user.resetFailedLoginAttempts();
            return userRepository.save(user);
        }
        return Mono.just(user);
    }
    
    private Mono<User> handleFailedLogin(User user) {
        user.incrementFailedLoginAttempts();
        
        if (user.getFailedLoginAttempts() >= maxLoginAttempts) {
            Instant lockUntil = Instant.now().plus(lockoutDurationMinutes, ChronoUnit.MINUTES);
            user.lockAccount(lockUntil);
            log.warn("Account locked for user: {}", user.getEmail());
        }
        
        return userRepository.save(user);
    }
    
    private AuthResult buildAuthResult(User user) {
        String token = jwtProvider.generateToken(user);
        return AuthResult.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
