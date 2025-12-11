package com.estimate.application.service;

import com.estimate.application.dto.WorkRequest;
import com.estimate.application.dto.WorkResponse;
import com.estimate.domain.model.Work;
import com.estimate.domain.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkService {
    
    private final WorkRepository workRepository;
    
    public List<WorkResponse> getUserWorks(String userId) {
        return workRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }
    
    public WorkResponse getWork(String userId, String workId) {
        Work work = workRepository.findById(workId)
                .filter(w -> w.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Work not found"));
        
        return toResponse(work);
    }
    
    public WorkResponse createWork(String userId, WorkRequest request) {
        Work work = Work.builder()
                .userId(userId)
                .name(request.getName())
                .unit(request.getUnit())
                .materials(request.getMaterials() != null ? request.getMaterials() : new ArrayList<>())
                .build();
        
        work = workRepository.save(work);
        log.info("Work created: {} for user: {}", work.getName(), userId);
        
        return toResponse(work);
    }
    
    public WorkResponse updateWork(String userId, String workId, WorkRequest request) {
        Work work = workRepository.findById(workId)
                .filter(w -> w.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Work not found"));
        
        work.setName(request.getName());
        work.setUnit(request.getUnit());
        work.setMaterials(request.getMaterials() != null ? request.getMaterials() : new ArrayList<>());
        
        work = workRepository.save(work);
        log.info("Work updated: {} for user: {}", work.getName(), userId);
        
        return toResponse(work);
    }
    
    public void deleteWork(String userId, String workId) {
        Work work = workRepository.findById(workId)
                .filter(w -> w.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Work not found"));
        
        workRepository.delete(work);
        log.info("Work deleted: {} for user: {}", workId, userId);
    }
    
    // Admin methods
    public List<WorkResponse> getAllWorks() {
        return workRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
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
