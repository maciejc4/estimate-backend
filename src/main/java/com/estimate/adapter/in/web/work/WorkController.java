package com.estimate.adapter.in.web.work;

import com.estimate.adapter.in.web.work.dto.WorkRequest;
import com.estimate.adapter.in.web.work.dto.WorkResponse;
import com.estimate.domain.model.Work;
import com.estimate.domain.port.in.work.*;
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
    
    private final CreateWorkUseCase createWorkUseCase;
    private final UpdateWorkUseCase updateWorkUseCase;
    private final DeleteWorkUseCase deleteWorkUseCase;
    private final FindWorkUseCase findWorkUseCase;
    
    @GetMapping
    public Flux<WorkResponse> getWorks(@AuthenticationPrincipal UserPrincipal principal) {
        return findWorkUseCase.findByUserId(principal.getId())
                .map(this::toResponse);
    }
    
    @GetMapping("/{id}")
    public Mono<WorkResponse> getWork(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        return findWorkUseCase.findById(id, principal.getId())
                .map(this::toResponse);
    }
    
    @PostMapping
    public Mono<WorkResponse> createWork(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody WorkRequest request) {
        CreateWorkCommand command = CreateWorkCommand.builder()
                .userId(principal.getId())
                .name(request.getName())
                .unit(request.getUnit())
                .materials(request.getMaterials())
                .build();
        
        return createWorkUseCase.create(command)
                .map(this::toResponse);
    }
    
    @PutMapping("/{id}")
    public Mono<WorkResponse> updateWork(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id,
            @Valid @RequestBody WorkRequest request) {
        UpdateWorkCommand command = UpdateWorkCommand.builder()
                .workId(id)
                .userId(principal.getId())
                .name(request.getName())
                .unit(request.getUnit())
                .materials(request.getMaterials())
                .build();
        
        return updateWorkUseCase.update(command)
                .map(this::toResponse);
    }
    
    @DeleteMapping("/{id}")
    public Mono<Void> deleteWork(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        return deleteWorkUseCase.delete(id, principal.getId());
    }
    
    private WorkResponse toResponse(Work work) {
        return WorkResponse.builder()
                .id(work.getId())
                .name(work.getName())
                .unit(work.getUnit())
                .materials(work.getMaterials())
                .createdAt(work.getCreatedAt())
                .updatedAt(work.getUpdatedAt())
                .build();
    }
}
