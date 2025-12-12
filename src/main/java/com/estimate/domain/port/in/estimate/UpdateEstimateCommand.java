package com.estimate.domain.port.in.estimate;

import com.estimate.domain.model.EstimateWorkItem;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class UpdateEstimateCommand {
    String estimateId;
    String userId;
    String investorName;
    String investorAddress;
    List<String> templateIds;
    List<EstimateWorkItem> workItems;
    BigDecimal materialDiscount;
    BigDecimal laborDiscount;
    String notes;
    LocalDate validUntil;
    LocalDate startDate;
}
