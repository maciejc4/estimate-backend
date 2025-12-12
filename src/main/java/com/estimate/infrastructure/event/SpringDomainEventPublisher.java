package com.estimate.infrastructure.event;

import com.estimate.domain.event.DomainEvent;
import com.estimate.domain.event.DomainEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {
    
    private final ApplicationEventPublisher eventPublisher;
    
    public SpringDomainEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Mono<Void> publish(DomainEvent event) {
        return Mono.fromRunnable(() -> {
            log.debug("Publishing domain event: {}", event.getClass().getSimpleName());
            eventPublisher.publishEvent(event);
        });
    }
}
