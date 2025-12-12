package com.estimate.adapter.out.persistence.mongodb.mapper;

import com.estimate.adapter.out.persistence.mongodb.entity.WorkEntity;
import com.estimate.domain.model.Work;
import org.springframework.stereotype.Component;

@Component
public class WorkEntityMapper {
    
    public WorkEntity toEntity(Work domain) {
        if (domain == null) {
            return null;
        }
        return WorkEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .name(domain.getName())
                .unit(domain.getUnit())
                .materials(domain.getMaterials())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
    
    public Work toDomain(WorkEntity entity) {
        if (entity == null) {
            return null;
        }
        return Work.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .name(entity.getName())
                .unit(entity.getUnit())
                .materials(entity.getMaterials())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
