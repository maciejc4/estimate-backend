package com.estimate.application.service;

import com.estimate.application.dto.EstimateRequest;
import com.estimate.application.dto.EstimateResponse;
import com.estimate.domain.model.Estimate;
import com.estimate.domain.repository.EstimateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstimateService {
    
    private final EstimateRepository estimateRepository;
    
    public Flux<EstimateResponse> getUserEstimates(String userId) {
        return estimateRepository.findByUserId(userId)
                .map(this::toResponse);
    }
    
    public Mono<EstimateResponse> getEstimate(String userId, String estimateId) {
        return findUserEstimate(userId, estimateId)
                .map(this::toResponse);
    }
    
    public Mono<EstimateResponse> createEstimate(String userId, EstimateRequest request) {
        Estimate estimate = buildEstimate(userId, request);
        
        return estimateRepository.save(estimate)
                .doOnNext(saved -> log.info("Estimate created for investor: {} by user: {}", saved.getInvestorName(), userId))
                .map(this::toResponse);
    }
    
    public Mono<EstimateResponse> updateEstimate(String userId, String estimateId, EstimateRequest request) {
        return findUserEstimate(userId, estimateId)
                .flatMap(estimate -> {
                    updateEstimateFields(estimate, request);
                    return estimateRepository.save(estimate);
                })
                .doOnNext(saved -> log.info("Estimate updated for investor: {} by user: {}", saved.getInvestorName(), userId))
                .map(this::toResponse);
    }
    
    public Mono<Void> deleteEstimate(String userId, String estimateId) {
        return findUserEstimate(userId, estimateId)
                .flatMap(estimate -> estimateRepository.delete(estimate)
                        .doOnSuccess(v -> log.info("Estimate deleted: {} for user: {}", estimateId, userId)));
    }
    
    public Flux<EstimateResponse> getAllEstimates() {
        return estimateRepository.findAll()
                .map(this::toResponse);
    }
    
    private Mono<Estimate> findUserEstimate(String userId, String estimateId) {
        return estimateRepository.findById(estimateId)
                .filter(e -> e.getUserId().equals(userId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Estimate not found")));
    }
    
    private Estimate buildEstimate(String userId, EstimateRequest request) {
        return Estimate.builder()
                .userId(userId)
                .investorName(request.getInvestorName())
                .investorAddress(request.getInvestorAddress())
                .templateIds(request.getTemplateIds() != null ? request.getTemplateIds() : new ArrayList<>())
                .workItems(request.getWorkItems() != null ? request.getWorkItems() : new ArrayList<>())
                .materialDiscount(request.getMaterialDiscount() != null ? request.getMaterialDiscount() : BigDecimal.ZERO)
                .laborDiscount(request.getLaborDiscount() != null ? request.getLaborDiscount() : BigDecimal.ZERO)
                .notes(request.getNotes())
                .validUntil(request.getValidUntil())
                .startDate(request.getStartDate())
                .build();
    }
    
    private void updateEstimateFields(Estimate estimate, EstimateRequest request) {
        estimate.setInvestorName(request.getInvestorName());
        estimate.setInvestorAddress(request.getInvestorAddress());
        estimate.setTemplateIds(request.getTemplateIds() != null ? request.getTemplateIds() : new ArrayList<>());
        estimate.setWorkItems(request.getWorkItems() != null ? request.getWorkItems() : new ArrayList<>());
        estimate.setMaterialDiscount(request.getMaterialDiscount() != null ? request.getMaterialDiscount() : BigDecimal.ZERO);
        estimate.setLaborDiscount(request.getLaborDiscount() != null ? request.getLaborDiscount() : BigDecimal.ZERO);
        estimate.setNotes(request.getNotes());
        estimate.setValidUntil(request.getValidUntil());
        estimate.setStartDate(request.getStartDate());
    }
    
    private EstimateResponse toResponse(Estimate estimate) {
        return EstimateResponse.builder()
                .id(estimate.getId())
                .investorName(estimate.getInvestorName())
                .investorAddress(estimate.getInvestorAddress())
                .templateIds(estimate.getTemplateIds())
                .workItems(estimate.getWorkItems())
                .materialCost(estimate.calculateMaterialCost())
                .laborCost(estimate.calculateLaborCost())
                .materialDiscount(estimate.getMaterialDiscount())
                .laborDiscount(estimate.getLaborDiscount())
                .materialCostWithDiscount(estimate.calculateMaterialCostWithDiscount())
                .laborCostWithDiscount(estimate.calculateLaborCostWithDiscount())
                .totalCost(estimate.calculateTotalCost())
                .notes(estimate.getNotes())
                .validUntil(estimate.getValidUntil())
                .startDate(estimate.getStartDate())
                .createdAt(estimate.getCreatedAt())
                .updatedAt(estimate.getUpdatedAt())
                .build();
    }
}
