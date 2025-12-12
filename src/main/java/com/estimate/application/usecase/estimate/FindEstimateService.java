package com.estimate.application.usecase.estimate;

import com.estimate.domain.exception.ResourceNotFoundException;
import com.estimate.domain.exception.UnauthorizedAccessException;
import com.estimate.domain.model.Estimate;
import com.estimate.domain.port.in.estimate.FindEstimateUseCase;
import com.estimate.domain.port.out.EstimateRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindEstimateService implements FindEstimateUseCase {
    
    private final EstimateRepositoryPort estimateRepository;
    
    @Override
    public Mono<Estimate> findById(String estimateId, String userId) {
        return estimateRepository.findById(estimateId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Estimate not found")))
                .flatMap(estimate -> {
                    if (!estimate.getUserId().equals(userId)) {
                        return Mono.error(new UnauthorizedAccessException("Not authorized to view this estimate"));
                    }
                    return Mono.just(estimate);
                });
    }
    
    @Override
    public Flux<Estimate> findByUserId(String userId) {
        return estimateRepository.findByUserId(userId);
    }
}
