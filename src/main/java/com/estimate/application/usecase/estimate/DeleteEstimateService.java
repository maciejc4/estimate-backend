package com.estimate.application.usecase.estimate;

import com.estimate.domain.exception.ResourceNotFoundException;
import com.estimate.domain.exception.UnauthorizedAccessException;
import com.estimate.domain.port.in.estimate.DeleteEstimateUseCase;
import com.estimate.domain.port.out.EstimateRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteEstimateService implements DeleteEstimateUseCase {
    
    private final EstimateRepositoryPort estimateRepository;
    
    @Override
    public Mono<Void> delete(String estimateId, String userId) {
        return estimateRepository.findById(estimateId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Estimate not found")))
                .flatMap(estimate -> {
                    if (!estimate.getUserId().equals(userId)) {
                        return Mono.error(new UnauthorizedAccessException("Not authorized to delete this estimate"));
                    }
                    return estimateRepository.deleteById(estimateId);
                })
                .doOnSuccess(v -> log.info("Estimate deleted: {}", estimateId));
    }
}
