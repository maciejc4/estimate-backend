package com.estimate.api.controller;

import com.estimate.application.dto.EstimateRequest;
import com.estimate.application.dto.EstimateResponse;
import com.estimate.application.service.EstimateService;
import com.estimate.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/estimates")
@RequiredArgsConstructor
public class EstimateController {
    
    private final EstimateService estimateService;
    
    @GetMapping
    public Flux<EstimateResponse> getEstimates(
            @AuthenticationPrincipal UserPrincipal principal) {
        return estimateService.getUserEstimates(principal.getId());
    }
    
    @GetMapping("/{id}")
    public Mono<EstimateResponse> getEstimate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        return estimateService.getEstimate(principal.getId(), id);
    }
    
    @PostMapping
    public Mono<EstimateResponse> createEstimate(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody EstimateRequest request) {
        return estimateService.createEstimate(principal.getId(), request);
    }
    
    @PutMapping("/{id}")
    public Mono<EstimateResponse> updateEstimate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id,
            @Valid @RequestBody EstimateRequest request) {
        return estimateService.updateEstimate(principal.getId(), id, request);
    }
    
    @DeleteMapping("/{id}")
    public Mono<Void> deleteEstimate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        return estimateService.deleteEstimate(principal.getId(), id);
    }
}
