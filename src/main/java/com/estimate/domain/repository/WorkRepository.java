package com.estimate.domain.repository;

import com.estimate.domain.model.Work;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface WorkRepository extends ReactiveMongoRepository<Work, String> {
    
    Flux<Work> findByUserId(String userId);
    
    Flux<Work> findByUserIdAndIdIn(String userId, List<String> ids);
    
    Mono<Void> deleteByUserId(String userId);
}
