package com.estimate.adapter.out.persistence.mongodb.repository;

import com.estimate.adapter.out.persistence.mongodb.entity.RenovationTemplateEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenovationTemplateMongoRepository extends ReactiveMongoRepository<RenovationTemplateEntity, String> {
    
    Flux<RenovationTemplateEntity> findByUserId(String userId);
    
    Mono<Void> deleteByUserId(String userId);
}
