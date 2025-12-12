package com.estimate.adapter.out.persistence.mongodb.repository;

import com.estimate.adapter.out.persistence.mongodb.entity.UserEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserMongoRepository extends ReactiveMongoRepository<UserEntity, String> {
    
    Mono<UserEntity> findByEmail(String email);
    
    Mono<Boolean> existsByEmail(String email);
}
