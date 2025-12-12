package com.estimate.application.usecase.estimate;

import com.estimate.domain.exception.ResourceNotFoundException;
import com.estimate.domain.exception.UnauthorizedAccessException;
import com.estimate.domain.model.Estimate;
import com.estimate.domain.port.in.estimate.UpdateEstimateCommand;
import com.estimate.domain.port.in.estimate.UpdateEstimateUseCase;
import com.estimate.domain.port.out.EstimateRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateEstimateService implements UpdateEstimateUseCase {
    
    private final EstimateRepositoryPort estimateRepository;
    
    @Override
    public Mono<Estimate> update(UpdateEstimateCommand command) {
        return estimateRepository.findById(command.getEstimateId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Estimate not found")))
                .flatMap(existing -> {
                    if (!existing.getUserId().equals(command.getUserId())) {
                        return Mono.error(new UnauthorizedAccessException("Not authorized to update this estimate"));
                    }
                    
                    existing.setInvestorName(command.getInvestorName());
                    existing.setInvestorAddress(command.getInvestorAddress());
                    existing.setTemplateIds(command.getTemplateIds());
                    existing.setWorkItems(command.getWorkItems());
                    existing.setMaterialDiscount(command.getMaterialDiscount());
                    existing.setLaborDiscount(command.getLaborDiscount());
                    existing.setNotes(command.getNotes());
                    existing.setValidUntil(command.getValidUntil());
                    existing.setStartDate(command.getStartDate());
                    
                    return estimateRepository.save(existing);
                })
                .doOnNext(updated -> log.info("Estimate updated: {}", updated.getId()));
    }
}
