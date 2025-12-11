package com.estimate.domain.usecase;

import com.estimate.domain.model.Estimate;
import reactor.core.publisher.Flux;

public interface GetUserEstimatesUseCase {
    Flux<Estimate> execute(String userId);
}
