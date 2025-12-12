package com.estimate.domain.port.in.estimate;

import com.estimate.domain.model.Estimate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FindEstimateUseCase {
    Mono<Estimate> findById(String estimateId, String userId);
    Flux<Estimate> findByUserId(String userId);
}
