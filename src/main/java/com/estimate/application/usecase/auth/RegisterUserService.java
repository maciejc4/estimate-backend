package com.estimate.application.usecase.auth;

import com.estimate.domain.exception.EmailAlreadyExistsException;
import com.estimate.domain.model.User;
import com.estimate.domain.port.in.auth.AuthResult;
import com.estimate.domain.port.in.auth.RegisterUserCommand;
import com.estimate.domain.port.in.auth.RegisterUserUseCase;
import com.estimate.domain.port.out.JwtProviderPort;
import com.estimate.domain.port.out.PasswordEncoderPort;
import com.estimate.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {
    
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtProviderPort jwtProvider;
    
    @Override
    public Mono<AuthResult> register(RegisterUserCommand command) {
        return userRepository.existsByEmail(command.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new EmailAlreadyExistsException(command.getEmail()));
                    }
                    
                    User user = User.builder()
                            .email(command.getEmail())
                            .passwordHash(passwordEncoder.encode(command.getPassword()))
                            .companyName(command.getCompanyName())
                            .phone(command.getPhone())
                            .role(User.Role.USER)
                            .build();
                    
                    return userRepository.save(user);
                })
                .doOnNext(user -> log.info("User registered: {}", user.getEmail()))
                .map(this::buildAuthResult);
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
