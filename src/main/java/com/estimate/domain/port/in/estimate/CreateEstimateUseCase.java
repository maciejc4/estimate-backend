package com.estimate.domain.port.in.estimate;

import com.estimate.domain.model.Estimate;
import reactor.core.publisher.Mono;

public interface CreateEstimateUseCase {
    Mono<Estimate> create(CreateEstimateCommand command);
}
