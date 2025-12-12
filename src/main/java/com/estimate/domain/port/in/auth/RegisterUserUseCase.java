package com.estimate.domain.port.in.auth;

import reactor.core.publisher.Mono;

public interface RegisterUserUseCase {
    Mono<AuthResult> register(RegisterUserCommand command);
}
