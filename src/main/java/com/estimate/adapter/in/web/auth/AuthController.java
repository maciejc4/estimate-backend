package com.estimate.adapter.in.web.auth;

import com.estimate.adapter.in.web.auth.dto.AuthResponse;
import com.estimate.adapter.in.web.auth.dto.LoginRequest;
import com.estimate.adapter.in.web.auth.dto.RegisterRequest;
import com.estimate.domain.port.in.auth.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    
    @PostMapping("/register")
    public Mono<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterUserCommand command = RegisterUserCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .companyName(request.getCompanyName())
                .phone(request.getPhone())
                .build();
        
        return registerUserUseCase.register(command)
                .map(this::toResponse);
    }
    
    @PostMapping("/login")
    public Mono<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginUserCommand command = LoginUserCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
        
        return loginUserUseCase.login(command)
                .map(this::toResponse);
    }
    
    private AuthResponse toResponse(AuthResult result) {
        return AuthResponse.builder()
                .token(result.getToken())
                .userId(result.getUserId())
                .email(result.getEmail())
                .role(result.getRole())
                .build();
    }
}
