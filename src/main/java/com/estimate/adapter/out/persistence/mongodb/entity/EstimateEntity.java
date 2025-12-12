package com.estimate.adapter.out.persistence.mongodb.entity;

import com.estimate.domain.model.EstimateWorkItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "estimates")
public class EstimateEntity {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    private String investorName;
    
    private String investorAddress;
    
    @Builder.Default
    private List<String> templateIds = new ArrayList<>();
    
    @Builder.Default
    private List<EstimateWorkItem> workItems = new ArrayList<>();
    
    @Builder.Default
    private BigDecimal materialDiscount = BigDecimal.ZERO;
    
    @Builder.Default
    private BigDecimal laborDiscount = BigDecimal.ZERO;
    
    private String notes;
    
    private LocalDate validUntil;
    
    private LocalDate startDate;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
