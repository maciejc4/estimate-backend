package com.estimate.application.usecase.work;

import com.estimate.domain.exception.UnauthorizedAccessException;
import com.estimate.domain.exception.WorkNotFoundException;
import com.estimate.domain.model.Work;
import com.estimate.domain.port.in.work.FindWorkUseCase;
import com.estimate.domain.port.out.WorkRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FindWorkService implements FindWorkUseCase {
    
    private final WorkRepositoryPort workRepository;
    
    @Override
    public Mono<Work> findById(String workId, String userId) {
        return workRepository.findById(workId)
                .switchIfEmpty(Mono.error(new WorkNotFoundException(workId)))
                .flatMap(work -> {
                    if (!work.getUserId().equals(userId)) {
                        return Mono.error(new UnauthorizedAccessException("Not authorized to view this work"));
                    }
                    return Mono.just(work);
                });
    }
    
    @Override
    public Flux<Work> findByUserId(String userId) {
        return workRepository.findByUserId(userId);
    }
    
    @Override
    public Flux<Work> findAll() {
        return workRepository.findAll();
    }
}
