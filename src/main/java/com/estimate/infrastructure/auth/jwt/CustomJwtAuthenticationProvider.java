package com.estimate.infrastructure.auth.jwt;

import com.estimate.domain.exception.AccountLockedException;
import com.estimate.domain.exception.EmailAlreadyExistsException;
import com.estimate.domain.exception.InvalidCredentialsException;
import com.estimate.domain.model.AuthProviderType;
import com.estimate.domain.model.AuthenticationResult;
import com.estimate.domain.model.RegisterUserData;
import com.estimate.domain.model.User;
import com.estimate.domain.model.UserAuthInfo;
import com.estimate.domain.port.out.AuthenticationProviderPort;
import com.estimate.domain.port.out.PasswordEncoderPort;
import com.estimate.domain.port.out.UserRepositoryPort;
import com.estimate.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@Profile("jwt")
@RequiredArgsConstructor
public class CustomJwtAuthenticationProvider implements AuthenticationProviderPort {
    
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Value("${app.security.max-login-attempts:5}")
    private int maxLoginAttempts;
    
    @Value("${app.security.lockout-duration-minutes:15}")
    private int lockoutDurationMinutes;
    
    @Override
    public Mono<AuthenticationResult> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()))
                .flatMap(user -> validateAndAuthenticate(user, password))
                .map(this::buildAuthenticationResult);
    }
    
    @Override
    public Mono<AuthenticationResult> registerUser(RegisterUserData userData) {
        return userRepository.existsByEmail(userData.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new EmailAlreadyExistsException(userData.getEmail()));
                    }
                    
                    User user = User.builder()
                            .email(userData.getEmail())
                            .passwordHash(passwordEncoder.encode(userData.getPassword()))
                            .companyName(userData.getCompanyName())
                            .phone(userData.getPhone())
                            .role(User.Role.USER)
                            .build();
                    
                    return userRepository.save(user);
                })
                .map(this::buildAuthenticationResult);
    }
    
    @Override
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> jwtTokenProvider.validateToken(token));
    }
    
    @Override
    public Mono<UserAuthInfo> extractUserInfo(String token) {
        return Mono.fromCallable(() -> {
            String userId = jwtTokenProvider.getUserIdFromToken(token);
            String email = jwtTokenProvider.getEmailFromToken(token);
            String role = jwtTokenProvider.getRoleFromToken(token);
            return new UserAuthInfo(userId, email, role);
        });
    }
    
    @Override
    public boolean managesPasswords() {
        return true;
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
    
    private AuthenticationResult buildAuthenticationResult(User user) {
        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
        
        return AuthenticationResult.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .providerType(AuthProviderType.CUSTOM_JWT)
                .build();
    }
}
