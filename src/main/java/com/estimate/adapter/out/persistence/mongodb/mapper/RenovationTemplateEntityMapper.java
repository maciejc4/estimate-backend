package com.estimate.adapter.out.persistence.mongodb.mapper;

import com.estimate.adapter.out.persistence.mongodb.entity.RenovationTemplateEntity;
import com.estimate.domain.model.RenovationTemplate;
import org.springframework.stereotype.Component;

@Component
public class RenovationTemplateEntityMapper {
    
    public RenovationTemplateEntity toEntity(RenovationTemplate domain) {
        if (domain == null) {
            return null;
        }
        return RenovationTemplateEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .name(domain.getName())
                .workIds(domain.getWorkIds())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
    
    public RenovationTemplate toDomain(RenovationTemplateEntity entity) {
        if (entity == null) {
            return null;
        }
        return RenovationTemplate.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .name(entity.getName())
                .workIds(entity.getWorkIds())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
