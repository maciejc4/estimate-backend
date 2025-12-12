package com.estimate.application.usecase.work;

import com.estimate.domain.exception.UnauthorizedAccessException;
import com.estimate.domain.exception.WorkNotFoundException;
import com.estimate.domain.model.Work;
import com.estimate.domain.port.in.work.UpdateWorkCommand;
import com.estimate.domain.port.in.work.UpdateWorkUseCase;
import com.estimate.domain.port.out.WorkRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateWorkService implements UpdateWorkUseCase {
    
    private final WorkRepositoryPort workRepository;
    
    @Override
    public Mono<Work> update(UpdateWorkCommand command) {
        return workRepository.findById(command.getWorkId())
                .switchIfEmpty(Mono.error(new WorkNotFoundException(command.getWorkId())))
                .flatMap(work -> {
                    if (!work.getUserId().equals(command.getUserId())) {
                        return Mono.error(new UnauthorizedAccessException("Not authorized to update this work"));
                    }
                    
                    work.setName(command.getName());
                    work.setUnit(command.getUnit());
                    work.setMaterials(command.getMaterials());
                    
                    return workRepository.save(work);
                })
                .doOnNext(saved -> log.info("Work updated: {} for user: {}", saved.getName(), saved.getUserId()));
    }
}
