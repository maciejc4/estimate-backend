package com.estimate.domain.repository;

import com.estimate.domain.model.RenovationTemplate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RenovationTemplateRepository extends ReactiveMongoRepository<RenovationTemplate, String> {
    
    Flux<RenovationTemplate> findByUserId(String userId);
    
    Mono<Void> deleteByUserId(String userId);
}
