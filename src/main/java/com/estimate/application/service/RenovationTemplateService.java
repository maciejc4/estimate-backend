package com.estimate.application.service;

import com.estimate.application.dto.RenovationTemplateRequest;
import com.estimate.application.dto.RenovationTemplateResponse;
import com.estimate.application.dto.WorkResponse;
import com.estimate.domain.model.RenovationTemplate;
import com.estimate.domain.model.Work;
import com.estimate.domain.repository.RenovationTemplateRepository;
import com.estimate.domain.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RenovationTemplateService {
    
    private final RenovationTemplateRepository templateRepository;
    private final WorkRepository workRepository;
    
    public List<RenovationTemplateResponse> getUserTemplates(String userId) {
        return templateRepository.findByUserId(userId).stream()
                .map(t -> toResponse(t, userId))
                .toList();
    }
    
    public RenovationTemplateResponse getTemplate(String userId, String templateId) {
        RenovationTemplate template = templateRepository.findById(templateId)
                .filter(t -> t.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Template not found"));
        
        return toResponse(template, userId);
    }
    
    public RenovationTemplateResponse createTemplate(String userId, RenovationTemplateRequest request) {
        RenovationTemplate template = RenovationTemplate.builder()
                .userId(userId)
                .name(request.getName())
                .workIds(request.getWorkIds() != null ? request.getWorkIds() : new ArrayList<>())
                .build();
        
        template = templateRepository.save(template);
        log.info("Template created: {} for user: {}", template.getName(), userId);
        
        return toResponse(template, userId);
    }
    
    public RenovationTemplateResponse updateTemplate(String userId, String templateId, RenovationTemplateRequest request) {
        RenovationTemplate template = templateRepository.findById(templateId)
                .filter(t -> t.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Template not found"));
        
        template.setName(request.getName());
        template.setWorkIds(request.getWorkIds() != null ? request.getWorkIds() : new ArrayList<>());
        
        template = templateRepository.save(template);
        log.info("Template updated: {} for user: {}", template.getName(), userId);
        
        return toResponse(template, userId);
    }
    
    public void deleteTemplate(String userId, String templateId) {
        RenovationTemplate template = templateRepository.findById(templateId)
                .filter(t -> t.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Template not found"));
        
        templateRepository.delete(template);
        log.info("Template deleted: {} for user: {}", templateId, userId);
    }
    
    // Admin methods
    public List<RenovationTemplateResponse> getAllTemplates() {
        return templateRepository.findAll().stream()
                .map(t -> toResponseWithoutWorks(t))
                .toList();
    }
    
    private RenovationTemplateResponse toResponse(RenovationTemplate template, String userId) {
        List<WorkResponse> works = new ArrayList<>();
        if (template.getWorkIds() != null && !template.getWorkIds().isEmpty()) {
            List<Work> workList = workRepository.findByUserIdAndIdIn(userId, template.getWorkIds());
            works = workList.stream()
                    .map(w -> WorkResponse.builder()
                            .id(w.getId())
                            .name(w.getName())
                            .unit(w.getUnit())
                            .materials(w.getMaterials())
                            .createdAt(w.getCreatedAt())
                            .updatedAt(w.getUpdatedAt())
                            .build())
                    .toList();
        }
        
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
