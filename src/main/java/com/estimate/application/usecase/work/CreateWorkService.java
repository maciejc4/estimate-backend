package com.estimate.application.usecase.work;

import com.estimate.domain.model.Work;
import com.estimate.domain.port.in.work.CreateWorkCommand;
import com.estimate.domain.port.in.work.CreateWorkUseCase;
import com.estimate.domain.port.out.WorkRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateWorkService implements CreateWorkUseCase {
    
    private final WorkRepositoryPort workRepository;
    
    @Override
    public Mono<Work> create(CreateWorkCommand command) {
        Work work = Work.builder()
                .userId(command.getUserId())
                .name(command.getName())
                .unit(command.getUnit())
                .materials(command.getMaterials())
                .build();
        
        return workRepository.save(work)
                .doOnNext(saved -> log.info("Work created: {} for user: {}", saved.getName(), saved.getUserId()));
    }
}
