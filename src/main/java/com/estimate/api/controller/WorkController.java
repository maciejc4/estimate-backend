package com.estimate.api.controller;

import com.estimate.application.dto.WorkRequest;
import com.estimate.application.dto.WorkResponse;
import com.estimate.application.service.WorkService;
import com.estimate.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
public class WorkController {
    
    private final WorkService workService;
    
    @GetMapping
    public Flux<WorkResponse> getWorks(@AuthenticationPrincipal UserPrincipal principal) {
        return workService.getUserWorks(principal.getId());
    }
    
    @GetMapping("/{id}")
    public Mono<WorkResponse> getWork(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        return workService.getWork(principal.getId(), id);
    }
    
    @PostMapping
    public Mono<WorkResponse> createWork(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody WorkRequest request) {
        return workService.createWork(principal.getId(), request);
    }
    
    @PutMapping("/{id}")
    public Mono<WorkResponse> updateWork(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id,
            @Valid @RequestBody WorkRequest request) {
        return workService.updateWork(principal.getId(), id, request);
    }
    
    @DeleteMapping("/{id}")
    public Mono<Void> deleteWork(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        return workService.deleteWork(principal.getId(), id);
    }
}
