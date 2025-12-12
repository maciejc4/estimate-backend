package com.estimate.adapter.out.persistence.mongodb.repository;

import com.estimate.adapter.out.persistence.mongodb.entity.WorkEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface WorkMongoRepository extends ReactiveMongoRepository<WorkEntity, String> {
    
    Flux<WorkEntity> findByUserId(String userId);
    
    Flux<WorkEntity> findByUserIdAndIdIn(String userId, List<String> ids);
    
    Mono<Void> deleteByUserId(String userId);
}
