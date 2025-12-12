package com.estimate.domain.port.out;

import com.estimate.domain.model.RenovationTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RenovationTemplateRepositoryPort {
    
    Mono<RenovationTemplate> save(RenovationTemplate template);
    
    Mono<RenovationTemplate> findById(String id);
    
    Flux<RenovationTemplate> findByUserId(String userId);
    
    Flux<RenovationTemplate> findAll();
    
    Mono<Void> deleteById(String id);
    
    Mono<Void> deleteByUserId(String userId);
}
