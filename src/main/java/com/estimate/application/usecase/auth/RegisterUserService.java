package com.estimate.application.usecase.auth;

import com.estimate.domain.model.AuthenticationResult;
import com.estimate.domain.model.RegisterUserData;
import com.estimate.domain.port.in.auth.AuthResult;
import com.estimate.domain.port.in.auth.RegisterUserCommand;
import com.estimate.domain.port.in.auth.RegisterUserUseCase;
import com.estimate.domain.port.out.AuthenticationProviderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {
    
    private final AuthenticationProviderPort authenticationProvider;
    
    @Override
    public Mono<AuthResult> register(RegisterUserCommand command) {
        RegisterUserData userData = RegisterUserData.builder()
                .email(command.getEmail())
                .password(command.getPassword())
                .companyName(command.getCompanyName())
                .phone(command.getPhone())
                .build();
        
        return authenticationProvider.registerUser(userData)
                .doOnNext(result -> log.info("User registered: {}", result.getEmail()))
                .map(this::toAuthResult);
    }
    
    private AuthResult toAuthResult(AuthenticationResult result) {
        return AuthResult.builder()
                .token(result.getToken())
                .userId(result.getUserId())
                .email(result.getEmail())
                .role(result.getRole())
                .build();
    }
}
