package com.estimate.domain.usecase;

import com.estimate.domain.model.User;
import reactor.core.publisher.Mono;

public interface RegisterUserUseCase {
    Mono<User> execute(String email, String password, String companyName, String phone);
}
