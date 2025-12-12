package com.estimate.domain.port.in.template;

import com.estimate.domain.model.RenovationTemplate;
import reactor.core.publisher.Mono;

public interface CreateTemplateUseCase {
    Mono<RenovationTemplate> create(CreateTemplateCommand command);
}
