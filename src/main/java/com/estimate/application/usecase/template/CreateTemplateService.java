package com.estimate.application.usecase.template;

import com.estimate.domain.model.RenovationTemplate;
import com.estimate.domain.port.in.template.CreateTemplateCommand;
import com.estimate.domain.port.in.template.CreateTemplateUseCase;
import com.estimate.domain.port.out.RenovationTemplateRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateTemplateService implements CreateTemplateUseCase {
    
    private final RenovationTemplateRepositoryPort templateRepository;
    
    @Override
    public Mono<RenovationTemplate> create(CreateTemplateCommand command) {
        RenovationTemplate template = RenovationTemplate.builder()
                .userId(command.getUserId())
                .name(command.getName())
                .workIds(command.getWorkIds())
                .build();
        
        return templateRepository.save(template)
                .doOnNext(saved -> log.info("Template created: {} for user: {}", saved.getName(), saved.getUserId()));
    }
}
