package com.estimate.domain.port.in.estimate;

import reactor.core.publisher.Mono;

public interface DeleteEstimateUseCase {
    Mono<Void> delete(String estimateId, String userId);
}
