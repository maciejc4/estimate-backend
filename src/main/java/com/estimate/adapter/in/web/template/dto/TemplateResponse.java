package com.estimate.adapter.in.web.template.dto;

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
public class TemplateResponse {
    
    private String id;
    private String name;
    private List<String> workIds;
    private Instant createdAt;
    private Instant updatedAt;
}
