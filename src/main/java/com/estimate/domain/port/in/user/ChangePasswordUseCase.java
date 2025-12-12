package com.estimate.domain.port.in.user;

import reactor.core.publisher.Mono;

public interface ChangePasswordUseCase {
    Mono<Void> changePassword(ChangePasswordCommand command);
}
