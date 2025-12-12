package com.estimate.adapter.out.persistence.mongodb.mapper;

import com.estimate.adapter.out.persistence.mongodb.entity.EstimateEntity;
import com.estimate.domain.model.Estimate;
import org.springframework.stereotype.Component;

@Component
public class EstimateEntityMapper {
    
    public EstimateEntity toEntity(Estimate domain) {
        if (domain == null) {
            return null;
        }
        return EstimateEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .investorName(domain.getInvestorName())
                .investorAddress(domain.getInvestorAddress())
                .templateIds(domain.getTemplateIds())
                .workItems(domain.getWorkItems())
                .materialDiscount(domain.getMaterialDiscount())
                .laborDiscount(domain.getLaborDiscount())
                .notes(domain.getNotes())
                .validUntil(domain.getValidUntil())
                .startDate(domain.getStartDate())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
    
    public Estimate toDomain(EstimateEntity entity) {
        if (entity == null) {
            return null;
        }
        return Estimate.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .investorName(entity.getInvestorName())
                .investorAddress(entity.getInvestorAddress())
                .templateIds(entity.getTemplateIds())
                .workItems(entity.getWorkItems())
                .materialDiscount(entity.getMaterialDiscount())
                .laborDiscount(entity.getLaborDiscount())
                .notes(entity.getNotes())
                .validUntil(entity.getValidUntil())
                .startDate(entity.getStartDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
