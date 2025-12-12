package com.estimate.domain.event;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class WorkDeletedEvent implements DomainEvent {
    String workId;
    String userId;
    @Builder.Default
    Instant occurredOn = Instant.now();
    
    @Override
    public Instant occurredOn() {
        return occurredOn;
    }
}
