package com.estimate.api.controller;

import com.estimate.application.dto.RenovationTemplateRequest;
import com.estimate.application.dto.RenovationTemplateResponse;
import com.estimate.application.service.RenovationTemplateService;
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
public class RenovationTemplateController {
    
    private final RenovationTemplateService templateService;
    
    @GetMapping
    public Flux<RenovationTemplateResponse> getTemplates(
            @AuthenticationPrincipal UserPrincipal principal) {
        return templateService.getUserTemplates(principal.getId());
    }
    
    @GetMapping("/{id}")
    public Mono<RenovationTemplateResponse> getTemplate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        return templateService.getTemplate(principal.getId(), id);
    }
    
    @PostMapping
    public Mono<RenovationTemplateResponse> createTemplate(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody RenovationTemplateRequest request) {
        return templateService.createTemplate(principal.getId(), request);
    }
    
    @PutMapping("/{id}")
    public Mono<RenovationTemplateResponse> updateTemplate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id,
            @Valid @RequestBody RenovationTemplateRequest request) {
        return templateService.updateTemplate(principal.getId(), id, request);
    }
    
    @DeleteMapping("/{id}")
    public Mono<Void> deleteTemplate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        return templateService.deleteTemplate(principal.getId(), id);
    }
}
