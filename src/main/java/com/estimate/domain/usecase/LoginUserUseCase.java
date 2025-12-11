package com.estimate.domain.usecase;

import com.estimate.domain.model.User;
import reactor.core.publisher.Mono;

public interface LoginUserUseCase {
    Mono<User> execute(String email, String password);
}
