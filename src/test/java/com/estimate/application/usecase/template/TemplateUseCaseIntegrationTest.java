package com.estimate.application.usecase.template;

import com.estimate.domain.exception.ResourceNotFoundException;
import com.estimate.domain.exception.UnauthorizedAccessException;
import com.estimate.domain.port.in.template.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class TemplateUseCaseIntegrationTest {
    
    @Autowired
    private CreateTemplateUseCase createTemplateUseCase;
    
    @Autowired
    private UpdateTemplateUseCase updateTemplateUseCase;
    
    @Autowired
    private DeleteTemplateUseCase deleteTemplateUseCase;
    
    @Autowired
    private FindTemplateUseCase findTemplateUseCase;
    
    @Test
    void shouldCreateTemplate() {
        CreateTemplateCommand command = CreateTemplateCommand.builder()
                .userId("user123")
                .name("Bathroom Renovation")
                .workIds(List.of("work1", "work2"))
                .build();
        
        StepVerifier.create(createTemplateUseCase.create(command))
                .expectNextMatches(template -> 
                        template.getId() != null && 
                        template.getName().equals("Bathroom Renovation"))
                .verifyComplete();
    }
    
    @Test
    void shouldFindTemplatesByUserId() {
        CreateTemplateCommand command = CreateTemplateCommand.builder()
                .userId("user456")
                .name("Kitchen Renovation")
                .workIds(new ArrayList<>())
                .build();
        
        StepVerifier.create(
                createTemplateUseCase.create(command)
                        .thenMany(findTemplateUseCase.findByUserId("user456"))
        )
                .expectNextMatches(template -> template.getName().equals("Kitchen Renovation"))
                .verifyComplete();
    }
    
    @Test
    void shouldUpdateTemplate() {
        CreateTemplateCommand createCommand = CreateTemplateCommand.builder()
                .userId("user789")
                .name("Old Name")
                .workIds(new ArrayList<>())
                .build();
        
        StepVerifier.create(
                createTemplateUseCase.create(createCommand)
                        .flatMap(created -> {
                            UpdateTemplateCommand updateCommand = UpdateTemplateCommand.builder()
                                    .templateId(created.getId())
                                    .userId("user789")
                                    .name("New Name")
                                    .workIds(List.of("work3"))
                                    .build();
                            return updateTemplateUseCase.update(updateCommand);
                        })
        )
                .expectNextMatches(updated -> updated.getName().equals("New Name"))
                .verifyComplete();
    }
    
    @Test
    void shouldNotUpdateTemplateFromDifferentUser() {
        CreateTemplateCommand createCommand = CreateTemplateCommand.builder()
                .userId("user1")
                .name("Test Template")
                .workIds(new ArrayList<>())
                .build();
        
        StepVerifier.create(
                createTemplateUseCase.create(createCommand)
                        .flatMap(created -> {
                            UpdateTemplateCommand updateCommand = UpdateTemplateCommand.builder()
                                    .templateId(created.getId())
                                    .userId("user2")
                                    .name("Hacked")
                                    .workIds(new ArrayList<>())
                                    .build();
                            return updateTemplateUseCase.update(updateCommand);
                        })
        )
                .expectError(UnauthorizedAccessException.class)
                .verify();
    }
    
    @Test
    void shouldDeleteTemplate() {
        CreateTemplateCommand command = CreateTemplateCommand.builder()
                .userId("user999")
                .name("To Delete")
                .workIds(new ArrayList<>())
                .build();
        
        StepVerifier.create(
                createTemplateUseCase.create(command)
                        .flatMap(created -> deleteTemplateUseCase.delete(created.getId(), "user999")
                                .thenReturn(created.getId()))
                        .flatMap(id -> findTemplateUseCase.findById(id, "user999"))
        )
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}
