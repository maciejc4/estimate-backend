package com.estimate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstimateWorkItem {
    
    private String workId;
    
    private String workName;
    
    private String unit;
    
    private BigDecimal quantity;
    
    private BigDecimal laborPricePerUnit;
    
    @Builder.Default
    private List<EstimateMaterialPrice> materialPrices = new ArrayList<>();
    
    public BigDecimal calculateLaborCost() {
        if (quantity == null || laborPricePerUnit == null) {
            return BigDecimal.ZERO;
        }
        return quantity.multiply(laborPricePerUnit);
    }
    
    public BigDecimal calculateMaterialCost() {
        if (materialPrices == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return materialPrices.stream()
                .map(mp -> mp.calculateCost(quantity))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
