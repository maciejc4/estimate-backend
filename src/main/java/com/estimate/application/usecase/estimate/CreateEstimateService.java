package com.estimate.application.usecase.estimate;

import com.estimate.domain.model.Estimate;
import com.estimate.domain.port.in.estimate.CreateEstimateCommand;
import com.estimate.domain.port.in.estimate.CreateEstimateUseCase;
import com.estimate.domain.port.out.EstimateRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateEstimateService implements CreateEstimateUseCase {
    
    private final EstimateRepositoryPort estimateRepository;
    
    @Override
    public Mono<Estimate> create(CreateEstimateCommand command) {
        Estimate estimate = Estimate.builder()
                .userId(command.getUserId())
                .investorName(command.getInvestorName())
                .investorAddress(command.getInvestorAddress())
                .templateIds(command.getTemplateIds())
                .workItems(command.getWorkItems())
                .materialDiscount(command.getMaterialDiscount())
                .laborDiscount(command.getLaborDiscount())
                .notes(command.getNotes())
                .validUntil(command.getValidUntil())
                .startDate(command.getStartDate())
                .build();
        
        return estimateRepository.save(estimate)
                .doOnNext(saved -> log.info("Estimate created: {} for user: {}", saved.getId(), saved.getUserId()));
    }
}
