package com.estimate.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenovationTemplateResponse {
    
    private String id;
    private String name;
    private List<String> workIds;
    private List<WorkResponse> works;
    private Instant createdAt;
    private Instant updatedAt;
}
