package com.estimate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenovationTemplate {
    
    private String id;
    private String userId;
    private String name;
    @Builder.Default
    private List<String> workIds = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
    
    public void addWork(String workId) {
        if (this.workIds == null) {
            this.workIds = new ArrayList<>();
        }
        this.workIds.add(workId);
    }
    
    public void removeWork(String workId) {
        if (this.workIds != null) {
            this.workIds.remove(workId);
        }
    }
}
