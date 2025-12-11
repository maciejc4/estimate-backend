package com.estimate.api.controller;

import com.estimate.application.dto.RenovationTemplateRequest;
import com.estimate.application.dto.RenovationTemplateResponse;
import com.estimate.application.service.RenovationTemplateService;
import com.estimate.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class RenovationTemplateController {
    
    private final RenovationTemplateService templateService;
    
    @GetMapping
    public ResponseEntity<List<RenovationTemplateResponse>> getTemplates(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(templateService.getUserTemplates(principal.getId()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RenovationTemplateResponse> getTemplate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        return ResponseEntity.ok(templateService.getTemplate(principal.getId(), id));
    }
    
    @PostMapping
    public ResponseEntity<RenovationTemplateResponse> createTemplate(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody RenovationTemplateRequest request) {
        return ResponseEntity.ok(templateService.createTemplate(principal.getId(), request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RenovationTemplateResponse> updateTemplate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id,
            @Valid @RequestBody RenovationTemplateRequest request) {
        return ResponseEntity.ok(templateService.updateTemplate(principal.getId(), id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        templateService.deleteTemplate(principal.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
