package com.estimate.application.service;

import com.estimate.application.dto.WorkRequest;
import com.estimate.application.dto.WorkResponse;
import com.estimate.domain.model.Work;
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
public class WorkService {
    
    private final WorkRepository workRepository;
    
    public Flux<WorkResponse> getUserWorks(String userId) {
        return workRepository.findByUserId(userId)
                .map(this::toResponse);
    }
    
    public Mono<WorkResponse> getWork(String userId, String workId) {
        return findUserWork(userId, workId)
                .map(this::toResponse);
    }
    
    public Mono<WorkResponse> createWork(String userId, WorkRequest request) {
        Work work = buildWork(userId, request);
        
        return workRepository.save(work)
                .doOnNext(saved -> log.info("Work created: {} for user: {}", saved.getName(), userId))
                .map(this::toResponse);
    }
    
    public Mono<WorkResponse> updateWork(String userId, String workId, WorkRequest request) {
        return findUserWork(userId, workId)
                .flatMap(work -> {
                    updateWorkFields(work, request);
                    return workRepository.save(work);
                })
                .doOnNext(saved -> log.info("Work updated: {} for user: {}", saved.getName(), userId))
                .map(this::toResponse);
    }
    
    public Mono<Void> deleteWork(String userId, String workId) {
        return findUserWork(userId, workId)
                .flatMap(work -> workRepository.delete(work)
                        .doOnSuccess(v -> log.info("Work deleted: {} for user: {}", workId, userId)));
    }
    
    public Flux<WorkResponse> getAllWorks() {
        return workRepository.findAll()
                .map(this::toResponse);
    }
    
    private Mono<Work> findUserWork(String userId, String workId) {
        return workRepository.findById(workId)
                .filter(w -> w.getUserId().equals(userId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Work not found")));
    }
    
    private Work buildWork(String userId, WorkRequest request) {
        return Work.builder()
                .userId(userId)
                .name(request.getName())
                .unit(request.getUnit())
                .materials(request.getMaterials() != null ? request.getMaterials() : new ArrayList<>())
                .build();
    }
    
    private void updateWorkFields(Work work, WorkRequest request) {
        work.setName(request.getName());
        work.setUnit(request.getUnit());
        work.setMaterials(request.getMaterials() != null ? request.getMaterials() : new ArrayList<>());
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
