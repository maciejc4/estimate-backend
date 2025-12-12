package com.estimate.domain.port.out;

import com.estimate.domain.model.Work;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface WorkRepositoryPort {
    
    Mono<Work> save(Work work);
    
    Mono<Work> findById(String id);
    
    Flux<Work> findByUserId(String userId);
    
    Flux<Work> findByUserIdAndIdIn(String userId, List<String> ids);
    
    Flux<Work> findAll();
    
    Mono<Void> deleteById(String id);
    
    Mono<Void> deleteByUserId(String userId);
}
