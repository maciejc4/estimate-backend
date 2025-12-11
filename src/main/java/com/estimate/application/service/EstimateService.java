package com.estimate.application.service;

import com.estimate.application.dto.EstimateRequest;
import com.estimate.application.dto.EstimateResponse;
import com.estimate.domain.model.Estimate;
import com.estimate.domain.repository.EstimateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstimateService {
    
    private final EstimateRepository estimateRepository;
    
    public List<EstimateResponse> getUserEstimates(String userId) {
        return estimateRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }
    
    public EstimateResponse getEstimate(String userId, String estimateId) {
        Estimate estimate = estimateRepository.findById(estimateId)
                .filter(e -> e.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Estimate not found"));
        
        return toResponse(estimate);
    }
    
    public EstimateResponse createEstimate(String userId, EstimateRequest request) {
        Estimate estimate = Estimate.builder()
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
        
        estimate = estimateRepository.save(estimate);
        log.info("Estimate created for investor: {} by user: {}", estimate.getInvestorName(), userId);
        
        return toResponse(estimate);
    }
    
    public EstimateResponse updateEstimate(String userId, String estimateId, EstimateRequest request) {
        Estimate estimate = estimateRepository.findById(estimateId)
                .filter(e -> e.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Estimate not found"));
        
        estimate.setInvestorName(request.getInvestorName());
        estimate.setInvestorAddress(request.getInvestorAddress());
        estimate.setTemplateIds(request.getTemplateIds() != null ? request.getTemplateIds() : new ArrayList<>());
        estimate.setWorkItems(request.getWorkItems() != null ? request.getWorkItems() : new ArrayList<>());
        estimate.setMaterialDiscount(request.getMaterialDiscount() != null ? request.getMaterialDiscount() : BigDecimal.ZERO);
        estimate.setLaborDiscount(request.getLaborDiscount() != null ? request.getLaborDiscount() : BigDecimal.ZERO);
        estimate.setNotes(request.getNotes());
        estimate.setValidUntil(request.getValidUntil());
        estimate.setStartDate(request.getStartDate());
        
        estimate = estimateRepository.save(estimate);
        log.info("Estimate updated for investor: {} by user: {}", estimate.getInvestorName(), userId);
        
        return toResponse(estimate);
    }
    
    public void deleteEstimate(String userId, String estimateId) {
        Estimate estimate = estimateRepository.findById(estimateId)
                .filter(e -> e.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Estimate not found"));
        
        estimateRepository.delete(estimate);
        log.info("Estimate deleted: {} for user: {}", estimateId, userId);
    }
    
    // Admin methods
    public List<EstimateResponse> getAllEstimates() {
        return estimateRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }
    
    public Estimate getEstimateEntity(String userId, String estimateId) {
        return estimateRepository.findById(estimateId)
                .filter(e -> e.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Estimate not found"));
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
