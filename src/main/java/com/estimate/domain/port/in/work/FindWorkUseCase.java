package com.estimate.domain.port.in.work;

import com.estimate.domain.model.Work;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FindWorkUseCase {
    Mono<Work> findById(String workId, String userId);
    Flux<Work> findByUserId(String userId);
    Flux<Work> findAll();
}
