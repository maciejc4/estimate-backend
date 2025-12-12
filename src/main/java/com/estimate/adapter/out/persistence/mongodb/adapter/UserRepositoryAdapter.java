package com.estimate.adapter.out.persistence.mongodb.adapter;

import com.estimate.adapter.out.persistence.mongodb.mapper.UserEntityMapper;
import com.estimate.adapter.out.persistence.mongodb.repository.UserMongoRepository;
import com.estimate.domain.model.User;
import com.estimate.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {
    
    private final UserMongoRepository mongoRepository;
    private final UserEntityMapper mapper;
    
    @Override
    public Mono<User> save(User user) {
        return Mono.just(user)
                .map(mapper::toEntity)
                .flatMap(mongoRepository::save)
                .map(mapper::toDomain);
    }
    
    @Override
    public Mono<User> findById(String id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Mono<User> findByEmail(String email) {
        return mongoRepository.findByEmail(email)
                .map(mapper::toDomain);
    }
    
    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return mongoRepository.existsByEmail(email);
    }
    
    @Override
    public Flux<User> findAll() {
        return mongoRepository.findAll()
                .map(mapper::toDomain);
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepository.deleteById(id);
    }
}
