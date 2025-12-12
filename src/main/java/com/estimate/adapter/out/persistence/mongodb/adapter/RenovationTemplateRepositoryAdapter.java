package com.estimate.adapter.out.persistence.mongodb.adapter;

import com.estimate.adapter.out.persistence.mongodb.mapper.RenovationTemplateEntityMapper;
import com.estimate.adapter.out.persistence.mongodb.repository.RenovationTemplateMongoRepository;
import com.estimate.domain.model.RenovationTemplate;
import com.estimate.domain.port.out.RenovationTemplateRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RenovationTemplateRepositoryAdapter implements RenovationTemplateRepositoryPort {
    
    private final RenovationTemplateMongoRepository mongoRepository;
    private final RenovationTemplateEntityMapper mapper;
    
    @Override
    public Mono<RenovationTemplate> save(RenovationTemplate template) {
        return Mono.just(template)
                .map(mapper::toEntity)
                .flatMap(mongoRepository::save)
                .map(mapper::toDomain);
    }
    
    @Override
    public Mono<RenovationTemplate> findById(String id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Flux<RenovationTemplate> findByUserId(String userId) {
        return mongoRepository.findByUserId(userId)
                .map(mapper::toDomain);
    }
    
    @Override
    public Flux<RenovationTemplate> findAll() {
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
