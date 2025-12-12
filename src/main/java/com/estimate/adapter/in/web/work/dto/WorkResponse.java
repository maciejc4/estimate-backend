package com.estimate.adapter.in.web.work.dto;

import com.estimate.domain.model.Material;
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
public class WorkResponse {
    
    private String id;
    private String name;
    private String unit;
    private List<Material> materials;
    private Instant createdAt;
    private Instant updatedAt;
}
