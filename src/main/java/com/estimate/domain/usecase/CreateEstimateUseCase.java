package com.estimate.domain.usecase;

import com.estimate.domain.model.Estimate;
import com.estimate.domain.model.EstimateWorkItem;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CreateEstimateUseCase {
    Mono<Estimate> execute(String userId, String investorName, String investorAddress, 
                          List<String> templateIds, List<EstimateWorkItem> workItems,
                          BigDecimal materialDiscount, BigDecimal laborDiscount,
                          String notes, LocalDate validUntil, LocalDate startDate);
}
