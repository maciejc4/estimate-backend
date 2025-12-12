package com.estimate.domain.port.in.template;

import com.estimate.domain.model.RenovationTemplate;
import reactor.core.publisher.Mono;

public interface UpdateTemplateUseCase {
    Mono<RenovationTemplate> update(UpdateTemplateCommand command);
}
