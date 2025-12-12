package com.estimate.domain.port.in.template;

import reactor.core.publisher.Mono;

public interface DeleteTemplateUseCase {
    Mono<Void> delete(String templateId, String userId);
}
