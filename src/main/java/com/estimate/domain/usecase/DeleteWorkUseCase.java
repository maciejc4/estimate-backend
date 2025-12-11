package com.estimate.domain.usecase;

import reactor.core.publisher.Mono;

public interface DeleteWorkUseCase {
    Mono<Void> execute(String userId, String workId);
}
