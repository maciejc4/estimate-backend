package com.estimate.application.usecase.template;

import com.estimate.domain.exception.ResourceNotFoundException;
import com.estimate.domain.exception.UnauthorizedAccessException;
import com.estimate.domain.model.RenovationTemplate;
import com.estimate.domain.port.in.template.UpdateTemplateCommand;
import com.estimate.domain.port.in.template.UpdateTemplateUseCase;
import com.estimate.domain.port.out.RenovationTemplateRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateTemplateService implements UpdateTemplateUseCase {
    
    private final RenovationTemplateRepositoryPort templateRepository;
    
    @Override
    public Mono<RenovationTemplate> update(UpdateTemplateCommand command) {
        return templateRepository.findById(command.getTemplateId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Template not found")))
                .flatMap(existing -> {
                    if (!existing.getUserId().equals(command.getUserId())) {
                        return Mono.error(new UnauthorizedAccessException("Not authorized to update this template"));
                    }
                    
                    existing.setName(command.getName());
                    existing.setWorkIds(command.getWorkIds());
                    
                    return templateRepository.save(existing);
                })
                .doOnNext(updated -> log.info("Template updated: {}", updated.getId()));
    }
}
