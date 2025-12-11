package com.estimate.domain.repository;

import com.estimate.domain.model.Estimate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface EstimateRepository extends ReactiveMongoRepository<Estimate, String> {
    
    Flux<Estimate> findByUserId(String userId);
    
    Mono<Void> deleteByUserId(String userId);
}
