package com.estimate.api.controller;

import com.estimate.application.dto.WorkRequest;
import com.estimate.application.dto.WorkResponse;
import com.estimate.application.service.WorkService;
import com.estimate.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
public class WorkController {
    
    private final WorkService workService;
    
    @GetMapping
    public ResponseEntity<List<WorkResponse>> getWorks(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(workService.getUserWorks(principal.getId()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<WorkResponse> getWork(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        return ResponseEntity.ok(workService.getWork(principal.getId(), id));
    }
    
    @PostMapping
    public ResponseEntity<WorkResponse> createWork(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody WorkRequest request) {
        return ResponseEntity.ok(workService.createWork(principal.getId(), request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<WorkResponse> updateWork(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id,
            @Valid @RequestBody WorkRequest request) {
        return ResponseEntity.ok(workService.updateWork(principal.getId(), id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWork(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        workService.deleteWork(principal.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
