package com.estimate.domain.port.in.template;

import com.estimate.domain.model.RenovationTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FindTemplateUseCase {
    Mono<RenovationTemplate> findById(String templateId, String userId);
    Flux<RenovationTemplate> findByUserId(String userId);
}
