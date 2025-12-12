package com.estimate.application.usecase.estimate;

import com.estimate.domain.exception.ResourceNotFoundException;
import com.estimate.domain.exception.UnauthorizedAccessException;
import com.estimate.domain.model.Estimate;
import com.estimate.domain.model.EstimateWorkItem;
import com.estimate.domain.port.in.estimate.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

@SpringBootTest
class EstimateUseCaseIntegrationTest {
    
    @Autowired
    private CreateEstimateUseCase createEstimateUseCase;
    
    @Autowired
    private UpdateEstimateUseCase updateEstimateUseCase;
    
    @Autowired
    private DeleteEstimateUseCase deleteEstimateUseCase;
    
    @Autowired
    private FindEstimateUseCase findEstimateUseCase;
    
    @Test
    void shouldCreateEstimate() {
        CreateEstimateCommand command = CreateEstimateCommand.builder()
                .userId("user123")
                .investorName("John Doe")
                .investorAddress("123 Main St")
                .workItems(new ArrayList<>())
                .materialDiscount(BigDecimal.ZERO)
                .laborDiscount(BigDecimal.ZERO)
                .validUntil(LocalDate.now().plusDays(30))
                .build();
        
        StepVerifier.create(createEstimateUseCase.create(command))
                .expectNextMatches(estimate -> 
                        estimate.getId() != null && 
                        estimate.getInvestorName().equals("John Doe"))
                .verifyComplete();
    }
    
    @Test
    void shouldFindEstimatesByUserId() {
        CreateEstimateCommand command = CreateEstimateCommand.builder()
                .userId("user456")
                .investorName("Jane Smith")
                .investorAddress("456 Oak Ave")
                .workItems(new ArrayList<>())
                .build();
        
        StepVerifier.create(
                createEstimateUseCase.create(command)
                        .thenMany(findEstimateUseCase.findByUserId("user456"))
        )
                .expectNextMatches(estimate -> estimate.getInvestorName().equals("Jane Smith"))
                .verifyComplete();
    }
    
    @Test
    void shouldUpdateEstimate() {
        CreateEstimateCommand createCommand = CreateEstimateCommand.builder()
                .userId("user789")
                .investorName("Old Name")
                .investorAddress("Old Address")
                .workItems(new ArrayList<>())
                .build();
        
        StepVerifier.create(
                createEstimateUseCase.create(createCommand)
                        .flatMap(created -> {
                            UpdateEstimateCommand updateCommand = UpdateEstimateCommand.builder()
                                    .estimateId(created.getId())
                                    .userId("user789")
                                    .investorName("New Name")
                                    .investorAddress("New Address")
                                    .workItems(new ArrayList<>())
                                    .build();
                            return updateEstimateUseCase.update(updateCommand);
                        })
        )
                .expectNextMatches(updated -> updated.getInvestorName().equals("New Name"))
                .verifyComplete();
    }
    
    @Test
    void shouldNotUpdateEstimateFromDifferentUser() {
        CreateEstimateCommand createCommand = CreateEstimateCommand.builder()
                .userId("user1")
                .investorName("Test")
                .investorAddress("Test Address")
                .workItems(new ArrayList<>())
                .build();
        
        StepVerifier.create(
                createEstimateUseCase.create(createCommand)
                        .flatMap(created -> {
                            UpdateEstimateCommand updateCommand = UpdateEstimateCommand.builder()
                                    .estimateId(created.getId())
                                    .userId("user2")
                                    .investorName("Hacked")
                                    .investorAddress("Hacked Address")
                                    .workItems(new ArrayList<>())
                                    .build();
                            return updateEstimateUseCase.update(updateCommand);
                        })
        )
                .expectError(UnauthorizedAccessException.class)
                .verify();
    }
    
    @Test
    void shouldDeleteEstimate() {
        CreateEstimateCommand command = CreateEstimateCommand.builder()
                .userId("user999")
                .investorName("To Delete")
                .investorAddress("Delete Address")
                .workItems(new ArrayList<>())
                .build();
        
        StepVerifier.create(
                createEstimateUseCase.create(command)
                        .flatMap(created -> deleteEstimateUseCase.delete(created.getId(), "user999")
                                .thenReturn(created.getId()))
                        .flatMap(id -> findEstimateUseCase.findById(id, "user999"))
        )
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}
