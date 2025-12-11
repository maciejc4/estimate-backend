package com.estimate.domain.usecase.impl;

import com.estimate.domain.model.User;
import com.estimate.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    private RegisterUserUseCaseImpl useCase;
    
    @BeforeEach
    void setUp() {
        useCase = new RegisterUserUseCaseImpl(userRepository, passwordEncoder);
    }
    
    @Test
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        
        User savedUser = User.builder()
                .id("userId")
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .companyName("Test Company")
                .phone("+48 123 456 789")
                .role(User.Role.USER)
                .build();
        
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));
        
        Mono<User> result = useCase.execute("test@example.com", "password", "Test Company", "+48 123 456 789");
        
        StepVerifier.create(result)
                .expectNextMatches(user -> 
                        "userId".equals(user.getId()) &&
                        "test@example.com".equals(user.getEmail()) &&
                        "Test Company".equals(user.getCompanyName())
                )
                .verifyComplete();
    }
    
    @Test
    void shouldFailWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(true));
        
        Mono<User> result = useCase.execute("test@example.com", "password", "Test Company", "+48 123 456 789");
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                        throwable instanceof IllegalArgumentException &&
                        "Email already registered".equals(throwable.getMessage())
                )
                .verify();
    }
}
