package com.estimate.adapter.out.persistence.mongodb.adapter;

import com.estimate.adapter.out.persistence.mongodb.mapper.WorkEntityMapper;
import com.estimate.adapter.out.persistence.mongodb.repository.WorkMongoRepository;
import com.estimate.domain.model.Work;
import com.estimate.domain.port.out.WorkRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WorkRepositoryAdapter implements WorkRepositoryPort {
    
    private final WorkMongoRepository mongoRepository;
    private final WorkEntityMapper mapper;
    
    @Override
    public Mono<Work> save(Work work) {
        return Mono.just(work)
                .map(mapper::toEntity)
                .flatMap(mongoRepository::save)
                .map(mapper::toDomain);
    }
    
    @Override
    public Mono<Work> findById(String id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Flux<Work> findByUserId(String userId) {
        return mongoRepository.findByUserId(userId)
                .map(mapper::toDomain);
    }
    
    @Override
    public Flux<Work> findByUserIdAndIdIn(String userId, List<String> ids) {
        return mongoRepository.findByUserIdAndIdIn(userId, ids)
                .map(mapper::toDomain);
    }
    
    @Override
    public Flux<Work> findAll() {
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
