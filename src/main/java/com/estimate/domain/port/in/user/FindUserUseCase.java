package com.estimate.domain.port.in.user;

import com.estimate.domain.model.User;
import reactor.core.publisher.Mono;

public interface FindUserUseCase {
    Mono<User> findById(String userId);
}
