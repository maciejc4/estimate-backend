package com.estimate.domain.port.out;

import com.estimate.domain.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepositoryPort {
    
    Mono<User> save(User user);
    
    Mono<User> findById(String id);
    
    Mono<User> findByEmail(String email);
    
    Mono<Boolean> existsByEmail(String email);
    
    Flux<User> findAll();
    
    Mono<Void> deleteById(String id);
}
