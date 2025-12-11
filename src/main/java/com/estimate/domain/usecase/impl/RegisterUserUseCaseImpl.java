package com.estimate.domain.usecase.impl;

import com.estimate.domain.model.User;
import com.estimate.domain.repository.UserRepository;
import com.estimate.domain.usecase.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public Mono<User> execute(String email, String password, String companyName, String phone) {
        return userRepository.existsByEmail(email)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Email already registered"));
                    }
                    
                    User user = User.builder()
                            .email(email)
                            .passwordHash(passwordEncoder.encode(password))
                            .companyName(companyName)
                            .phone(phone)
                            .role(User.Role.USER)
                            .build();
                    
                    return userRepository.save(user);
                });
    }
}
