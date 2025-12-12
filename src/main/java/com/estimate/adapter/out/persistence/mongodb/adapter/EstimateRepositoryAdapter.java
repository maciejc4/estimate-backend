package com.estimate.adapter.out.persistence.mongodb.adapter;

import com.estimate.adapter.out.persistence.mongodb.mapper.EstimateEntityMapper;
import com.estimate.adapter.out.persistence.mongodb.repository.EstimateMongoRepository;
import com.estimate.domain.model.Estimate;
import com.estimate.domain.port.out.EstimateRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class EstimateRepositoryAdapter implements EstimateRepositoryPort {
    
    private final EstimateMongoRepository mongoRepository;
    private final EstimateEntityMapper mapper;
    
    @Override
    public Mono<Estimate> save(Estimate estimate) {
        return Mono.just(estimate)
                .map(mapper::toEntity)
                .flatMap(mongoRepository::save)
                .map(mapper::toDomain);
    }
    
    @Override
    public Mono<Estimate> findById(String id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Flux<Estimate> findByUserId(String userId) {
        return mongoRepository.findByUserId(userId)
                .map(mapper::toDomain);
    }
    
    @Override
    public Flux<Estimate> findAll() {
        return mongoRepository.findAll()
                .map(mapper::toDomain);
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepository.deleteById(id);
    }
    
    @Override
    public Mono<Void> deleteByUserId(String userId) {
        return mongoRepository.deleteByUserId(userId);
    }
}
