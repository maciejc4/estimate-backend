package com.estimate.application.service;

import com.estimate.application.dto.RenovationTemplateRequest;
import com.estimate.application.dto.RenovationTemplateResponse;
import com.estimate.application.dto.WorkResponse;
import com.estimate.domain.model.RenovationTemplate;
import com.estimate.domain.repository.RenovationTemplateRepository;
import com.estimate.domain.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class RenovationTemplateService {
    
    private final RenovationTemplateRepository templateRepository;
    private final WorkRepository workRepository;
    
    public Flux<RenovationTemplateResponse> getUserTemplates(String userId) {
        return templateRepository.findByUserId(userId)
                .flatMap(t -> toResponse(t, userId));
    }
    
    public Mono<RenovationTemplateResponse> getTemplate(String userId, String templateId) {
        return templateRepository.findById(templateId)
                .filter(t -> t.getUserId().equals(userId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Template not found")))
                .flatMap(t -> toResponse(t, userId));
    }
    
    public Mono<RenovationTemplateResponse> createTemplate(String userId, RenovationTemplateRequest request) {
        RenovationTemplate template = RenovationTemplate.builder()
                .userId(userId)
                .name(request.getName())
                .workIds(request.getWorkIds() != null ? request.getWorkIds() : new ArrayList<>())
                .build();
        
        return templateRepository.save(template)
                .doOnNext(saved -> log.info("Template created: {} for user: {}", saved.getName(), userId))
                .flatMap(t -> toResponse(t, userId));
    }
    
    public Mono<RenovationTemplateResponse> updateTemplate(String userId, String templateId, RenovationTemplateRequest request) {
        return templateRepository.findById(templateId)
                .filter(t -> t.getUserId().equals(userId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Template not found")))
                .flatMap(template -> {
                    template.setName(request.getName());
                    template.setWorkIds(request.getWorkIds() != null ? request.getWorkIds() : new ArrayList<>());
                    
                    return templateRepository.save(template);
                })
                .doOnNext(saved -> log.info("Template updated: {} for user: {}", saved.getName(), userId))
                .flatMap(t -> toResponse(t, userId));
    }
    
    public Mono<Void> deleteTemplate(String userId, String templateId) {
        return templateRepository.findById(templateId)
                .filter(t -> t.getUserId().equals(userId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Template not found")))
                .flatMap(template -> templateRepository.delete(template)
                        .doOnSuccess(v -> log.info("Template deleted: {} for user: {}", templateId, userId)));
    }
    
    public Flux<RenovationTemplateResponse> getAllTemplates() {
        return templateRepository.findAll()
                .map(this::toResponseWithoutWorks);
    }
    
    private Mono<RenovationTemplateResponse> toResponse(RenovationTemplate template, String userId) {
        if (template.getWorkIds() == null || template.getWorkIds().isEmpty()) {
            return Mono.just(buildResponse(template, new ArrayList<>()));
        }
        
        return workRepository.findByUserIdAndIdIn(userId, template.getWorkIds())
                .map(w -> WorkResponse.builder()
                        .id(w.getId())
                        .name(w.getName())
                        .unit(w.getUnit())
                        .materials(w.getMaterials())
                        .createdAt(w.getCreatedAt())
                        .updatedAt(w.getUpdatedAt())
                        .build())
                .collectList()
                .map(works -> buildResponse(template, works));
    }
    
    private RenovationTemplateResponse buildResponse(RenovationTemplate template, java.util.List<WorkResponse> works) {
        return RenovationTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .workIds(template.getWorkIds())
                .works(works)
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
    
    private RenovationTemplateResponse toResponseWithoutWorks(RenovationTemplate template) {
        return RenovationTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .workIds(template.getWorkIds())
                .works(new ArrayList<>())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
