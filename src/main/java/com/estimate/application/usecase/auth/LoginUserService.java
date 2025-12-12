package com.estimate.application.usecase.auth;

import com.estimate.domain.model.AuthenticationResult;
import com.estimate.domain.port.in.auth.AuthResult;
import com.estimate.domain.port.in.auth.LoginUserCommand;
import com.estimate.domain.port.in.auth.LoginUserUseCase;
import com.estimate.domain.port.out.AuthenticationProviderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUserService implements LoginUserUseCase {
    
    private final AuthenticationProviderPort authenticationProvider;
    
    @Override
    public Mono<AuthResult> login(LoginUserCommand command) {
        return authenticationProvider.authenticate(command.getEmail(), command.getPassword())
                .doOnNext(result -> log.info("User logged in: {}", result.getEmail()))
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
