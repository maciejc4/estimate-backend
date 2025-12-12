package com.estimate.domain.port.out;

import com.estimate.domain.model.Estimate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EstimateRepositoryPort {
    
    Mono<Estimate> save(Estimate estimate);
    
    Mono<Estimate> findById(String id);
    
    Flux<Estimate> findByUserId(String userId);
    
    Flux<Estimate> findAll();
    
    Mono<Void> deleteById(String id);
    
    Mono<Void> deleteByUserId(String userId);
}
