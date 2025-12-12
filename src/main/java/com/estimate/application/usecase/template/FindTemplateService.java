package com.estimate.application.usecase.template;

import com.estimate.domain.exception.ResourceNotFoundException;
import com.estimate.domain.exception.UnauthorizedAccessException;
import com.estimate.domain.model.RenovationTemplate;
import com.estimate.domain.port.in.template.FindTemplateUseCase;
import com.estimate.domain.port.out.RenovationTemplateRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindTemplateService implements FindTemplateUseCase {
    
    private final RenovationTemplateRepositoryPort templateRepository;
    
    @Override
    public Mono<RenovationTemplate> findById(String templateId, String userId) {
        return templateRepository.findById(templateId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Template not found")))
                .flatMap(template -> {
                    if (!template.getUserId().equals(userId)) {
                        return Mono.error(new UnauthorizedAccessException("Not authorized to view this template"));
                    }
                    return Mono.just(template);
                });
    }
    
    @Override
    public Flux<RenovationTemplate> findByUserId(String userId) {
        return templateRepository.findByUserId(userId);
    }
}
