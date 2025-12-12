package com.estimate.domain.port.in.user;

import reactor.core.publisher.Mono;

public interface DeleteUserUseCase {
    Mono<Void> delete(String userId);
}
