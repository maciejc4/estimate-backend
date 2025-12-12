package com.estimate.domain.port.in.work;

import com.estimate.domain.model.Work;
import reactor.core.publisher.Mono;

public interface CreateWorkUseCase {
    Mono<Work> create(CreateWorkCommand command);
}
