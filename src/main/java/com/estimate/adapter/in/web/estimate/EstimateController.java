package com.estimate.adapter.in.web.estimate;

import com.estimate.adapter.in.web.estimate.dto.EstimateRequest;
import com.estimate.adapter.in.web.estimate.dto.EstimateResponse;
import com.estimate.domain.model.Estimate;
import com.estimate.domain.port.in.estimate.*;
import com.estimate.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/estimates")
@RequiredArgsConstructor
public class EstimateController {
    
    private final CreateEstimateUseCase createEstimateUseCase;
    private final UpdateEstimateUseCase updateEstimateUseCase;
    private final DeleteEstimateUseCase deleteEstimateUseCase;
    private final FindEstimateUseCase findEstimateUseCase;
    
    @GetMapping
    public Flux<EstimateResponse> getEstimates(@AuthenticationPrincipal UserPrincipal principal) {
        return findEstimateUseCase.findByUserId(principal.getId())
                .map(this::toResponse);
    }
    
    @GetMapping("/{id}")
    public Mono<EstimateResponse> getEstimate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        return findEstimateUseCase.findById(id, principal.getId())
                .map(this::toResponse);
    }
    
    @PostMapping
    public Mono<EstimateResponse> createEstimate(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody EstimateRequest request) {
        CreateEstimateCommand command = CreateEstimateCommand.builder()
                .userId(principal.getId())
                .investorName(request.getInvestorName())
                .investorAddress(request.getInvestorAddress())
                .templateIds(request.getTemplateIds())
                .workItems(request.getWorkItems())
                .materialDiscount(request.getMaterialDiscount())
                .laborDiscount(request.getLaborDiscount())
                .notes(request.getNotes())
                .validUntil(request.getValidUntil())
                .startDate(request.getStartDate())
                .build();
        
        return createEstimateUseCase.create(command)
                .map(this::toResponse);
    }
    
    @PutMapping("/{id}")
    public Mono<EstimateResponse> updateEstimate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id,
            @Valid @RequestBody EstimateRequest request) {
        UpdateEstimateCommand command = UpdateEstimateCommand.builder()
                .estimateId(id)
                .userId(principal.getId())
                .investorName(request.getInvestorName())
                .investorAddress(request.getInvestorAddress())
                .templateIds(request.getTemplateIds())
                .workItems(request.getWorkItems())
                .materialDiscount(request.getMaterialDiscount())
                .laborDiscount(request.getLaborDiscount())
                .notes(request.getNotes())
                .validUntil(request.getValidUntil())
                .startDate(request.getStartDate())
                .build();
        
        return updateEstimateUseCase.update(command)
                .map(this::toResponse);
    }
    
    @DeleteMapping("/{id}")
    public Mono<Void> deleteEstimate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        return deleteEstimateUseCase.delete(id, principal.getId());
    }
    
    private EstimateResponse toResponse(Estimate estimate) {
        return EstimateResponse.builder()
                .id(estimate.getId())
                .investorName(estimate.getInvestorName())
                .investorAddress(estimate.getInvestorAddress())
                .templateIds(estimate.getTemplateIds())
                .workItems(estimate.getWorkItems())
                .materialDiscount(estimate.getMaterialDiscount())
                .laborDiscount(estimate.getLaborDiscount())
                .materialCost(estimate.calculateMaterialCostWithDiscount())
                .laborCost(estimate.calculateLaborCostWithDiscount())
                .totalCost(estimate.calculateTotalCost())
                .notes(estimate.getNotes())
                .validUntil(estimate.getValidUntil())
                .startDate(estimate.getStartDate())
                .createdAt(estimate.getCreatedAt())
                .updatedAt(estimate.getUpdatedAt())
                .build();
    }
}
