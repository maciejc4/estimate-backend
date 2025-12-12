package com.estimate.domain.event;

import reactor.core.publisher.Mono;

public interface DomainEventPublisher {
    Mono<Void> publish(DomainEvent event);
}
