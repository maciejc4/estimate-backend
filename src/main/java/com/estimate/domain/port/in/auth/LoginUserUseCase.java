package com.estimate.domain.port.in.auth;

import reactor.core.publisher.Mono;

public interface LoginUserUseCase {
    Mono<AuthResult> login(LoginUserCommand command);
}
