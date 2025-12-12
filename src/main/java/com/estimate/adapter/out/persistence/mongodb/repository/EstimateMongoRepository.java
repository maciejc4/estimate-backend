package com.estimate.adapter.out.persistence.mongodb.repository;

import com.estimate.adapter.out.persistence.mongodb.entity.EstimateEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EstimateMongoRepository extends ReactiveMongoRepository<EstimateEntity, String> {
    
    Flux<EstimateEntity> findByUserId(String userId);
    
    Mono<Void> deleteByUserId(String userId);
}
