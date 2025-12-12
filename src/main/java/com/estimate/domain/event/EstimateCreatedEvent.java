package com.estimate.domain.event;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class EstimateCreatedEvent implements DomainEvent {
    String estimateId;
    String userId;
    @Builder.Default
    Instant occurredOn = Instant.now();
    
    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
