package com.estimate.domain.usecase;

import com.estimate.domain.model.Work;
import reactor.core.publisher.Flux;

public interface GetUserWorksUseCase {
    Flux<Work> execute(String userId);
}
