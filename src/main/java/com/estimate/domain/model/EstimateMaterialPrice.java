package com.estimate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstimateMaterialPrice {
    
    private String materialName;
    private String unit;
    private BigDecimal consumptionPerWorkUnit;
    private BigDecimal pricePerUnit;
    
    public BigDecimal calculateCost(BigDecimal workQuantity) {
        if (workQuantity == null || consumptionPerWorkUnit == null || pricePerUnit == null) {
            return BigDecimal.ZERO;
        }
        return workQuantity.multiply(consumptionPerWorkUnit).multiply(pricePerUnit);
    }
}
