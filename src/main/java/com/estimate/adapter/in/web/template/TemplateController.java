package com.estimate.adapter.in.web.template;

import com.estimate.adapter.in.web.template.dto.TemplateRequest;
import com.estimate.adapter.in.web.template.dto.TemplateResponse;
import com.estimate.domain.model.RenovationTemplate;
import com.estimate.domain.port.in.template.*;
import com.estimate.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {
    
    private final CreateTemplateUseCase createTemplateUseCase;
    private final UpdateTemplateUseCase updateTemplateUseCase;
    private final DeleteTemplateUseCase deleteTemplateUseCase;
    private final FindTemplateUseCase findTemplateUseCase;
    
    @GetMapping
    public Flux<TemplateResponse> getTemplates(@AuthenticationPrincipal UserPrincipal principal) {
        return findTemplateUseCase.findByUserId(principal.getId())
                .map(this::toResponse);
    }
    
    @GetMapping("/{id}")
    public Mono<TemplateResponse> getTemplate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        return findTemplateUseCase.findById(id, principal.getId())
                .map(this::toResponse);
    }
    
    @PostMapping
    public Mono<TemplateResponse> createTemplate(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TemplateRequest request) {
        CreateTemplateCommand command = CreateTemplateCommand.builder()
                .userId(principal.getId())
                .name(request.getName())
                .workIds(request.getWorkIds())
                .build();
        
        return createTemplateUseCase.create(command)
                .map(this::toResponse);
    }
    
    @PutMapping("/{id}")
    public Mono<TemplateResponse> updateTemplate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id,
            @Valid @RequestBody TemplateRequest request) {
        UpdateTemplateCommand command = UpdateTemplateCommand.builder()
                .templateId(id)
                .userId(principal.getId())
                .name(request.getName())
                .workIds(request.getWorkIds())
                .build();
        
        return updateTemplateUseCase.update(command)
                .map(this::toResponse);
    }
    
    @DeleteMapping("/{id}")
    public Mono<Void> deleteTemplate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        return deleteTemplateUseCase.delete(id, principal.getId());
    }
    
    private TemplateResponse toResponse(RenovationTemplate template) {
        return TemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .workIds(template.getWorkIds())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
