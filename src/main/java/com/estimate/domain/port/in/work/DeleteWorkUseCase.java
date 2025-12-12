package com.estimate.domain.port.in.work;

import reactor.core.publisher.Mono;

public interface DeleteWorkUseCase {
    Mono<Void> delete(String workId, String userId);
}
